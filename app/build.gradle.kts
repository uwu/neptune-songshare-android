plugins {
    id("com.android.application")
}

android {
    namespace = "dev.xinto.neptunesongshare"
    compileSdk = 33

    defaultConfig {
        applicationId = "dev.xinto.neptunesongshare"
        minSdk = 24
        targetSdk = 33
        versionCode = 100
        versionName = "1.0.0"

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
}