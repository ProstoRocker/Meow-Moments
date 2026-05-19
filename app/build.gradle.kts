plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt) // Добавляем плагин Hilt
    alias(libs.plugins.kotlin.parcelize) // Для Parcelable
    alias(libs.plugins.kotlin.kapt) // Для kapt (Room, Hilt)
    alias(libs.plugins.navigation.safeargs)
}

android {
    namespace = "com.ilyadev.meowmoments"
    compileSdk = 35 // Используем =, а не вызов функции

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
        buildConfigField("String", "THE_CAT_API_KEY", "\"${project.findProperty("THE_CAT_API_KEY")}\"")
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
    kotlinOptions {
        jvmTarget = "11"
    }

    // УБРАЛИ buildFeatures { compose = true }, так как используем View System
    buildFeatures {
        viewBinding = true // Включаем View Binding
        buildConfig = true
        // compose = false // Явно указать не обязательно, но для ясности
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    // Hilt для WorkManager
    implementation("androidx.hilt:hilt-work:1.2.0")
    kapt("androidx.hilt:hilt-compiler:1.2.0")

    // -- Firebase Cloud Messaging
    implementation(platform("com.google.firebase:firebase-bom:32.8.0"))
    implementation("com.google.firebase:firebase-messaging-ktx")

    // -- WorkManager для планирования задач
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    // --- Core ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.appcompat) // Для AppCompatDelegate и базовых виджетов
    implementation(libs.material) // Добавляем Material Components
    implementation(libs.androidx.constraintlayout) // Для View System Layouts
    implementation(libs.androidx.fragment.ktx) // Для Fragment API

    // --- Navigation ---
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // --- DI: Hilt ---
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler) // Используем kapt для Hilt Compiler

    // --- Lifecycle & ViewModel ---
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx) // Если будешь использовать LiveData
    implementation(libs.androidx.activity) // Для ActivityResultLauncher и т.п., если нужно

    // --- Room ---
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx) // Kotlin Extensions для Room
    kapt(libs.androidx.room.compiler) // Используем kapt для Room Compiler

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
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    testImplementation("org.mockito:mockito-core:5.14.2") // Добавляем Mockito Core
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0") // Добавляем Mockito Kotlin для удобства работы с Kotlin
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0") // Для тестирования Coroutines
}

