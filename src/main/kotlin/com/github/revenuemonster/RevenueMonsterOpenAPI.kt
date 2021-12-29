package com.github.revenuemonster

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.util.JSONPObject
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.github.revenuemonster.model.ApiErrorException
import com.github.revenuemonster.model.ApiResult
import com.github.revenuemonster.model.Environment
import com.github.revenuemonster.service.OAuthService
import com.github.revenuemonster.service.PaymentService
import com.github.revenuemonster.util.Random
import com.github.revenuemonster.util.Signature
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.io.Console
import java.security.PrivateKey
import java.security.PublicKey
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*
import kotlin.collections.HashMap

class RevenueMonsterOpenAPI(
    private val environment: Environment,
    private val clientId: String,
    private val clientSecret: String,
    private val privateKey: String,
    private val publicKey: String,
    private val signType: String = "sha256",
    private val logLevel: HttpLoggingInterceptor.Level = HttpLoggingInterceptor.Level.BASIC,
) {
    internal val oauthService: OAuthService
    internal val base64EncodedClient: String
    internal var accessToken: String = ""
    internal var refreshToken: String = ""
    private var privateKeyInstance: PrivateKey? = null
    private var publicKeyInstance: PublicKey? = null

    val oauth: OAuthAPI = OAuthAPI(this)
    val webhook: WebHook = WebHook(this)
    lateinit var payment: PaymentService

    private val mapper: ObjectMapper = ObjectMapper().registerModule(KotlinModule())
    private val client: OkHttpClient.Builder = OkHttpClient.Builder()

    init {
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
        mapper.enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        mapper.dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")

        val logInterceptor = HttpLoggingInterceptor()
        logInterceptor.level = logLevel
        client.addInterceptor(logInterceptor)

        val oAuthRetrofit = Retrofit.Builder()
            .baseUrl(getOAuthURL())
            .client(client.build())
            .addConverterFactory(JacksonConverterFactory.create(mapper))
            .build()

        oauthService = oAuthRetrofit.create(OAuthService::class.java)

        val id = "$clientId:$clientSecret"
        base64EncodedClient = String(Base64.getEncoder().encode(id.toByteArray()))
    }

    @Throws(ApiErrorException::class)
    fun <T> ensureResponse(api: Call<T>): T {
        val response = api.execute()
        if (!response.isSuccessful || response.code() >= 300) {
            val errorBody = response.errorBody()
            if (errorBody != null) {
                val err = readJSON<ApiResult<Any>>(errorBody.string())
                throw ApiErrorException(err.error!!)
            }
            throw ApiErrorException(response.errorBody()?.string() ?: response.message())
        }
        val body = response.body()
        return body!!
    }

    fun setupOpenApi() {
        client.addInterceptor {
            val original = it.request()

            val unix = Instant.now().epochSecond.toString()
            val nonce = Random.generateNonce(32)
            val body = okio.Buffer()
            original.body?.writeTo(body)
            val json = body.readUtf8()
            val method = original.method
            val requestUrl = original.url.toString()
            val signature = Signature.generateSignature(privateKeyInstance!!, json, requestUrl, nonce, signType, method, unix)

            val request = original.newBuilder()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer $accessToken")
                .header("X-Signature", "sha256 $signature")
                .header("X-Nonce-Str", nonce)
                .header("X-Timestamp", unix)
                .method(original.method, original.body)
                .build()

            val resp = it.proceed(request)
            val respXSignature = resp.header("X-Signature", "") ?: return@addInterceptor resp
            val respBody = resp.body!!
            val respNonce = resp.header("X-Nonce-Str","")!!
            val respUnix = resp.header("X-Timestamp", "")!!
            val respSignArray = respXSignature.split(" ")
            val respSignType = respSignArray[0]
            val respSignature = respSignArray[1]

            val respJson = mapper.readValue(respBody.byteStream(), HashMap::class.java)
            val respJsonBody = mapper.writeValueAsString(respJson)
            val isValidSignature = Signature.verifySignature(respSignature, publicKeyInstance!!, respJsonBody, requestUrl, respNonce, respSignType, method, respUnix)
            if (!isValidSignature) {
                throw Exception("fail to verify signature using provided public key")
            }

            return@addInterceptor resp.newBuilder().body(respJsonBody.toResponseBody(respBody.contentType())).build()
        }

        val openApiRetrofit = Retrofit.Builder()
            .baseUrl(getOpenAPIURL())
            .client(client.build())
            .addConverterFactory(JacksonConverterFactory.create(mapper)).
            build()

        publicKeyInstance = Signature.readPublicKey(publicKey)
        if (privateKey.contains("-----BEGIN PRIVATE KEY-----")) {
            privateKeyInstance = Signature.readPKCS8Key(privateKey)
        } else if (privateKey.contains("-----BEGIN RSA PRIVATE KEY-----")) {
            privateKeyInstance = Signature.readPCKS1Key(privateKey)
        }

        payment = openApiRetrofit.create(PaymentService::class.java)
    }

    private fun getOAuthURL(): String {
        return if (environment == Environment.SANDBOX) "https://sb-oauth.revenuemonster.my" else "https://oauth.revenuemonster.my"
    }

    private fun getOpenAPIURL(): String {
        return if (environment == Environment.SANDBOX) "https://sb-open.revenuemonster.my" else "https://open.revenuemonster.my"
    }

    internal inline fun <reified T> readJSON(json: String): T {
        return mapper.readValue(json, T::class.java)
    }
}