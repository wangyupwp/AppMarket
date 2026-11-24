import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault
import java.security.KeyStore
import java.security.MessageDigest

@DisableCachingByDefault(because = "The release certificate must be checked for each release build")
abstract class VerifyReleaseCertificateTask : DefaultTask() {
    @get:InputFile @get:Optional @get:PathSensitive(PathSensitivity.NONE)
    abstract val keystoreFile: RegularFileProperty

    @get:Internal abstract val storePassword: Property<String>
    @get:Input @get:Optional abstract val keyAlias: Property<String>
    @get:Input abstract val trustedSha256: Property<String>

    @TaskAction
    fun verify() {
        if (!keystoreFile.isPresent || !storePassword.isPresent || !keyAlias.isPresent) {
            throw GradleException("Release signing requires KEYSTORE_PATH, KEYSTORE_PASS and KEY_ALIAS. Debug signing is not permitted for release.")
        }
        val password = storePassword.get().toCharArray()
        try {
            val store = KeyStore.getInstance(keystoreFile.get().asFile, password)
            if (!store.isKeyEntry(keyAlias.get())) throw GradleException("The release signing alias is not a private key entry.")
            val certificate = store.getCertificate(keyAlias.get())
                ?: throw GradleException("The release signing certificate was not found.")
            val digest = MessageDigest.getInstance("SHA-256").digest(certificate.encoded)
                .joinToString("") { "%02X".format(it.toInt() and 0xff) }
            if (digest != trustedSha256.get()) {
                throw GradleException("Release signing certificate SHA-256 is not trusted. Use the official release keystore.")
            }
            logger.lifecycle("Release signing certificate SHA-256 verified.")
        } finally {
            password.fill('\u0000')
        }
    }
}
