package com.github.revenuemonster.model.transaction

import java.time.Instant

data class Expiry(
    val type: ExpiryType,
    val day: Int,
    val expiredAt: Instant,
)