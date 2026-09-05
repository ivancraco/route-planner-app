import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    id("app.cash.sqldelight") version "2.3.2"
    kotlin("plugin.serialization") version "2.3.21"
}

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) load(file.inputStream())
}
val googleApiKey = localProperties["GOOGLE_API_KEY"]?.toString()
    ?: error("GOOGLE_API_KEY not found in local.properties")

// Genera ApiKeys.kt en commonMain
val generateKeysTask = tasks.register("generateApiKeys") {
    val outputDir = layout.buildDirectory.dir("generated/keys/commonMain/kotlin")
    outputs.dir(outputDir)
    doLast {
        val dir = outputDir.get().asFile
        dir.mkdirs()
        File(dir, "ApiKeys.kt").writeText(
            """
            package com.routeplanner.app.core

            internal object ApiKeys {
                const val GOOGLE_API_KEY = "$googleApiKey"
            }
            """.trimIndent()
        )
    }
}

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }

    androidLibrary {
        namespace = "com.routeplanner.app.shared"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
        androidResources {
            enable = true
        }
        withHostTest {
            isIncludeAndroidResources = true
        }
    }

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    sourceSets {
        commonMain {
            kotlin.srcDir(
                layout.buildDirectory.dir("generated/keys/commonMain/kotlin")
            )
        }
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            //Ktor client
            implementation(libs.ktor.client.android)
            //Koin
            implementation(libs.koin.android)
            //Sqldelight
            implementation(libs.android.driver)
            //Play services
            implementation(libs.play.services.location)
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.material.icons.extended)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            //Ktor client
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.auth)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.logging)
            //Navigation
            implementation(libs.navigation.compose)
            //Koin
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            //Preferences
            implementation(libs.multiplatform.settings)
            //Data time
            implementation(libs.kotlinx.datetime)
            //SqlDelight
            implementation(libs.primitive.adapters)
            implementation(libs.sqldelight.coroutines.extensions)
            //Maps
            implementation(libs.kmp.maps.core)
            //Moko Permissions
            implementation(libs.permissions.location)
            implementation(libs.permissions.compose)
            //Connectivity
            implementation("dev.jordond.connectivity:connectivity-core:2.4.1")
            implementation("dev.jordond.connectivity:connectivity-device:2.4.1")
        }
        iosMain.dependencies {
            //Ktor client
            implementation(libs.ktor.client.darwin)
            //Sqldelight
            implementation(libs.native.driver)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}

sqldelight {
    databases {
        create("AppDatabase") {
            packageName.set("com.routeplanner.app")
            generateAsync.set(true)
        }
    }
}