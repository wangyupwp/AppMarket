package com.app.market.security

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.app.market.BuildConfig

internal object ReleaseSignatureVerifier {
    fun enforce(context: Context) {
        // 此开关由构建类型固定，不能依据可被修改的 Manifest debuggable 标志绕过校验。
        if (!BuildConfig.ENFORCE_SIGNATURE_CHECK) return
        val certificates = runCatching { currentCertificates(context) }.getOrDefault(emptyList())
        if (!SigningCertificatePolicy.matches(certificates, setOf(BuildConfig.TRUSTED_CERTIFICATE_SHA256))) {
            throw SecurityException("App signing certificate verification failed. Install an official build.")
        }
    }

    @Suppress("DEPRECATION")
    private fun currentCertificates(context: Context): List<ByteArray> {
        val manager = context.packageManager
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
            PackageManager.GET_SIGNING_CERTIFICATES else PackageManager.GET_SIGNATURES
        val info = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            manager.getPackageInfo(context.packageName, PackageManager.PackageInfoFlags.of(flags.toLong()))
        } else {
            manager.getPackageInfo(context.packageName, flags)
        }
        val signers = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
            info.signingInfo?.apkContentsSigners else info.signatures
        return signers.orEmpty().map { it.toByteArray() }
    }
}
