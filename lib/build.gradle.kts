plugins {
    id("com.android.library")
    kotlin("android")
    id("com.vanniktech.maven.publish")
}

android {
    namespace = "com.leon.logging"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    libraryVariants.all {
        generateBuildConfigProvider.configure {
            enabled = false
        }
    }
}

dependencies {
    compileOnly(libs.gson)
    compileOnly(libs.okhttp3.core)

    testImplementation(libs.junit)
    testImplementation(libs.gson)
    testImplementation(libs.okhttp3.core)
}
