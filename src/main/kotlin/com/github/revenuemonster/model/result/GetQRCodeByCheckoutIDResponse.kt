package com.github.revenuemonster.model.result

class GetQRCodeByCheckoutIDResponse(
    val qrcode: GetQRCodeByCheckoutIDResponseQRCode,
    val type: String
)

class GetQRCodeByCheckoutIDResponseQRCode(
    val base64Image: String,
    val data: String,
)