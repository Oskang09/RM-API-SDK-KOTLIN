package com.github.revenuemonster.model.request

data class OAuthRequest(
    val grantType: OAuthRequestGrantType,
    val refreshToken: String? = null
)