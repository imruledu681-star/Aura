// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.kotlin.compose) apply false
  alias(libs.plugins.google.devtools.ksp) apply false
  alias(libs.plugins.roborazzi) apply false
  alias(libs.plugins.secrets) apply false
  alias(libs.plugins.google.services) apply false
}

tasks.register("gitRestore") {
    doLast {
        val pb = java.lang.ProcessBuilder("git", "checkout", "app/src/main/java/com/example/ui/screens/AuraScreens.kt")
        pb.redirectErrorStream(true)
        val p = pb.start()
        val reader = java.io.BufferedReader(java.io.InputStreamReader(p.inputStream))
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            println("[GIT] $line")
        }
        val exitCode = p.waitFor()
        println("[GIT] Exit code: $exitCode")
    }
}

tasks.register("copyApk") {
    doLast {
        val srcFile = file("app/build/outputs/apk/debug/app-debug.apk")
        val destDir = file("APK_DOWNLOAD")
        if (!destDir.exists()) {
            destDir.mkdirs()
        }
        val destFile = file("APK_DOWNLOAD/app-debug.apk")
        if (srcFile.exists()) {
            srcFile.copyTo(destFile, overwrite = true)
            println("Successfully copied APK to ${destFile.absolutePath}, size: ${destFile.length()} bytes")
        } else {
            error("Source APK not found at ${srcFile.absolutePath}")
        }
    }
}
