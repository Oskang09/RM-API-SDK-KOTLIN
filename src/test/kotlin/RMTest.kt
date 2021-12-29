import com.github.revenuemonster.RevenueMonsterOpenAPI
import com.github.revenuemonster.model.Environment
import com.github.revenuemonster.model.request.*
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
                    "MIIEogIBAAKCAQEAkQvyXSnIGbQLs2wATKY5aljkvHy4FO4G0/UmK+TA+aW1mGq/\n" +
                    "7ZX8pNLunjTSzLi9sHPik8nbn7I1778n2zHjB9YHOb6m/2QOsQkt0SQH2a2mc+i+\n" +
                    "bHryEShVDLV7trDqyYWh2lYqs5tGxvk1wnZOKM3P3Ogvnb1J0TPPOLSyxAy3saxn\n" +
                    "3heo5txoRCuXXJfGHUOBrkqX0AGBQDbYvz0UHZc/0dGDPGagTnflpOq2l6X9Odfb\n" +
                    "W80pvGY6ehEJTJ84yRHvY6hnHX8aaMehDQ7XakkP/b9nlvYTnz11ddlG5veb0Dhn\n" +
                    "aaizH6WrI6gWhvLW36gVI5mAJO0j+A7VRMejZQIDAQABAoIBACaFUreVShQxcc4T\n" +
                    "x7yThLoOo6i/QrAkSuHtwFZ16R4j+SdialhxtegcMM0JYFaIVbBQBoOTX2V5EcP7\n" +
                    "M12OUSoamE6oqHc5HoG721QoyoDwEj3EORZcTH9sA7JdXF63e5NLjyGKMssCWPis\n" +
                    "5K8hdj518ldLvqGYzMrnhIgdzUAtasAY8j9udj3CLgmnm548ApP+fXrPYR2KZTD7\n" +
                    "lGsWeaTqCC/lcItXvA7h7ue9wfAAJGDIkQzGfJSg+DA706V8VAZD+34/jIRHaL4v\n" +
                    "XLlyuz5ntWQ3cum/9bCRvjSjbdkgBHJZStoi3cXP22w+rVjhsB5qtKlRHhNubYWd\n" +
                    "I/x+oP0CgYEA+Nf8vWhzrCaX+JrEQ5xBM5bc50y9lR117a7Hlh+u5GHMFyYz2Q6s\n" +
                    "XF421Iyrsd2VLAAdkiIL4xpaN96QZ2pG2TDkZieTffqVuGRp+1NJ8R72y0vZaxtM\n" +
                    "HMQ4V3Ot3ciMAqIbhfRhqQauMV8iBCE58DxSETg97n2kSNWYwqrrBicCgYEAlTfL\n" +
                    "jEW1yhAGXqG+6WlqucRaBTf1HijDk0h4MnLqvFridJxpsYV1jnT7qS89iaK45kY7\n" +
                    "C8TQr2J4E1t5/GUpiMizamcHlpwne0M+JK1CobT1OdOVP/ZdCGUYo3tUA25von/4\n" +
                    "EATG5qtwRb4UeFVK3gRxjF54EW6e0As6FTl37ZMCgYBrXh3xx4CpVZmarYRjO6cy\n" +
                    "UDSOJCFklmqMnC4HizIV3lCF6HjUfa1GyWvU98EZJGc5re8UX1ZLrdIhawlZZZSe\n" +
                    "H6dcoFTWNWmmPsUvqHct71NC0j5EJWoIu20n5oStPduTQ0im2pPMr1I3gTmXGJL8\n" +
                    "IbqzLVYjcdiRiH+59q8jXwKBgEVYAIfXfXvBprQjho0CKE7cKNlz/71cREcVzoXj\n" +
                    "UaLmuYZnidatpsvlIW6mPXrQ3AxnZtk5RaG7qQGV6UKZ4Itoebhg2O3s84wc26w/\n" +
                    "uE9dwPh2k01+OA3GrYSLs6dEkyx5O9Z14dkgorRGeUzSllBG0F3jKPkw5yzXis6C\n" +
                    "PnFxAoGAQv7szr5+L7czbb7FlxmLDhrVihhdDQMEMB9JI3/2HZk92RT56EGkIcdd\n" +
                    "dL7i5MD7XwatBulD6QNjtk8Gp3yHXwwYn4rW+XSLJscKmw56U+tMAs0B+DpQNkeO\n" +
                    "rjvo91QbLZRSFhN0nBFMwmneP2Gn7PODTgLCfyyWeMGhMZuRn4g=\n" +
                    "-----END RSA PRIVATE KEY-----",
    "-----BEGIN RSA PUBLIC KEY-----\n" +
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsGNKTtMhTcwXbL3PrdIc\n" +
            "zPQRn0W1u8aa/vVYcZpJ1zWY7bdewRT5GOuts2eZMr4fqDXqX6YB0qUZ7phizKuJ\n" +
            "Gm9lBfJCIvbMuoBu9IwXtqic9RPLFwJQ5axQSVCEKU5zlENMAYOltRY+MPdcXlHj\n" +
            "2tHitn+7rCmeSBXRyZF8o3l5FSLMvlPBF2VU9SgHhlqXiomGqbTEfdVmnfbnNlDj\n" +
            "TfaSatQjtUPbhNcbLCG9vvJrcRaw1t/uXgZ8aV0DAcvrSONDXNPGYUDdRRMgQOAH\n" +
            "o6TN3VbgrvlyWgjHYuRenvIGRyxfr4GJDKVIRiOw6doMY95VZYAvAGgJ7uZgOW3S\n" +
            "6wIDAQAB\n" +
            "-----END RSA PUBLIC KEY-----",
            logLevel = HttpLoggingInterceptor.Level.BASIC
        )
        val authCall = api.oauth.authenticate()
        val result = api.ensureResponse(authCall)
        api.oauth.usePreviousToken(result.accessToken, result.refreshToken)
        api.oauth.useAuthenticateAutoRefresh(result.accessToken, result.refreshToken) {
            println("onRefresh trigger $it")
        }
    }

    @Test
    fun payment() {
        api.setupOpenApi()
        val onlineTransaction = api.ensureResponse(api.payment.createOnlinePayment(OnlinePaymentRequest(
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

        api.ensureResponse(api.payment.getOnlineTransactionByCheckoutID(
            checkoutId = onlineTransaction.item!!.checkoutId
        ))

        api.ensureResponse(api.payment.getQRCodeByCheckoutID(GetQRCodeByCheckoutIDRequest(
            checkoutId = onlineTransaction.item!!.checkoutId,
            method = "TNG_MY"
        )))

        api.ensureResponse(api.payment.getURLByCheckoutID(GetURLByCheckoutIDRequest(
            checkoutId = onlineTransaction.item!!.checkoutId,
            method = "TNG_MY"
        )))
    }
}