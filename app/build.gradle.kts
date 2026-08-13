import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt) // Добавляем плагин Hilt
    alias(libs.plugins.kotlin.parcelize) // Для Parcelable
    alias(libs.plugins.navigation.safeargs)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.ilyadev.meowmoments"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.ilyadev.meowmoments"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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

    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    // --- Настройка productFlavors ---
    flavorDimensions += "version"
    productFlavors {
        create("free") {
            dimension = "version"
            applicationIdSuffix = ".free"
            versionNameSuffix = "-free"
        }
        create("paid") {
            dimension = "version"
            applicationIdSuffix = ".paid"
            versionNameSuffix = "-paid"
        }
    }

    // УБРАЛИ buildFeatures { compose = true }, так как используем View System
    buildFeatures {
        viewBinding = true // Включаем View Binding
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {

    // Paging 3
    implementation("androidx.paging:paging-runtime-ktx:3.3.2")

    // Hilt для WorkManager
    implementation("androidx.hilt:hilt-work:1.3.0")
    ksp("androidx.hilt:hilt-compiler:1.3.0")

    // -- Firebase Cloud Messaging
    implementation(platform("com.google.firebase:firebase-bom:34.16.0"))
    implementation("com.google.firebase:firebase-messaging")

    // -- WorkManager для планирования задач
    implementation("androidx.work:work-runtime-ktx:2.11.2")

    // --- Core ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.appcompat) // Для AppCompatDelegate и базовых виджетов
    implementation(libs.material) // Добавляем Material Components
    implementation(libs.androidx.constraintlayout) // Для View System Layouts
    implementation(libs.androidx.fragment.ktx) // Для Fragment API
    implementation(libs.androidx.swiperefreshlayout)

    // --- Navigation ---
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // --- DI: Hilt ---
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // --- Lifecycle & ViewModel ---
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.activity) // Для ActivityResultLauncher и т.п., если нужно

    // --- Room ---
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx) // Kotlin Extensions для Room
    implementation(libs.androidx.room.paging)
    ksp(libs.androidx.room.compiler)

    // --- DataStore (для настроек) ---
    implementation(libs.androidx.datastore.preferences)

    // --- Networking: Retrofit, OkHttp, Gson ---
    implementation(libs.retrofit)
    implementation(libs.converter.gson) // Конвертер для JSON
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor) // Для логирования HTTP запросов

    // --- Image Loading: Coil (заменяет Glide/Picasso для Kotlin) ---
    implementation(libs.coil)

    // --- Coroutines ---
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // --- Testing ---
    testImplementation(libs.junit)
    testImplementation(libs.androidx.junit)
    testImplementation(libs.androidx.arch.core.testing)
    testImplementation("com.google.dagger:hilt-android-testing:2.55")
    kspTest(libs.hilt.compiler)
    kspAndroidTest(libs.hilt.compiler)
    debugImplementation(libs.androidx.fragment.testing)
    androidTestImplementation(libs.androidx.espresso.contrib)
    testImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit.ktx)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.55")
    testImplementation("org.mockito:mockito-core:5.14.2") // Добавляем Mockito Core
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0") // Добавляем Mockito Kotlin для удобства работы с Kotlin
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0") // Для тестирования Coroutines
    testImplementation("org.robolectric:robolectric:4.14.1")
}

