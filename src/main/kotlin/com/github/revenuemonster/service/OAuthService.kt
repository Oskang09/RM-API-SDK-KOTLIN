package com.github.revenuemonster.service

import com.github.revenuemonster.model.request.OAuthRequest
import com.github.revenuemonster.model.request.OAuthRequestGrantType
import com.github.revenuemonster.model.result.OAuthResult
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface OAuthService  {

    @Headers("Content-Type: application/json")
    @POST("v1/token")
    fun getToken(
        @Header("Authorization") base64EncodedClient: String,
        @Body request: OAuthRequest
    ): Call<OAuthResult>

}