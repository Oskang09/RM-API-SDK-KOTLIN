package com.github.revenuemonster.model.request

data class GetQRCodeByCheckoutIDRequest(
    val checkoutId: String,
    val method: String,
    val type: String = "QRCODE"
)