plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp") version "2.1.0-1.0.29"
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.gms.google-services") version "4.4.2" apply true

}

android {
    namespace = "com.example.todolist"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.todolist"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }

    packagingOptions {
        resources {
            excludes += setOf("META-INF/DEPENDENCIES")
        }
    }
}

dependencies {
    // Splash Screen
    implementation("androidx.core:core-splashscreen:1.0.1")

    // Compose
    implementation(platform("androidx.compose:compose-bom-alpha:2025.01.00"))
    implementation("androidx.activity:activity-compose:1.10.0")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material:material-icons-extended")
    implementation(libs.androidx.appcompat)

    // Swipe
    implementation("me.saket.swipe:swipe:1.3.0")

    // glance widgets
    implementation("androidx.glance:glance-appwidget:1.1.1")
    implementation("androidx.glance:glance-material3:1.1.1")
    implementation("androidx.glance:glance-material:1.1.1")


    // Core
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.8.4")
    implementation("com.google.accompanist:accompanist-pager:0.28.0")

    // Room
    implementation("androidx.room:room-ktx:2.6.1")
    implementation("androidx.room:room-runtime:2.6.1")
    implementation(libs.androidx.work.runtime.ktx)
    ksp("androidx.room:room-compiler:2.6.1")

    // Lottie
    implementation("com.airbnb.android:lottie-compose:6.1.0")

    // OneTapSignIn
    implementation("com.github.stevdza-san:OneTapCompose:1.0.14")

    // Google Sign-In
    implementation("com.google.accompanist:accompanist-permissions:0.25.0")
    implementation("com.google.gms:google-services:4.4.2")
    implementation("com.google.android.gms:play-services-auth:21.1.1")

    // Coil
    implementation("io.coil-kt:coil-compose:2.5.0")

    // Credentials
    implementation("androidx.credentials:credentials:1.5.0-rc01")
    implementation("androidx.credentials:credentials-play-services-auth:1.5.0-rc01")
    implementation(libs.googleid)

    // Google Drive
    implementation("com.google.android.gms:play-services-drive:17.0.0")
    implementation("com.google.apis:google-api-services-drive:v3-rev20220815-2.0.0")
    implementation("com.google.api-client:google-api-client:2.0.0")
    implementation("com.google.api-client:google-api-client-android:1.32.1")
    implementation("com.google.oauth-client:google-oauth-client-jetty:1.34.1")
    implementation("com.google.auth:google-auth-library-oauth2-http:1.19.0")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore-ktx")

    // Debug
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    debugImplementation("androidx.compose.ui:ui-tooling")
}