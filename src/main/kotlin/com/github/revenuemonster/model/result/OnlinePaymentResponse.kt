package com.github.revenuemonster.model.result

data class OnlinePaymentResponse(
    val checkoutId: String,
    val url: String
)