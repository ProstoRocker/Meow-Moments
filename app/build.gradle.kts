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
        buildConfigField(
            "String",
            "THE_CAT_API_KEY",
            "\"${project.findProperty("THE_CAT_API_KEY")}\""
        )
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
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

    // --- Убедиться, что debug и release создаются для каждого flavor ---
    buildTypes.all {
        // Убедиться, что каждый buildType создает APK для каждого flavor
    }

    // --- Настройка копирования нужного nav_graph ---
    afterEvaluate {
        android.applicationVariants.forEach { variant ->
            val variantName = variant.name.replaceFirstChar { it.uppercase() }
            val flavorName = variant.productFlavors.firstOrNull()?.name

            if (flavorName == "free") {
                tasks.register<Copy>("copy${variantName}NavGraph") {
                    from("src/main/res/navigation/nav_graph_free.xml")
                    into("$buildDir/intermediates/merged_res/${variant.dirName}/res/navigation")
                    rename { "nav_graph.xml" }
                }
                tasks.matching { it.name == "merge${variantName}Resources" }.configureEach {
                    dependsOn("copy${variantName}NavGraph")
                }
            } else if (flavorName == "paid") {
                tasks.register<Copy>("copy${variantName}NavGraph") {
                    from("src/main/res/navigation/nav_graph_paid.xml")
                    into("$buildDir/intermediates/merged_res/${variant.dirName}/res/navigation")
                    rename { "nav_graph.xml" }
                }
                tasks.matching { it.name == "merge${variantName}Resources" }.configureEach {
                    dependsOn("copy${variantName}NavGraph")
                }
            }
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

    //Coil
    implementation("io.coil-kt:coil:2.6.0")
    implementation("io.coil-kt:coil-gif:2.6.0")

    // Paging 3
    implementation("androidx.paging:paging-runtime-ktx:3.3.2")
    implementation("androidx.paging:paging-compose:3.3.2")

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
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.androidx.runtime.saved.instance.state)

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
    implementation(libs.androidx.room.paging)
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
    implementation(libs.androidx.espresso.contrib)
    implementation(libs.androidx.fragment.testing)
    implementation(libs.androidx.junit.ktx)
    testImplementation(libs.junit)
    testImplementation(libs.androidx.arch.core.testing)
    testImplementation("com.google.dagger:hilt-android-testing:2.55")
    kaptTest(libs.hilt.compiler)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.55")
    kaptAndroidTest(libs.hilt.compiler)
    testImplementation("org.mockito:mockito-core:5.14.2") // Добавляем Mockito Core
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0") // Добавляем Mockito Kotlin для удобства работы с Kotlin
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0") // Для тестирования Coroutines
    testImplementation("org.robolectric:robolectric:4.14.1")
}

