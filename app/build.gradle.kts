import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
}


private val localProperties = Properties()
private val localPropertiesFile = rootProject.file("local.properties")

if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
} else {
    error("ecreate local.properties file in root project directory")
}

private val envId: String = localProperties.getProperty("env_id")

android {
    namespace = "com.blummock.cryptowallet"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.blummock.cryptowallet"
        minSdk = 33
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
    defaultConfig {
        buildConfigField("String", "ENVIRONMENT_ID", "\"$envId\"")
        buildConfigField("String", "APP_LOGO_URL", "\"https://your-app.com/logo.png\"")
        buildConfigField("String", "APP_NAME", "\"Crypto Wallet\"")
        buildConfigField("String", "REDIRECT_URL", "\"yourappscheme://\"")
        buildConfigField("String", "APP_ORIGIN", "\"https://your-app.com\"")
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(libs.kotlinx.serialization.json)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Core SDK (required)
    implementation(files("libs/dynamic-sdk-android.aar"))

    // Kotlin Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Android WebView
    implementation(libs.androidx.webkit)

    // HTTP client
    implementation(libs.okhttp)

    // Custom Tabs for authentication
    implementation(libs.androidx.browser)

    // Secure storage - DataStore + Tink
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.tink.android.v1150)

    // Passkeys support
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.play.services.auth)
}