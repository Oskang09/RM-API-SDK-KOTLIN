import com.github.revenuemonster.RevenueMonsterOpenAPI
import com.github.revenuemonster.model.Environment
import com.github.revenuemonster.model.request.*
import com.github.revenuemonster.util.CallbackWrapper
import com.github.revenuemonster.util.Random
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runners.MethodSorters
import kotlin.properties.Delegates

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class RevenueMonsterOpenAPITest {

    private var api: RevenueMonsterOpenAPI by Delegates.notNull()

    @Before
    fun setupAuthenticate() {
        api = RevenueMonsterOpenAPI(
            Environment.SANDBOX,
            "", "","",
            HttpLoggingInterceptor.Level.BODY
        )
        val authCall = api.oauth.authenticate()
        val result = api.getResponseFromCall(authCall)
        api.oauth.usePreviousToken(result.accessToken, result.refreshToken)
        api.oauth.useAuthenticateAutoRefresh(result.accessToken, result.refreshToken) {
            println("onRefresh trigger $it")
        }
    }

    @Test
    fun payment() {
        api.setupPayment()
        val onlineTransaction = api.getResponseFromCall(api.payment.createOnlinePayment(OnlinePaymentRequest(
            order = OnlinePaymentRequestOrder(
                id = Random.generateNonce(10),
                title = "some title",
                detail = "some detail",
                amount = 100,
                additionalData = "some additional data"
            ),
            customer = OnlinePaymentRequestCustomer(
                userId = "userid",
                email = "someemail@email.com",
                countryCode = "60",
                phoneNumber = "187824152",
            ),
            type = "WEB_PAYMENT",
            storeId = "1607765286955983901",
            redirectUrl = "https://oskatb.ap.ngrok.io",
            notifyUrl = "https://oskatb.ap.ngrok.io/transaction-test"
        )))

        api.getResponseFromCall(api.payment.getOnlineTransactionByCheckoutID(
            checkoutId = onlineTransaction.item!!.checkoutId
        ))

        api.getResponseFromCall(api.payment.getQRCodeByCheckoutID(GetQRCodeByCheckoutIDRequest(
            checkoutId = onlineTransaction.item!!.checkoutId,
            method = "TNG_MY"
        )))

        api.getResponseFromCall(api.payment.getURLByCheckoutID(GetURLByCheckoutIDRequest(
            checkoutId = onlineTransaction.item!!.checkoutId,
            method = "TNG_MY"
        )))
    }
}