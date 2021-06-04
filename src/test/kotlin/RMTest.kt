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
            "1616686697740585784", "uZsYdmzTsJzCQbAjcPMKGfFrYVclGxQX",
            "-----BEGIN RSA PRIVATE KEY-----\n" +
                    "MIIEoQIBAAKCAQB0ANraopd5Kcc9VQl6zHyrzL2g5RH1lchWoD0y6yWHXG7VJeF6\n" +
                    "C6FR9eRvV4NIA8LH7ttalA7H8jqPz8FCJo2/I2CbAejjOcZVhniG3JbsX9/rfnNm\n" +
                    "OsUPHazFiYeK1bMYDNuVtlqd/SBIwVyRi/ylp1B4A0Sn7e0AY/t3TP5l9C/klZ1o\n" +
                    "Eu9tpX6o24NGPjPdPD7/pp38HO3AngSXew71SagcdbXxv6cbYTXAF0UcCnBwcVO+\n" +
                    "PAu6+DSj0r9xMo3xlBUphNUCj+qSBJgGgi1AdM1y/Qo01oi1+MN39Ea9adNN+QFt\n" +
                    "BFYQCRZbJl/OlcXxISlcDGeq4+U55W7V/sGlAgMBAAECggEAAgSRygPSBrWHVbXI\n" +
                    "+G3eLU7ebZIOgesdFQSsi9ozSOt+sg56oZjaMYbJdnZbPkFyfe/VuPmiWDAKfL3s\n" +
                    "aq4pAQ4ofAnId0tl+87fAdmMdogkaQBGGZ0kGGM3wifmR6/38Y8nsq79XIouqZVT\n" +
                    "euSofGkwqSXFZ/ZnjP4wPZ8FPdi/i67hNEtlnKdJRsM3Rp3O5zzIzf2ZrzdD0LlT\n" +
                    "D+G90ZUSmFScrQZwhareKCLjvGHbqR7EMgJDejW1rJbjpsNodVJXdsDKUuNtWRQ9\n" +
                    "wP5XsiBvatD8nAtLxpatSK33Z/DLhjL7XzSpi8bOQZuIj6UVHvINiLTfTAKa7YCX\n" +
                    "+3CKAQKBgQC1dmJpVYAkAgcwSbOykQptU+lEemxl2NqFjW8YODz41HscMKDCVPWS\n" +
                    "lLF5v2Nkx4cjHVCPkD3MZ02VosXKAS+fFId5CizOVezS7cM3Gqqktf/GF41ofiN3\n" +
                    "r9gTYARJKwx+yAIgyXHVpP7gzJOFalEnkpucrplbCyWFCOCKcwqZIQKBgQCjpySh\n" +
                    "2A1tF+E9vkJODPykKxQXihOq7gy6105o9RGwxzsxyBSARPW/KzPQ4tNX9dbUSn+v\n" +
                    "WI6H+nk+YCNcXVwIv604K9y5f1TkU/RkPKmRav03IduM0pD5PxCMTuYXMU8UE1ML\n" +
                    "Q3PK37ua90Q3/mWuiAbKCW2ouHenIPVJ4X1EBQKBgA2M/6BaEC2gMSU7+71T83Fi\n" +
                    "mMLSWZHpdbgPbcJjQLpcM61RPFAGxCfkDrTGxAdclwzaPY/a96Jx/Gs2Mor5N7Mr\n" +
                    "d0pkph/qbrr5omBVD3UpWiZSz+6DrOZdLUeVHfzQyCgXi4EjSeroXVgwLrwBynmo\n" +
                    "CxLSPwV7eZvLo+jy2lHBAoGAaSIZYHehuHHc24N8qROiwfyCvdSQagDf4LAsyTSX\n" +
                    "FtAG8SYuNXEXxqYEda8iQqHGTz9E4+qqNiTs+utcDBxV4bDxoOJcvDZW3RAqMrLd\n" +
                    "5HOtFFwF5WPoipa/FMQjAMdGnAkGEnhUzQIKTbWH98jQndz5L5X7AqbvB0kfC0V2\n" +
                    "6dkCgYBeBg/VBhF6kjHrewz4E7vDkQmcSc765NZlCjGLP2RzfUJh2Ncfjv2hNWnT\n" +
                    "6UManxCW+Jz7ngsQMYxU6nIP3q5PuGeE+px+VAOQ9HTX0EvJy4oDBLdk63UWpA/M\n" +
                    "ILK1qftMsnMslSu0EpjxvqwifiOoNfOSIcC1+P8k+yF3mX6gCg==\n" +
                    "-----END RSA PRIVATE KEY-----",
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