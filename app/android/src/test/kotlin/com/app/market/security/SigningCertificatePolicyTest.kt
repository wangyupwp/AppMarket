package com.app.market.security

import java.security.MessageDigest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SigningCertificatePolicyTest {
    private val official = "official certificate DER fixture".toByteArray()
    private val other = "untrusted certificate DER fixture".toByteArray()

    private fun digest(bytes: ByteArray): String = MessageDigest.getInstance("SHA-256").digest(bytes)
        .joinToString("") { "%02x".format(it.toInt() and 0xff) }

    @Test
    fun acceptsTheExactTrustedCertificate() {
        assertTrue(SigningCertificatePolicy.matches(listOf(official), setOf(digest(official))))
    }

    @Test
    fun rejectsResignedAndModifiedCertificates() {
        assertFalse(SigningCertificatePolicy.matches(listOf(other), setOf(digest(official))))
        val modified = official.copyOf().also { it[0] = (it[0].toInt() xor 1).toByte() }
        assertFalse(SigningCertificatePolicy.matches(listOf(modified), setOf(digest(official))))
    }

    @Test
    fun rejectsMissingCertificatesOrInvalidPins() {
        assertFalse(SigningCertificatePolicy.matches(emptyList(), setOf(digest(official))))
        assertFalse(SigningCertificatePolicy.matches(listOf(byteArrayOf()), setOf(digest(official))))
        assertFalse(SigningCertificatePolicy.matches(listOf(official), emptySet()))
        assertFalse(SigningCertificatePolicy.matches(listOf(official), setOf("not-a-sha256")))
        assertFalse(SigningCertificatePolicy.matches(listOf(official), setOf("z".repeat(64))))
    }

    @Test
    fun rejectsMixedTrustedAndUntrustedCurrentSigners() {
        assertFalse(SigningCertificatePolicy.matches(listOf(official, other), setOf(digest(official))))
        assertTrue(SigningCertificatePolicy.matches(listOf(official, other), setOf(digest(official), digest(other))))
    }

    @Test
    fun acceptsColonSeparatedUppercaseFingerprints() {
        val fingerprint = digest(official).uppercase().chunked(2).joinToString(":")
        assertTrue(SigningCertificatePolicy.matches(listOf(official), setOf(fingerprint)))
    }
}
