@file:Suppress("UnstableApiUsage")

import com.android.build.api.variant.impl.VariantOutputImpl
import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeCompiler)
}

dependencies {
    testImplementation("junit:junit:4.13.2")
    implementation(projects.app.shared)
    implementation(projects.domain)
    implementation(projects.data)
    implementation(libs.koin.core)
    implementation(libs.koin.compose.viewmodel)
    implementation(libs.ktor.client.core)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.core.ktx)
    implementation(libs.coil.singleton)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.hiddenapibypass)
    implementation(libs.rikka.shizuku.provider)
    implementation(libs.rikka.shizuku.api)
    implementation(libs.focus.api)
}

android {
    namespace = ProjectConfig.PACKAGE_NAME
    buildFeatures {
        buildConfig = true
    }
    compileSdk {
        version = release(ProjectConfig.Android.COMPILE_SDK) {
            minorApiLevel = ProjectConfig.Android.COMPILE_SDK_MINOR
        }
    }

    defaultConfig {
        applicationId = ProjectConfig.PACKAGE_NAME
        minSdk = ProjectConfig.Android.MIN_SDK
        targetSdk = ProjectConfig.Android.TARGET_SDK
        versionCode = resolveVersionCode()
        versionName = ProjectConfig.VERSION_NAME
        buildConfigField("String", "TRUSTED_CERTIFICATE_SHA256", "\"${ProjectConfig.RELEASE_CERTIFICATE_SHA256}\"")

        ndk {
            //noinspection ChromeOsAbiSupport
            abiFilters += listOf("arm64-v8a", "armeabi-v7a")
        }
    }

    val properties = Properties()
    runCatching { project.rootProject.file("local.properties").reader(Charsets.UTF_8).use(properties::load) }
    val keystorePath = properties.getProperty("KEYSTORE_PATH") ?: System.getenv("KEYSTORE_PATH")
    val keystorePwd = properties.getProperty("KEYSTORE_PASS") ?: System.getenv("KEYSTORE_PASS")
    val alias = properties.getProperty("KEY_ALIAS") ?: System.getenv("KEY_ALIAS")
    val pwd = properties.getProperty("KEY_PASSWORD") ?: System.getenv("KEY_PASSWORD")
    val verifyReleaseCertificate = tasks.register<VerifyReleaseCertificateTask>("verifyReleaseSigningCertificate") {
        group = "verification"
        description = "Verify the release keystore certificate against the pinned SHA-256."
        if (keystorePath != null) keystoreFile.set(file(keystorePath))
        if (keystorePwd != null) storePassword.set(keystorePwd)
        if (alias != null) keyAlias.set(alias)
        trustedSha256.set(ProjectConfig.RELEASE_CERTIFICATE_SHA256)
    }
    tasks.matching { it.name == "preReleaseBuild" }.configureEach {
        dependsOn(verifyReleaseCertificate)
    }
    if (keystorePath != null) {
        signingConfigs {
            register("github") {
                storeFile = file(keystorePath)
                storePassword = keystorePwd
                keyAlias = alias
                keyPassword = pwd
                enableV3Signing = true
                enableV4Signing = true
            }
        }
    } else {
        signingConfigs {
            register("release") {
                enableV3Signing = true
                enableV4Signing = true
            }
        }
    }

    buildTypes {
        release {
            isDebuggable = false
            isJniDebuggable = false
            buildConfigField("boolean", "ENFORCE_SIGNATURE_CHECK", "true")
            isMinifyEnabled = true
            isShrinkResources = true
            vcsInfo.include = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules-android.pro")
            signingConfig = signingConfigs.getByName(if (keystorePath != null) "github" else "release")
        }
        debug {
            buildConfigField("boolean", "ENFORCE_SIGNATURE_CHECK", "false")
            if (keystorePath != null) signingConfig = signingConfigs.getByName("github")
        }
    }

    androidResources {
        localeFilters.addAll(listOf("en", "zh"))
    }

    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }

    packaging {
        jniLibs {
            excludes += "lib/*/libandroidx.graphics.path.so"
        }
        resources {
            excludes += arrayOf(
                "/META-INF/*",
                "/META-INF/androidx/**",
                "/META-INF/versions/**",
                "/org/bouncycastle/**",
                "/org/apache/commons/**",
                "/kotlin/**",
                "/kotlinx/**",
                "/okhttp3/**",
                "/*.txt",
                "/*.bin",
                "/*.json",
            )
        }
    }
}

androidComponents {
    onVariants { variant ->
        variant.outputs.forEach { output ->
            (output as? VariantOutputImpl)?.outputFileName?.set(
                output.versionName.zip(output.versionCode) { versionName, versionCode ->
                    "${ProjectConfig.APP_NAME}-v${versionName}(${versionCode})${if (variant.buildType == "debug") "_debug" else ""}.apk"
                }
            )
        }
    }
}
