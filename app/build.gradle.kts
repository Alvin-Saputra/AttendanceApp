plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
}

android {
    namespace = "com.example.attendanceapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.attendanceapp"
        minSdk = 26
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    implementation(libs.play.services.maps)

    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.activity)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation ("com.airbnb.android:lottie:+")


    implementation ("com.google.android.gms:play-services-maps:18.2.0")


    implementation ("com.google.android.gms:play-services-location:21.1.0")

    implementation ("androidx.camera:camera-camera2:1.4.0")
    implementation ("androidx.camera:camera-lifecycle:1.4.0")
    implementation ("androidx.camera:camera-view:1.4.0")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")


    implementation ("io.coil-kt:coil:2.2.2")
    implementation ("io.coil-kt:coil-gif:2.2.2")

    implementation ("com.github.bumptech.glide:glide:4.15.")
    implementation ("com.github.bumptech.glide:okhttp3-integration:4.15.1")

    implementation("androidx.datastore:datastore-preferences:1.0.0")

    implementation("com.applandeo:material-calendar-view:1.9.0")

    implementation ("de.hdodenhof:circleimageview:3.1.0")


    implementation ("pl.droidsonroids.gif:android-gif-drawable:1.2.29")
}