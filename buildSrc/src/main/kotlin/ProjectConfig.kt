object ProjectConfig {
    const val APP_NAME = "AppMarket"
    const val PACKAGE_NAME = "com.app.market"
    const val VERSION_NAME = "2.3.1"
    const val VERSION_CODE = 221
    // 正式应用签名证书的 SHA-256；仅在经过授权的密钥轮换时更新。
    const val RELEASE_CERTIFICATE_SHA256 = "4A5C8F131326A4A31A724C0946EA168FD9D17DE321DB47B8EA48CD5A24F6CA08"
    const val JVM_VERSION = 21

    object Android {
        const val MIN_SDK = 26
        const val TARGET_SDK = 37
        const val COMPILE_SDK = 37
        const val COMPILE_SDK_MINOR = 2
    }
}
