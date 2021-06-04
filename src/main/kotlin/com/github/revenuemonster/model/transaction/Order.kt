package com.github.revenuemonster.model.transaction

data class Order(
    val id: String,
    val title: String,
    val detail: String,
    val amount: Int
)