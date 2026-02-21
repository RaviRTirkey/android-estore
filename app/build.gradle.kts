plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    kotlin("kapt")
    id("com.google.dagger.hilt.android")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.learning.e_store"
    
    compileSdk = 36

    defaultConfig {
        applicationId = "com.learning.e_store"
        minSdk = 33
        targetSdk = 36
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
    android {
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
        }
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
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
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // --- 1. Hilt (Dependency Injection) ---
    implementation("com.google.dagger:hilt-android:2.57.2")
    kapt("com.google.dagger:hilt-android-compiler:2.57.2")
    implementation("androidx.hilt:hilt-navigation-compose:1.3.0")


    // --- 2. Retrofit (Networking) ---
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    // Converter for Kotlin Serialization
    implementation("com.squareup.retrofit2:converter-kotlinx-serialization:2.11.0")
    // OkHttp (Handling HTTP requests)
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0") // Useful for debugging


    // --- 3. Kotlin Serialization (JSON Parsing) ---
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")

    // --- 4. Coil (Image Loading in Compose) ---
    // I. Basic Coil Compose integration
    implementation("io.coil-kt.coil3:coil-compose:3.0.4")
    // II. REQUIRED for network images (URLs) in Coil 3+
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.0.4")

    // --- 5. Lifecycle & ViewModel ---
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.0")

    // Jetpack Security library
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    
    // Extended Icons
    implementation("androidx.compose.material:material-icons-extended")

    //date time for kotlin serialization
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.7.1") 

}