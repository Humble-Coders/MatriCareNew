plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    kotlin("kapt")
    id("com.google.gms.google-services")
}

kapt {
    correctErrorTypes = true
}

android {
    namespace = "com.example.matricareog"
    compileSdk = 35

    // tensorflow
    aaptOptions {
        noCompress.add("tflite")  // Note the .add() for Kotlin DSL
    }

    defaultConfig {
        applicationId = "com.example.matricareog"
        minSdk = 26
        targetSdk = 35
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


    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {


    //tensorflow dependencies
    implementation(libs.tensorflow.lite)
    implementation(libs.tensorflow.lite.select.tf.ops)

    // corountine
    implementation(libs.kotlinx.coroutines.android)


    // YCharts for Jetpack Compose charting
    implementation(libs.ycharts)
    implementation(libs.mpandroidchart)
    // --- Compose BOM (manages compatible versions for Compose libs) ---
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))


    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler) // Use kapt with version catalog
    implementation(libs.hilt.navigation.compose)

    // --- Compose Core Dependencies ---
    implementation("androidx.compose.ui:ui")
    //
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material:material")
    implementation("androidx.compose.material:material-icons-extended:1.6.8") // Needed explicitly
    implementation("androidx.compose.material3:material3") // BOM handles version

    // --- Navigation ---
    implementation("androidx.navigation:navigation-compose:2.7.5")

    // --- Activity & Lifecycle ---
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.core:core-ktx:1.13.1") // Replace with latest if not using libs. convention
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

    // --- Firebase (Analytics as example, extend as needed) ---
    implementation(platform("com.google.firebase:firebase-bom:33.15.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation(libs.firebase.auth)

// Firestore (for storing user data)
    implementation("com.google.firebase:firebase-firestore")
    implementation( "com.google.firebase:firebase-firestore-ktx:24.10.0")
    implementation ("com.google.firebase:firebase-auth-ktx:22.3.0")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation ("androidx.compose.runtime:runtime-livedata:1.5.4")

    // --- Optional: Coil (for images) ---
    implementation("io.coil-kt:coil-compose:2.5.0")

    // --- Testing ---
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.02.00"));
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}


