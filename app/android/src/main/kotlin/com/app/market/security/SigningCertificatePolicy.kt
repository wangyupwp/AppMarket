package com.app.market.security

import java.security.MessageDigest

/** 只接受所有当前签名者均在白名单内的 APK；不以历史证书代替当前签名者。 */
internal object SigningCertificatePolicy {
    fun matches(certificates: List<ByteArray>, trustedSha256: Set<String>): Boolean {
        if (certificates.isEmpty() || certificates.any { it.isEmpty() } || trustedSha256.isEmpty()) return false
        val trusted = trustedSha256.map { decodeDigest(it) ?: return false }
        return certificates.all { certificate ->
            val actual = MessageDigest.getInstance("SHA-256").digest(certificate)
            trusted.any { expected -> MessageDigest.isEqual(expected, actual) }
        }
    }

    private fun decodeDigest(value: String): ByteArray? {
        val hex = value.trim().replace(":", "")
        if (hex.length != 64 || hex.any { it.digitToIntOrNull(16) == null }) return null
        return ByteArray(32) { index ->
            ((hex[index * 2].digitToInt(16) shl 4) or hex[index * 2 + 1].digitToInt(16)).toByte()
        }
    }
}
