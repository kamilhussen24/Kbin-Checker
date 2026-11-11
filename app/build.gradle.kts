import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

// Release signing is supplied by GitHub Actions (see .github/workflows/build.yml).
val releaseKeystore = file("keystore/android.keystore")
val hasReleaseSigning = releaseKeystore.exists() && !System.getenv("STORE_PASSWORD").isNullOrBlank()

android {
    namespace = "com.kamildex.kbinchecker"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.kamildex.kbinchecker"
        minSdk = 24
        targetSdk = 36
        versionName = "1.0.2"
        // Google Play needs a higher versionCode on every upload.
        // GitHub Actions passes its run number; local builds fall back to 1.
        versionCode = (System.getenv("VERSION_CODE") ?: "1").toInt()

        // Lookup endpoint. Set the BIN_API_URL GitHub secret to your own relay
        // (see server/README.md); without it the app calls the provider directly.
        val binApiUrl = System.getenv("BIN_API_URL")?.trim().takeUnless { it.isNullOrEmpty() }
            ?: "https://data.handyapi.com/bin/"
        buildConfigField("String", "BIN_API_URL", "\"$binApiUrl\"")

        // Optional provider key for direct calls. Leave unset when a relay is used.
        buildConfigField("String", "BIN_API_KEY", "\"${System.getenv("BIN_API_KEY")?.trim() ?: ""}\"")
    }

    signingConfigs {
        // Shared debug key so every debug build can be installed over the previous one.
        getByName("debug") {
            storeFile = file("keystore/debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
        if (hasReleaseSigning) {
            create("release") {
                storeFile = releaseKeystore
                storePassword = System.getenv("STORE_PASSWORD")
                keyAlias = System.getenv("KEY_ALIAS")
                keyPassword = System.getenv("KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            signingConfig = signingConfigs.getByName("debug")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            if (hasReleaseSigning) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    lint {
        abortOnError = false
        checkReleaseBuilds = false
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2025.05.00"))

    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.9.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.0")

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")

    testImplementation("junit:junit:4.13.2")
}
