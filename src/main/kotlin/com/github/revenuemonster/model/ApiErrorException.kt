package com.github.revenuemonster.model

class ApiErrorException(error: ErrorResult) : Exception("code: ${error.code}, error: ${error.message}") {
    constructor(message: String): this(ErrorResult(
        code = "sdk-error",
        message= message,
    ))
}