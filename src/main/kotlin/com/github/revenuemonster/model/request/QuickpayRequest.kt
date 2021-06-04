package com.github.revenuemonster.model.request

data class QuickpayRequest(
    val authCode: String,
    val ipAddress: String,
    val storeId: String,
    val order: QuickpayRequestOrder,
    val extraInfo: QuickpayRequestExtraInfo
)

data class QuickpayRequestExtraInfo(
    val type: String,
    val reference: String
)

data class QuickpayRequestOrder(
    val id: String,
    val title: String,
    val detail: String,
    val amount: Int,
    val currencyType: String = "MYR",
    val additionalData: String,
)
