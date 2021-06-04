# RM-API-SDK-KOTLIN

```bash
repository {
    maven { url = URI.create("https://jitpack.io") }
}

dependencies {
  implementation("com.github.Oskang09:RM-API-SDK-KOTLIN:$version")  
}
```

### Covered Functions

- [x] Client Credentials (Authentication)
- [x] Refresh Token (Authentication)
- [x] Payment (Transaction QR) - Create Transaction QR
- [x] Payment ( Web / Mobile ) - Create Transaction
- [x] Payment ( Web / Mobile ) - Notify Response Transformer
- [x] Payment ( Web / Mobile ) - Get QRCode & URL By Checkout ID
- [x] Payment ( Web / Mobile ) - Get Online Transaction By Checkout ID
- [x] Payment - Refund
- [x] Payment - Reverse
- [x] Payment - Query Status By Order ID
- [x] Payment - Query Status By Transaction ID


### Authenticating

```kotlin
class Example {
    private val api = RevenueMonsterOpenAPI(
        Environment.SANDBOX, // environment SANDBOX / PRODUCTION
        "clientId", // your client id
        "clientSecret", // your client secret
        'private key", // your client private key
    )
    
    fun authenticate() {
        val result = api.getResponseFromCall(authCall)
        
        // choose one of the way you like
        // 2. is recommended since everything handled automatically.
        
        // 1. set token to context
        api.oauth.usePreviousToken(result.accessToken, result.refreshToken)

        // 2. enable auto refresh with auto set token with not exists
        api.oauth.useAuthenticateAutoRefresh {
            println("onRefresh trigger")
        }
        
        // 3. enable auto refresh with token
        api.oauth.useAuthenticateAutoRefresh(result.accessToken, result.refreshToken) {
            println("onRefresh trigger")
        }
        
        // currently only payment supported
        api.setupPayment()
    }
}
```

### Calling API

```kotlin
class Example {

    private val api = RevenueMonsterOpenAPI(
        Environment.SANDBOX, // environment SANDBOX / PRODUCTION
        "clientId", // your client id
        "clientSecret", // your client secret
        'private key", // your client private key
    )
    
    fun apiCall() {
        val authCall = api.oauth.authenticate()

        // api.getResponseFromCall will handle api error for you by returning exception
        // Asynchronous Way: 
        authCall.enqueue {
            onResponse = {
                try {
                    val result = api.getResponseFromCall(it)
                } catch (e: ApiErrorException) {

                }
            }

            onFailure = {

            }
        }

        // Synchronous
        try {
            val result = api.getResponseFromCall(authCall)
        } catch (e: ApiErrorException) {

        }
    }
    
}
```