plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")


}

android {
    namespace = "com.example.chambua"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.chambua"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

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
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

}

dependencies {

    implementation(libs.androidx.core.ktx.v1120)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose.v180)
    implementation(platform(libs.androidx.compose.bom.v20231000))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit.v115)
    androidTestImplementation(libs.androidx.espresso.core.v351)
    androidTestImplementation(platform(libs.androidx.compose.bom.v20231000))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)

    implementation(libs.androidx.lifecycle.runtime.compose.v270alpha02)

    //compose destination
    implementation(libs.core)
    ksp(libs.compose.destinations.ksp)

    // Room

    implementation(libs.androidx.room.runtime.v252)
    ksp(libs.androidx.room.compiler.v252)
    implementation(libs.androidx.room.ktx)

    //Dagger-Hilt
    implementation(libs.hilt.android.v248)
    ksp(libs.hilt.android.compiler.v248)
    ksp(libs.androidx.hilt.compiler.v100)
    implementation(libs.androidx.hilt.navigation.compose.v100)

    //fonts
    implementation(libs.androidx.ui.text.google.fonts.v153)

    //Desugaring
    coreLibraryDesugaring(libs.desugar.jdk.libs)
}