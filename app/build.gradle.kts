import java.text.SimpleDateFormat
import java.util.Date

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-kapt")
    id("com.google.devtools.ksp")
//    id("io.sentry.android.gradle") version "4.10.0"
    id("com.google.gms.google-services")
//    // Add the Crashlytics Gradle plugin
//    id("com.google.firebase.crashlytics")

}

android {
    namespace = "com.innovu.visitor"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.innovu.visitor"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        buildConfigField ("String", "SERVICE_END_POINT", "\"http://astrovigyan.com:8080/\"")
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
        applicationVariants.all{
            val variant = this
            variant.outputs
                .map { it as com.android.build.gradle.internal.api.BaseVariantOutputImpl }
                .forEach { output ->
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                    val currentDate = dateFormat.format(Date())
                    val outputFileName = "Visitor - ${variant.baseName} - ${variant.versionName} ${variant.versionCode} - $currentDate.apk"
                    println("OutputFileName: $outputFileName")
                    output.outputFileName = outputFileName
                }
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
        buildConfig= true
    }

    flavorDimensions += listOf("brand")
    productFlavors {
        create("visitor") {
            manifestPlaceholders += mapOf("IS_DEV_BUILD" to false)
            dimension = "brand"
            applicationId = "com.innovu.visitor"
            buildConfigField ("String", "SERVICE_END_POINT", "\"http://49.204.232.32:15000/api/\"")
        }
        create("visitorV1") {
            dimension = "brand"
            manifestPlaceholders += mapOf("IS_DEV_BUILD" to false)
            applicationId = "com.innovu.visitor"
            buildConfigField ("String", "SERVICE_END_POINT", "\"http://49.204.232.32:15000/api/\"")
        }
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
    implementation(libs.firebase.dataconnect)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //App Logging
    implementation("com.jakewharton.timber:timber:5.0.1")

    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.0")

    implementation("com.chibatching.kotpref:kotpref:2.13.2")

    //timber - logging
    implementation (libs.timber)

    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.converter.scalars)
    implementation (libs.okhttp)
    implementation(libs.logging.interceptor)


    implementation ("androidx.multidex:multidex:2.0.1")

    implementation ("com.journeyapps:zxing-android-embedded:4.3.0")

    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))

    // Add the dependencies for the Crashlytics and Analytics libraries
    // When using the BoM, you don't specify versions in Firebase library dependencies

    implementation("com.google.firebase:firebase-messaging:23.2.0")

    implementation ("com.google.android.gms:play-services-base:17.1.0")
    implementation ("com.google.android.gms:play-services-auth-api-phone:17.3.0")
    implementation ("com.google.android.gms:play-services-auth:20.7.0")
//    Coroutines
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    // Coroutine Lifecycle Scopes
    implementation (libs.androidx.lifecycle.viewmodel.ktx.v241)
    implementation (libs.androidx.lifecycle.runtime.ktx)

    //Image Lazy Loading
    implementation("io.coil-kt:coil:2.0.0")
    //Swipe in Image
    implementation ("androidx.viewpager2:viewpager2:1.0.0")
    implementation("com.google.android.material:material:1.6.1")
    implementation("com.microsoft.signalr:signalr:6.0.+")
    implementation ("com.google.android.material:material:1.12.0")


    implementation ("com.github.bumptech.glide:glide:4.16.0")
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")

    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
//
//implementation 'com.microsoft.signalr:signalr:9.0.6'
//
//    implementation ("com.google.dagger:dagger-android:2.52")
//    implementation ("com.google.dagger:dagger-android-support:2.52") // For Android support libraries
//    kapt ("com.google.dagger:dagger-compiler:2.52")
//    kapt ("com.google.dagger:dagger-android-processor:2.52")
////
//    val versions = "2.7.1"
//    val roomVersion = "2.5.2"
//    implementation("androidx.room:room-runtime:$roomVersion")
//    kapt("androidx.room:room-compiler:$roomVersion")
//    implementation("androidx.room:room-ktx:$roomVersion")
//    implementation ("androidx.work:work-runtime-ktx:$versions")
//    androidTestImplementation ("androidx.work:work-testing:$versions")
//
//
//    //payment-gateways
//    implementation ("com.razorpay:checkout:1.6.18")

//    val work_version = "2.7.1"
//    implementation ("androidx.work:work-runtime-ktx:$work_version")
//    //for work manager dependency injection
//    implementation ("androidx.hilt:hilt-work:1.0.0")
//    // When using Kotlin.
//    kapt ("androidx.hilt:hilt-compiler:1.0.0")
}