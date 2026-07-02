import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.BufferedOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import java.util.zip.Deflater
import java.lang.ProcessBuilder

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.google.devtools.ksp)
  alias(libs.plugins.roborazzi)
  alias(libs.plugins.secrets)
  alias(libs.plugins.google.services)
}

android {
  namespace = "com.example"
  compileSdk = 35

  defaultConfig {
    applicationId = "com.imrul.aura"
    minSdk = 24
    targetSdk = 35
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  signingConfigs {
    create("release") {
      val keystorePath = System.getenv("KEYSTORE_PATH") ?: "${rootDir}/my-upload-key.jks"
      storeFile = file(keystorePath)
      storePassword = System.getenv("STORE_PASSWORD")
      keyAlias = "upload"
      keyPassword = System.getenv("KEY_PASSWORD")
    }
    create("debugConfig") {
      storeFile = file("${rootDir}/debug.keystore")
      storePassword = "android"
      keyAlias = "androiddebugkey"
      keyPassword = "android"
    }
  }

  buildTypes {
    release {
      isCrunchPngs = false
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      signingConfig = signingConfigs.getByName("release")
    }
    debug {
      isDebuggable = true
      isMinifyEnabled = false
      isShrinkResources = false
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  buildFeatures {
    compose = true
    buildConfig = true
  }
  testOptions { unitTests { isIncludeAndroidResources = true } }
}

// Configure the Secrets Gradle Plugin to use .env and .env.example files
// to match the convention used in Web projects.
secrets {
  propertiesFileName = ".env"
  defaultPropertiesFileName = ".env.example"
}

// Some unused dependencies are commented out below instead of being removed.
// This makes it easy to add them back in the future if needed.
dependencies {
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.appwrite.sdk) {
    exclude(group = "com.squareup.okhttp3", module = "okhttp-bom")
  }
  // implementation(libs.accompanist.permissions)
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.camera.camera2)
  implementation(libs.androidx.camera.core)
  implementation(libs.androidx.camera.lifecycle)
  implementation(libs.androidx.camera.view)
  implementation(libs.androidx.compose.material.icons.core)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.core.ktx)
  // implementation(libs.androidx.datastore.preferences)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  implementation(libs.androidx.navigation.compose)
  implementation(libs.androidx.room.ktx)
  implementation(libs.androidx.room.runtime)
  implementation(libs.coil.compose)
  implementation(libs.coil.gif)
  implementation(libs.converter.moshi)
  implementation(libs.firebase.database)

  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.logging.interceptor)
  implementation(libs.moshi.kotlin)
  implementation(libs.okhttp)
  // implementation(libs.play.services.location)
  implementation(libs.retrofit)
  testImplementation(libs.androidx.compose.ui.test.junit4)
  testImplementation(libs.androidx.core)
  testImplementation(libs.androidx.junit)
  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.robolectric)
  testImplementation(libs.roborazzi)
  testImplementation(libs.roborazzi.compose)
  testImplementation(libs.roborazzi.junit.rule)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.runner)
  debugImplementation(libs.androidx.compose.ui.test.manifest)
  debugImplementation(libs.androidx.compose.ui.tooling)
  "ksp"(libs.androidx.room.compiler)
  "ksp"(libs.moshi.kotlin.codegen)
}

tasks.register("gitStatus") {
    doLast {
        val process = ProcessBuilder("git", "status")
            .redirectErrorStream(true)
            .start()
        process.waitFor()
        println(process.inputStream.reader().use { it.readText() })
    }
}

tasks.register("copyApkOutputs") {
    val srcPath = layout.buildDirectory.file("outputs/apk/debug/app-debug.apk").get().asFile.absolutePath
    val dest1Path = file("${project.rootDir}/.build-outputs/app-debug.apk").absolutePath
    
    val dest1DirPath = file("${project.rootDir}/.build-outputs").absolutePath

    doLast {
        val sourceApk = File(srcPath)
        if (sourceApk.exists()) {
            val dest1 = File(dest1Path)
            
            File(dest1DirPath).mkdirs()
            
            // Copy to standard destination 1
            sourceApk.copyTo(dest1, overwrite = true)
            println("SUCCESS: App debug APK copied to ${dest1.absolutePath} (Size: ${dest1.length()} bytes)")
        } else {
            error("Source APK not found at ${sourceApk.absolutePath}")
        }
    }
}

// Ensure the copy happens automatically after every standard compilation/assemble
tasks.matching { it.name == "assembleDebug" || it.name == "assemble" }.all {
    finalizedBy("copyApkOutputs")
}

tasks.register("revertScreensFile") {
    val dir = rootDir
    doFirst {
        println("REVERTING SCREENS FILE...")
        try {
            val process = ProcessBuilder("git", "checkout", "app/src/main/java/com/example/ui/screens/AuraScreens.kt")
                .directory(dir)
                .redirectErrorStream(true)
                .start()
            val output = process.inputStream.reader().use { it.readText() }
            process.waitFor()
            println("GIT OUTPUT: $output")
        } catch (e: Exception) {
            println("FAILED TO REVERT: ${e.message}")
        }
    }
}

