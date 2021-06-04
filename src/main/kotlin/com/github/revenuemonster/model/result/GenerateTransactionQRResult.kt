package com.github.revenuemonster.model.result

import com.github.revenuemonster.model.transaction.Expiry
import com.github.revenuemonster.model.transaction.Order
import com.github.revenuemonster.model.transaction.Store
import java.time.Instant

data class GenerateTransactionQRResult(
    val store: Store,
    val type: String,
    val isPreFillAmount: Boolean,
    val currencyType: String,
    val amount: Long,
    val platform: String,
    val method: List<String>,
    val expiry: Expiry?,
    val code: String,
    val status: String,
    val qrCodeUrl: String,
    val redirectUrl: String,
    val order: Order,
    val createdAt: Instant,
    val updatedAt: Instant
)