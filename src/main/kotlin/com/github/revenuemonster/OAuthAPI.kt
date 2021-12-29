package com.github.revenuemonster

import com.github.revenuemonster.model.ApiErrorException
import com.github.revenuemonster.model.ApiResult
import com.github.revenuemonster.model.request.OAuthRequest
import com.github.revenuemonster.model.request.OAuthRequestGrantType
import com.github.revenuemonster.model.result.OAuthResult
import com.github.revenuemonster.service.OAuthService
import retrofit2.Call
import java.util.*
import kotlin.Exception
import kotlin.concurrent.fixedRateTimer
import kotlin.jvm.Throws

class OAuthAPI(private val openapi: RevenueMonsterOpenAPI) {

    fun authenticate(): Call<OAuthResult> {
        return openapi.oauthService.getToken(
            openapi.base64EncodedClient,
            OAuthRequest(grantType = OAuthRequestGrantType.CLIENT_CREDENTIALS),
        )
    }

    fun usePreviousToken(accessToken: String, refreshToken: String) {
        openapi.accessToken = accessToken
        openapi.refreshToken = refreshToken
    }

    @Throws(ApiErrorException::class)
    fun useAuthenticateAutoRefresh(accessToken: String? = null, refreshToken: String? = null, onRefresh: (Pair<OAuthResult?, Exception?>) -> Unit): Timer {
        if (accessToken == null || refreshToken == null) {
            val body = openapi.ensureResponse(authenticate())
            openapi.accessToken = body.accessToken
            openapi.refreshToken = body.refreshToken
        } else {
            openapi.accessToken = accessToken
            openapi.refreshToken = refreshToken
        }

        val repeatAt: Long = 1000 * 60 * 60
        return fixedRateTimer("refresh-token", false, repeatAt, repeatAt) {
            try {
                val result = openapi.ensureResponse(openapi.oauthService.getToken(
                    openapi.base64EncodedClient,
                    OAuthRequest(
                        grantType = OAuthRequestGrantType.REFRESH_TOKEN,
                        refreshToken = openapi.refreshToken
                    )
                ))
                openapi.refreshToken = result.refreshToken
                openapi.accessToken = result.accessToken
                onRefresh(Pair(result, null))
            } catch (e: Exception) {
                onRefresh(Pair(null, e))
            }
        }
    }
}