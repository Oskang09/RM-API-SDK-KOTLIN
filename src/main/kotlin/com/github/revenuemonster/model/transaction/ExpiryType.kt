package com.github.revenuemonster.model.transaction

import com.fasterxml.jackson.annotation.JsonValue

enum class ExpiryType(@JsonValue val id: String) {
    PERMANENT("PERMANENT"),
    DYNAMIC("DYNAMIC"),
    FIX("FIX")
}