package com.github.revenuemonster.model.transaction

data class Transaction (
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
    val createdAt: String,
    val updatedAt: String
)