import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties
import java.io.FileInputStream
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    signingConfigs {
        create("Release") {
            val propertiesFile = rootProject.file("signing.properties")
            if (propertiesFile.exists()) {
                val properties = Properties()
                properties.load(FileInputStream(propertiesFile))

                storeFile = file(properties["KEYSTORE_FILE"] as String)
                storePassword = properties["KEYSTORE_PASSWORD"] as String
                keyAlias = properties["KEY_ALIAS"] as String
                keyPassword = properties["KEY_PASSWORD"] as String
            } else {
                println("Warning: signing.properties file not found!")
            }
        }
    }
    namespace = "com.immichframe.immichframe"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.immichframe.immichframe"
        minSdk = 23
        targetSdk = 36
        versionCode = 50
        versionName = "1.0.50.0"

        // Load secret default values from a gitignored secrets.properties file
        // (local development) or from environment variables (CI / GitHub Actions).
        // Environment variables take precedence over the properties file.
        val secretsProps = Properties().apply {
            val secretsFile = rootProject.file("secrets.properties")
            if (secretsFile.exists()) {
                load(FileInputStream(secretsFile))
            }
        }
        fun secret(key: String): String =
            System.getenv(key) ?: secretsProps.getProperty(key) ?: ""

        // Generated at build time into a string resource so it can be referenced
        // from settings_view.xml via android:defaultValue="@string/...".
        resValue("string", "default_webview_url", secret("DEFAULT_WEBVIEW_URL"))
        resValue("string", "default_auth_secret", secret("DEFAULT_AUTH_SECRET"))
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("Release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.retrofit)
    implementation(libs.retrofitgson)
    implementation(libs.nanohttpd)
    implementation(libs.androidx.preference)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}