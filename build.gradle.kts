// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    dependencies {
        classpath(libs.android.gradle.plugin)
        classpath(libs.kotlin.gradle.plugin)
        classpath(libs.dokka.plugin)
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.spotless)
    alias(libs.plugins.mavenPublish) apply false
}

spotless {
    kotlin {
        target("**/*.kt")
        targetExclude("**/build/**/*.kt")
        ktlint(libs.versions.ktlint.get()).userData(
            mapOf(
                "android" to "true",
                "disabled_rules" to "no-wildcard-imports",
            )
        ).editorConfigOverride(
            mapOf("max_line_length" to "off")
        )
        trimTrailingWhitespace()
        indentWithSpaces()
    }

    kotlinGradle {
        target("**/*.gradle.kts")
        targetExclude("**/build/**/*.kts")
        ktlint()
    }
}
