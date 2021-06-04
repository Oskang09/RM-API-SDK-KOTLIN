package com.github.revenuemonster.model.request

data class RefundTransactionRequest(
    val transactionId: String,
    val refund: RefundTransactionRequestDetail,
    val reason: String
)

data class RefundTransactionRequestDetail(
    val type: RefundTransactionType = RefundTransactionType.FULL_REFUND,
    val currencyType: String = "MYR",
    val amount: Int,
)