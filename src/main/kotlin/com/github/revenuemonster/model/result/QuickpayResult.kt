package com.github.revenuemonster.model.result

import com.github.revenuemonster.model.transaction.Order
import com.github.revenuemonster.model.transaction.Payee
import com.github.revenuemonster.model.transaction.Store
import java.time.Instant

data class QuickpayResult(
    val store: Store,
    val referenceId: String,
    val transactionId: String,
    val order: Order,
    val terminalId: String,
    val payee: Payee,
    val currencyType: String,
    val balanceAmount: Long,
    val platform: String,
    val method: String,
    val transactionAt: String,
    val type: String,
    val status: String,
    val region: String,
    val createdAt: Instant,
    val updatedAt: Instant
)