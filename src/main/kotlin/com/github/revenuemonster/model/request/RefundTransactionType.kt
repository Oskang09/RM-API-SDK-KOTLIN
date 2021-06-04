package com.github.revenuemonster.model.request

import com.fasterxml.jackson.annotation.JsonValue

enum class RefundTransactionType(@JsonValue val id: String) {
    FULL_REFUND("FULL"),
    PARTIAL_REFUND("PARTIAL")
}