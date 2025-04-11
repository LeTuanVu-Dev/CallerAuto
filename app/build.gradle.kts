import java.text.SimpleDateFormat
import java.util.Date

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.devtool.ksp)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.gms.googleServices)
    id("kotlin-parcelize")
}
android {
    namespace = "com.freelances.callerauto"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.freelances.callerauto"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        val formattedDate = SimpleDateFormat("MMM.dd.yyyy").format(Date())

        base.archivesName =
            "CallerAuto-v${versionName}(${versionCode})_${formattedDate}"
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
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //viewmodel
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    //preference
    implementation(libs.androidx.preference.ktx)

    //firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.perf.ktx)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.config.ktx)
    //sdp ssp
    implementation(libs.sdp.android)
    implementation(libs.ssp.android)
    //serialization
    implementation(libs.kotlinx.serialization.json)
    //gson
    implementation(libs.gson)
    //koin
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.android.compat)
    //glide
    implementation(libs.glide)
    implementation(libs.glide.transformations)
    annotationProcessor(libs.compiler)
    implementation ("org.apache.poi:poi:5.2.5")
    implementation ("org.apache.poi:poi-ooxml:5.2.5")
    implementation ("com.google.firebase:firebase-firestore-ktx:24.10.3")
}