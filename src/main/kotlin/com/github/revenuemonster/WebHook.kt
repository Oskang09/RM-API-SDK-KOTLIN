package com.github.revenuemonster

import com.github.revenuemonster.model.result.OnlinePaymentNotifyResponse

class WebHook(private val openapi: RevenueMonsterOpenAPI) {

    fun readOnlinePaymentFromJSON(json: String): OnlinePaymentNotifyResponse {
        return openapi.readJSON(json)
    }
}