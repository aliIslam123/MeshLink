plugins {
    alias(libs.plugins.android.application)
}

// Temporary task to fix invalid file names that cause build failures
tasks.register("fixInvalidFiles") {
    doLast {
        val layoutDir = file("src/main/res/layout")
        layoutDir.listFiles()?.forEach { file ->
            if (file.name.contains("-") || file.name.contains(" ")) {
                println("Deleting invalid resource file: ${file.name}")
                file.delete()
            }
        }
        val javaDir = file("src/main/java/com/example/meshlink")
        javaDir.listFiles()?.forEach { file ->
            if (file.name.contains("-")) {
                println("Deleting invalid java file: ${file.name}")
                file.delete()
            }
        }
    }
}

android {
    namespace = "com.example.meshlink"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.meshlink"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
