package com.github.revenuemonster.service

import com.github.revenuemonster.model.ApiResult
import com.github.revenuemonster.model.request.*
import com.github.revenuemonster.model.result.*
import com.github.revenuemonster.model.transaction.OnlineTransaction
import com.github.revenuemonster.model.transaction.Transaction
import retrofit2.Call
import retrofit2.http.*

interface PaymentService {

    @POST("v3/payment/quickpay")
    fun quickPay(@Body request: QuickpayRequest): Call<ApiResult<QuickpayResult>>

    @POST("v3/payment/online")
    fun createOnlinePayment(@Body request: OnlinePaymentRequest): Call<ApiResult<OnlinePaymentResponse>>

    @POST("v3/payment/online/checkout")
    fun getQRCodeByCheckoutID(@Body request: GetQRCodeByCheckoutIDRequest): Call<ApiResult<GetQRCodeByCheckoutIDResponse>>

    @POST("v3/payment/online/checkout")
    fun getURLByCheckoutID(@Body request: GetURLByCheckoutIDRequest): Call<ApiResult<GetURLByCheckoutIDResponse>>

    @GET("v3/payment/online")
    fun getOnlineTransactionByCheckoutID(@Query("checkoutId") checkoutId: String): Call<ApiResult<OnlineTransaction>>

    @POST("v3/payment/transaction/qrcode")
    fun createStaticTransactionQR(@Body request: CreateStaticTransactionQRRequest): Call<ApiResult<GenerateTransactionQRResult>>

    @POST("v3/payment/transaction/qrcode")
    fun createDynamicTransactionQR(@Body request: CreateDynamicTransactionQRRequest): Call<ApiResult<GenerateTransactionQRResult>>

    @POST("v3/payment/refund")
    fun refundTransaction(@Body request: RefundTransactionRequest): Call<ApiResult<Transaction>>

    @POST("v3/payment/reverse")
    fun reverseTransaction(@Body request: ReverseTransactionRequest): Call<ApiResult<Transaction>>

    @GET("v3/payment/transaction/order/{orderId}")
    fun queryStatusByOrderID(@Path("orderId")orderId: String): Call<ApiResult<Transaction>>

    @GET("v3/payment/transaction/{transactionId}")
    fun queryStatusByTransactionID(@Path("transactionId")transactionId: String): Call<ApiResult<Transaction>>
}