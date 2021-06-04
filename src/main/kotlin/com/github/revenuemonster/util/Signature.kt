package com.github.revenuemonster.util

import sun.security.util.DerInputStream
import java.nio.charset.StandardCharsets
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.Signature
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.RSAPrivateCrtKeySpec
import java.util.*

object Signature {

    fun generateSignature(compactData: String, privateKey: String, requestUrl: String, nonceStr: String, signType: String, method: String, timestamp: String): String {
        var result = ""
        try {
            val encodedData: String = encodeBase64(compactData)
            val plainText: String = if (compactData != "") {
                ("data=" + encodedData + "&method=" + method.lowercase() + "&nonceStr="
                        + nonceStr + "&requestUrl=" + requestUrl + "&signType=" + signType
                        + "&timestamp=" + timestamp)
            } else {
                ("method=" + method.lowercase() + "&nonceStr="
                        + nonceStr + "&requestUrl=" + requestUrl + "&signType=" + signType
                        + "&timestamp=" + timestamp)
            }
            val plainTextByte = plainText.toByteArray(StandardCharsets.UTF_8)
            var privKey: PrivateKey? = null
            if (privateKey.contains("-----BEGIN PRIVATE KEY-----")) {
                privKey = readPKCS8Key(privateKey)
            } else if (privateKey.contains("-----BEGIN RSA PRIVATE KEY-----")) {
                privKey = readPCKS1Key(privateKey)
            }
            val sig = Signature.getInstance("SHA256WithRSA")
            sig.initSign(privKey)
            sig.update(plainTextByte)
            val signatureBytes = sig.sign()
            result = String(Base64.getEncoder().encode(signatureBytes))
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return result
    }

    private fun encodeBase64(plainText: String): String {
        val encoded = Base64.getEncoder().encode(plainText.toByteArray())
        return String(encoded)
    }

    private fun decodeBase64(plainText: String): String {
        val decoded = Base64.getDecoder().decode(plainText.toByteArray())
        return String(decoded)
    }

    private fun readPCKS1Key(key: String): PrivateKey {
        val content = key
            .replace("-----BEGIN RSA PRIVATE KEY-----", "")
            .replace("-----END RSA PRIVATE KEY-----", "")
            .replace("\\n".toRegex(), "")
            .replace("\\s".toRegex(), "");
        val bytes = Base64.getDecoder().decode(content)
        val derReader = DerInputStream(bytes)
        val seq = derReader.getSequence(0)
        val modulus = seq[1].bigInteger
        val publicExp = seq[2].bigInteger
        val privateExp = seq[3].bigInteger
        val prime1 = seq[4].bigInteger
        val prime2 = seq[5].bigInteger
        val exp1 = seq[6].bigInteger
        val exp2 = seq[7].bigInteger
        val crtCoef = seq[8].bigInteger
        val keySpec =
            RSAPrivateCrtKeySpec(modulus, publicExp, privateExp, prime1, prime2, exp1, exp2, crtCoef)
        val keyFactory = KeyFactory.getInstance("RSA")
        return keyFactory.generatePrivate(keySpec)
    }


    private fun readPKCS8Key(key: String): PrivateKey {
        val content = key
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replace("\\n", "")
            .replace("\\s", "")
        val bytes = Base64.getDecoder().decode(content)
        val keySpec = PKCS8EncodedKeySpec(bytes)
        val keyFactory = KeyFactory.getInstance("RSA")
        return keyFactory.generatePrivate(keySpec)
    }
}