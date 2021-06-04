package com.github.revenuemonster.model.result

data class OAuthResult(
    val accessToken: String,
    val tokenType: String,
    val expiresIn: Int,
    val refreshToken: String,
    val refreshTokenExpiresIn: Int,
)