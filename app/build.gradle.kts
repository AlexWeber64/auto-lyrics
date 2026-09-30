import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

// Optional shared signing key. app/signing.p12 is not committed; to use it, put the keystore there
// and set autolyrics.signing.password in local.properties (or AUTOLYRICS_SIGNING_PASSWORD).
// Without it, builds are signed with the standard Android debug key.
val localProperties = Properties().apply {
    rootProject.file("local.properties").takeIf { it.exists() }?.inputStream()?.use { load(it) }
}
val sharedKeystore = file("signing.p12")
val sharedKeystorePassword: String? = localProperties.getProperty("autolyrics.signing.password")
    ?: System.getenv("AUTOLYRICS_SIGNING_PASSWORD")

android {
    namespace = "com.autolyrics"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.autolyrics"
        minSdk = 26
        targetSdk = 34
        versionCode = 31
        versionName = "1.10.0"
    }

    signingConfigs {
        if (sharedKeystore.exists() && sharedKeystorePassword != null) {
            getByName("debug") {
                storeFile = sharedKeystore
                storePassword = sharedKeystorePassword
                keyAlias = "autolyrics"
                keyPassword = sharedKeystorePassword
            }
        }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("debug")
        }
        release {
            isMinifyEnabled = true
            signingConfig = signingConfigs.getByName("debug")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

    // Android Auto - MediaBrowserService
    implementation("androidx.media:media:1.7.0")

    // Networking
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.google.code.gson:gson:2.10.1")

    // Color extraction from album art
    implementation("androidx.palette:palette-ktx:1.0.0")

    // ML Kit — on-device language detection & translation
    implementation("com.google.mlkit:language-id:17.0.6")
    implementation("com.google.mlkit:translate:17.0.3")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
}
