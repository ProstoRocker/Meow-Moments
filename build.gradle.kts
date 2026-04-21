// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false // Нужен для Android-библиотек, не только для Compose
    alias(libs.plugins.hilt) apply false // Добавляем плагин Hilt
    alias(libs.plugins.kotlin.parcelize) apply false // Может понадобиться для моделей данных
    alias(libs.plugins.kotlin.kapt) apply false // Для Room Compiler и Hilt Annotation Processor
}