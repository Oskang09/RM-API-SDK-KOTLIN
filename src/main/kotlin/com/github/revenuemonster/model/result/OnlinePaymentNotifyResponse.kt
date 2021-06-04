package com.github.revenuemonster.model.result

import com.github.revenuemonster.model.transaction.Transaction

class OnlinePaymentNotifyResponse(val data: Transaction, val eventType: String)