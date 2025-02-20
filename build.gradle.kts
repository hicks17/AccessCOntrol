

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}

buildscript {
    dependencies {
        val hiltVersion = "2.51.1"
        classpath("com.google.dagger:hilt-android-gradle-plugin:$hiltVersion")
    }
}