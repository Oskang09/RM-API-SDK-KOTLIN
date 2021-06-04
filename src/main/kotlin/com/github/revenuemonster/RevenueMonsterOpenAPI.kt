package com.github.revenuemonster

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.github.revenuemonster.model.ApiErrorException
import com.github.revenuemonster.model.ApiResult
import com.github.revenuemonster.model.Environment
import com.github.revenuemonster.service.OAuthService
import com.github.revenuemonster.service.PaymentService
import com.github.revenuemonster.util.Random
import com.github.revenuemonster.util.Signature
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*

class RevenueMonsterOpenAPI(
    private val environment: Environment,
    private val clientId: String,
    private val clientSecret: String,
    private val privateKey: String,
    private val logLevel: HttpLoggingInterceptor.Level = HttpLoggingInterceptor.Level.BASIC,
) {
    internal val oauthService: OAuthService
    internal val base64EncodedClient: String
    internal var accessToken: String = ""
    internal var refreshToken: String = ""

    val oauth: OAuthAPI = OAuthAPI(this)
    val webhook: WebHook = WebHook(this)
    lateinit var payment: PaymentService

    private val mapper: ObjectMapper = ObjectMapper().registerModule(KotlinModule())
    private val client: OkHttpClient.Builder = OkHttpClient.Builder()

    init {
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
        mapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true)
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
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
    fun <T> getResponseFromCall(api: Call<T>): T {
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

    fun setupPayment() {
        client.addInterceptor {
            val original = it.request()

            val unix = Instant.now().epochSecond.toString()
            val nonce = Random.generateNonce(32)
            val body = okio.Buffer()
            original.body?.writeTo(body)
            val json = body.readUtf8()
            val method = original.method
            val signature = Signature.generateSignature(json, privateKey, original.url.toString(), nonce, "sha256", method, unix)

            val request = original.newBuilder()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer $accessToken")
                .header("X-Signature", "sha256 $signature")
                .header("X-Nonce-Str", nonce)
                .header("X-Timestamp", unix)
                .method(original.method, original.body)
                .build()

            return@addInterceptor it.proceed(request)
        }

        val openApiRetrofit = Retrofit.Builder()
            .baseUrl(getOpenAPIURL())
            .client(client.build())
            .addConverterFactory(JacksonConverterFactory.create(mapper)).
            build()

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