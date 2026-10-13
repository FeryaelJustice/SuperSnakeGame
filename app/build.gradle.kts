import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.dagger.hilt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.google.gms.services)
}

val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties =
    Properties().apply {
        load(FileInputStream(keystorePropertiesFile))
    }

val localProperties =
    Properties().apply {
        val file = rootProject.file("local.properties")
        if (file.exists()) {
            load(FileInputStream(file))
        }
    }
val admobAppId =
    localProperties.getProperty("admob.appId")?.trim()
        ?.takeIf { it.isNotEmpty() }
        ?: "ca-app-pub-3940256099942544~3347511713"
val debugInterstitialAdUnitId =
    localProperties.getProperty("admob.interstitial.testAdUnitId")?.trim()
        ?.takeIf { it.isNotEmpty() }
        ?: "ca-app-pub-3940256099942544/1033173712"
val releaseInterstitialAdUnitId =
    localProperties.getProperty("admob.interstitial.productionAdUnitId")?.trim()
        ?.takeIf { it.isNotEmpty() }
        ?: debugInterstitialAdUnitId

android {
    signingConfigs {
        getByName("debug") {
            storeFile = file(keystoreProperties["storeFileDev"] as String)
            storePassword = keystoreProperties["storePasswordDev"] as String
            keyAlias = keystoreProperties["keyAliasDev"] as String
            keyPassword = keystoreProperties["keyPasswordDev"] as String
        }
        create("release") {
            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
        }
    }
    namespace = "com.feryaeljustice.supersnakegame"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.feryaeljustice.supersnakegame"
        minSdk = 24
        targetSdk = 37
        versionCode = 12
        versionName = "2.2.0"

        manifestPlaceholders["admobAppId"] = admobAppId

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            signingConfig = signingConfigs.getByName("release")
            buildConfigField(
                "String",
                "ADMOB_INTERSTITIAL_AD_UNIT_ID",
                "\"${releaseInterstitialAdUnitId.replace("\"", "\\\"")}\"",
            )
        }
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            isDebuggable = true
            signingConfig = signingConfigs.getByName("debug")
            buildConfigField(
                "String",
                "ADMOB_INTERSTITIAL_AD_UNIT_ID",
                "\"${debugInterstitialAdUnitId.replace("\"", "\\\"")}\"",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    testOptions {
        unitTests.isReturnDefaultValues = true
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
    implementation(libs.androidx.material)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // Navigation compose
    implementation(libs.navigation.compose)

    // Serialization
    implementation(libs.kotlinx.serialization.json)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)

    // Google Auth
    implementation(libs.play.services.auth)
    // for Google Credential Manager
    implementation(libs.androidx.credentials)
    implementation(libs.googleid)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.kotlinx.coroutines.play.services)

    // Google Mobile Ads (AdMob)
    implementation(libs.play.services.ads)
    // User Messaging Platform (UMP)
    implementation(libs.user.messaging.platform)
}
