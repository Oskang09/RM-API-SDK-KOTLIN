package com.github.revenuemonster.model.request

data class GetURLByCheckoutIDRequest(
    val checkoutId: String,
    val method: String,
    val type: String = "URL"
)