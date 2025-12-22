plugins {
    alias(libs.plugins.android.application)
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
        buildConfigField("String", "API_BASE_URL", "\"http://10.0.186.100:8080/\"")
    }

    buildTypes {
        debug {
            buildConfigField("String", "API_BASE_URL", "\"http://10.0.186.100:8080/\"")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // TODO: Update this URL to your production API before release
            buildConfigField("String", "API_BASE_URL", "\"https://your-production-api.com/\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
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
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation ("com.google.android.material:material:1.14.0-alpha07")
    implementation ("androidx.viewpager2:viewpager2:1.1.0")
    implementation ("com.github.bumptech.glide:glide:5.0.5")
    annotationProcessor ("com.github.bumptech.glide:compiler:5.0.5")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:okhttp:4.12.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation ("com.android.volley:volley:1.2.1")
    implementation ("com.google.code.gson:gson:2.9.1")
    implementation ("com.google.android.flexbox:flexbox:3.0.0")

    // ViewModel and LiveData
    implementation ("androidx.lifecycle:lifecycle-viewmodel:2.7.0")
    implementation ("androidx.lifecycle:lifecycle-livedata:2.7.0")

    // Security - EncryptedSharedPreferences
    implementation ("androidx.security:security-crypto:1.1.0-alpha06")
    
    // Shimmer effect for skeleton loading
    implementation ("com.facebook.shimmer:shimmer:0.5.0")
    
    // SwipeRefreshLayout for pull-to-refresh
    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
}