plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "com.example.filmspace_mobile"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.filmspace_mobile"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        // API Base URL configuration
        // Use 10.0.2.2 for Android Emulator to access host machine's localhost
        // Use your actual IP (e.g., 10.0.104.68) for physical device
        buildConfigField("String", "API_BASE_URL", "\"https://c6b8034bb8fb.ngrok-free.app\"")
    }

    buildTypes {
        debug {
            // Use 10.0.2.2 for Android Emulator, change to your IP for physical device
            buildConfigField("String", "API_BASE_URL", "\"https://c6b8034bb8fb.ngrok-free.app\"")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // TODO: Update this URL to your production API before release
            buildConfigField("String", "API_BASE_URL", "\"https://supervisors-claimed-conclusions-subsidiaries.trycloudflare.com/\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    
    // Testing Dependencies
    testImplementation(libs.junit)
    testImplementation("org.mockito:mockito-core:5.8.0")
    testImplementation("org.mockito:mockito-inline:5.2.0")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation("org.mockito:mockito-android:5.8.0")

    implementation ("com.google.android.material:material:1.14.0-alpha07")
    implementation ("androidx.viewpager2:viewpager2:1.1.0")
    implementation ("com.github.bumptech.glide:glide:5.0.5")
    annotationProcessor ("com.github.bumptech.glide:compiler:5.0.5")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:okhttp:4.12.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation ("com.google.code.gson:gson:2.10.1")
    implementation ("com.google.android.flexbox:flexbox:3.0.0")

    // ViewModel and LiveData
    implementation ("androidx.lifecycle:lifecycle-viewmodel:2.8.7")
    implementation ("androidx.lifecycle:lifecycle-livedata:2.8.7")
    
    // Paging 3 for pagination
    implementation ("androidx.paging:paging-runtime:3.3.5")

    // Room for local database
    implementation ("androidx.room:room-runtime:2.6.1")
    annotationProcessor ("androidx.room:room-compiler:2.6.1")
    implementation ("androidx.room:room-paging:2.6.1")

    // Security - EncryptedSharedPreferences
    implementation ("androidx.security:security-crypto:1.1.0")
    
    // Shimmer effect for skeleton loading
    implementation ("com.facebook.shimmer:shimmer:0.5.0")
    
    // SwipeRefreshLayout for pull-to-refresh
    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    
    // ExoPlayer for advanced video playback
    implementation ("androidx.media3:media3-exoplayer:1.2.1")
    implementation ("androidx.media3:media3-ui:1.2.1")
    implementation ("androidx.media3:media3-common:1.2.1")
    
    // Hilt for Dependency Injection
    implementation(libs.hilt.android)
    annotationProcessor(libs.hilt.compiler)

    implementation ("androidx.core:core-splashscreen:1.0.1")
}