package com.github.revenuemonster.model

open class ApiResult<T>(
    val code: String?,
    val item: T?,
    val error: ErrorResult?
)