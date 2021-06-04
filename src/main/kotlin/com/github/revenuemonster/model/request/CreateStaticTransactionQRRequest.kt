package com.github.revenuemonster.model.request

import com.github.revenuemonster.model.transaction.Expiry
import com.github.revenuemonster.model.transaction.TransactionQROrder

class CreateStaticTransactionQRRequest(
    val amount: Int,
    val currencyType: String = "MYR",
    val method: List<String> = listOf(),
    val expiry: Expiry? = null,
    val order: TransactionQROrder,
    val redirectUrl: String,
    val type: String = "STATIC",
    val storeId: String,
    val isPreFillAmount: Boolean
)