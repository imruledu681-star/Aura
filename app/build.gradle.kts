import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.BufferedOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import java.util.zip.Deflater

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.google.devtools.ksp)
  alias(libs.plugins.roborazzi)
  alias(libs.plugins.secrets)
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
      signingConfig = signingConfigs.getByName("debugConfig")
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
  // implementation(libs.androidx.camera.camera2)
  // implementation(libs.androidx.camera.core)
  // implementation(libs.androidx.camera.lifecycle)
  // implementation(libs.androidx.camera.view)
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
  implementation(libs.converter.moshi)

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
        val process = Runtime.getRuntime().exec("git status")
        process.waitFor()
        println(process.inputStream.bufferedReader().readText())
        println(process.errorStream.bufferedReader().readText())
    }
}

tasks.register("copyApkOutputs") {
    val srcPath = layout.buildDirectory.file("outputs/apk/debug/app-debug.apk").get().asFile.absolutePath
    val dest1Path = file("${project.rootDir}/.build-outputs/app-debug.apk").absolutePath
    val dest2Path = file("${project.rootDir}/APK_DOWNLOAD/app-debug.apk").absolutePath
    val fallbackBinPath = file("${project.rootDir}/APK_DOWNLOAD/app-debug-apk.bin").absolutePath
    val zipFilePath = file("${project.rootDir}/APK_DOWNLOAD/app-debug-apk.zip").absolutePath
    
    val dest1DirPath = file("${project.rootDir}/.build-outputs").absolutePath
    val dest2DirPath = file("${project.rootDir}/APK_DOWNLOAD").absolutePath
    val vercelPublicDirPath = file("${project.rootDir}/Vercel-Portal/public").absolutePath

    doLast {
        val sourceApk = File(srcPath)
        if (sourceApk.exists()) {
            val dest1 = File(dest1Path)
            val dest2 = File(dest2Path)
            val fallbackBin = File(fallbackBinPath)
            val zipFile = File(zipFilePath)
            
            File(dest1DirPath).mkdirs()
            File(dest2DirPath).mkdirs()
            File(vercelPublicDirPath).mkdirs()
            
            // 1. Copy to standard destination 1
            sourceApk.copyTo(dest1, overwrite = true)
            println("SUCCESS: App debug APK copied to ${dest1.absolutePath} (Size: ${dest1.length()} bytes)")
            
            // 2. Copy to standard destination 2
            sourceApk.copyTo(dest2, overwrite = true)
            println("SUCCESS: App debug APK copied to ${dest2.absolutePath} (Size: ${dest2.length()} bytes)")
            
            // 3. Copy to fallback bin file (to prevent direct browser/platform downloads block on APK extension)
            sourceApk.copyTo(fallbackBin, overwrite = true)
            println("SUCCESS: App debug APK copied as BIN to ${fallbackBin.absolutePath} (Size: ${fallbackBin.length()} bytes)")
            
            // 4. Compress to a single ZIP file (maximum compatibility, prevents truncation / compression corruption)
            BufferedOutputStream(FileOutputStream(zipFile)).use { bos ->
                ZipOutputStream(bos).use { zos ->
                    zos.setLevel(Deflater.BEST_COMPRESSION)
                    val entry = ZipEntry("app-debug.apk")
                    zos.putNextEntry(entry)
                    val buffer = ByteArray(8192)
                    FileInputStream(sourceApk).use { fis ->
                        var len = fis.read(buffer)
                        while (len > 0) {
                            zos.write(buffer, 0, len)
                            len = fis.read(buffer)
                        }
                    }
                    zos.closeEntry()
                }
            }
            println("SUCCESS: App debug APK compressed into ZIP at ${zipFile.absolutePath} (Size: ${zipFile.length()} bytes)")

            // 5. Place in Vercel-Portal/public directory for easy Zero-Config Instant Deployment
            val vercelApk = File(vercelPublicDirPath, "app-debug.apk")
            val vercelBin = File(vercelPublicDirPath, "app-debug-apk.bin")
            val vercelZip = File(vercelPublicDirPath, "app-debug-apk.zip")
            sourceApk.copyTo(vercelApk, overwrite = true)
            sourceApk.copyTo(vercelBin, overwrite = true)
            zipFile.copyTo(vercelZip, overwrite = true)
            println("SUCCESS: Copied all installer assets to Vercel portal public assets folder at ${vercelPublicDirPath}")
        } else {
            error("Source APK not found at ${sourceApk.absolutePath}")
        }
    }
}

// Ensure the copy happens automatically after every standard compilation/assemble
tasks.matching { it.name == "assembleDebug" || it.name == "assemble" }.all {
    finalizedBy("copyApkOutputs")
}
