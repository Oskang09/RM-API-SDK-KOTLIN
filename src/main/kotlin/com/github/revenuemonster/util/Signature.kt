package com.github.revenuemonster.util

import sun.security.util.DerInputStream
import java.nio.charset.StandardCharsets
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.Signature
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.RSAPrivateCrtKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*

object Signature {

    private val algoSignTypeMapper = mapOf(
        "sha256" to "SHA256WithRSA",
    )

    private fun getSignByteArray(compactData: String, signType: String, requestUrl: String, nonceStr: String, method: String, timestamp: String): ByteArray?
    {
        val encodedData: String = Base64.getEncoder().encodeToString(compactData.encodeToByteArray())
        val plainText: String = if (compactData != "") {
            ("data=" + encodedData + "&method=" + method.toLowerCase() + "&nonceStr="
                    + nonceStr + "&requestUrl=" + requestUrl + "&signType=" + signType
                    + "&timestamp=" + timestamp)
        } else {
            ("method=" + method.toLowerCase() + "&nonceStr="
                    + nonceStr + "&requestUrl=" + requestUrl + "&signType=" + signType
                    + "&timestamp=" + timestamp)
        }
        return plainText.toByteArray(StandardCharsets.UTF_8)
    }

    fun verifySignature(signature: String, publicKey: PublicKey, compactData: String, requestUrl: String, nonceStr: String, signType: String, method: String, timestamp: String): Boolean
    {
        try {
            val plainTextByte = getSignByteArray(compactData, signType, requestUrl, nonceStr, method, timestamp)
            val sig = Signature.getInstance(algoSignTypeMapper[signType])
            sig.initVerify(publicKey)
            sig.update(plainTextByte)
            return sig.verify(Base64.getDecoder().decode(signature))
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return false
    }

    fun generateSignature(privateKey: PrivateKey, compactData: String, requestUrl: String, nonceStr: String, signType: String, method: String, timestamp: String): String {
        var result = ""
        try {
            val plainTextByte = getSignByteArray(compactData, signType, requestUrl, nonceStr, method, timestamp)
            val sig = Signature.getInstance(algoSignTypeMapper[signType])
            sig.initSign(privateKey)
            sig.update(plainTextByte)
            val signatureBytes = sig.sign()
            result = String(Base64.getEncoder().encode(signatureBytes))
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return result
    }

    fun readPublicKey(key: String): PublicKey {
        val content = key
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")
            .replace("-----BEGIN RSA PUBLIC KEY-----", "")
            .replace("-----END RSA PUBLIC KEY-----", "")
            .replace("\\n".toRegex(), "")
            .replace("\\s".toRegex(), "");

        val bytes = Base64.getDecoder().decode(content);
        val keySpec = X509EncodedKeySpec(bytes);
        val keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    fun readPCKS1Key(key: String): PrivateKey {
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


    fun readPKCS8Key(key: String): PrivateKey {
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