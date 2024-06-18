import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
}

// Read file
fun loadLocalProperties(file: File): Properties {
    val properties = Properties()
    if (file.exists()) {
        file.inputStream().use { stream ->
            properties.load(stream)
        }
    } else {
        println("Warning: local.properties file not found. Using default values.")
    }
    return properties
}

// Read local.properties
val localProperties = loadLocalProperties(rootProject.file("local.properties"))

// Access url property
val myUrl: String = localProperties.getProperty("myUrl")

android {
    namespace = "cz.trask.websdk_in_webview_android"
    compileSdk = 34

    defaultConfig {
        applicationId = "cz.trask.websdk_in_webview_android"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "MY_URL", "\"$myUrl\"")
    }

    buildFeatures {
        buildConfig = true
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.security.crypto)
    implementation(libs.timber)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}