import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    alias(libs.plugins.android)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.ksp)
//    kotlin("kapt")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    signingConfigs {
        create("release") {
            storeFile = file("/Users/sachin/Desktop/SachinProjects2023/CalculatorLock/app/calculator_lock_hide_photo_video_calculatorvault_safe_photovault_hideru_vault.jks")
            storePassword = "calculator_lock_hide_photo_video_calculatorvault_safe_photovault_hideru_vault"
            keyAlias = "calculator_lock_hide_photo_video_calculatorvault_safe_photovault_hideru_vault"
            keyPassword = "calculator_lock_hide_photo_video_calculatorvault_safe_photovault_hideru_vault"
        }
    }
    compileSdk = libs.versions.app.build.compileSDKVersion.get().toInt()

    namespace = "calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault"
    defaultConfig {
        applicationId = "calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault"
        minSdk = libs.versions.app.build.minimumSDK.get().toInt()
        targetSdk = libs.versions.app.build.targetSDK.get().toInt()
        versionCode = 6
        versionName = "6.0"
        val archiveBasename = applicationId!!.replace(".", "_") + "_$versionCode"
        archivesName.set(archiveBasename)
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = project.libs.versions.app.build.kotlinJVMTarget.get()
        kotlinOptions.freeCompilerArgs = listOf(
            "-opt-in=kotlin.RequiresOptIn",
            "-Xcontext-receivers"
        )
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    bundle {
        language {
            enableSplit = false
        }
    }

//    kapt {
//        correctErrorTypes = true
//    }


    lint {
        baseline = file("lint-baseline.xml")
        checkReleaseBuilds =false

    }


}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // DI-Hilt
    implementation("com.google.dagger:hilt-android:2.48")
//    kapt("com.google.dagger:hilt-compiler:2.48")
    ksp("com.google.dagger:hilt-compiler:2.48")

    // Lifecycle Extensions
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.activity:activity-ktx:1.7.2")
    implementation("androidx.fragment:fragment-ktx:1.6.1")

    // WorkManager
    implementation("androidx.work:work-runtime:2.8.1")

    // Room components
    implementation("androidx.room:room-ktx:2.5.2")
    ksp("androidx.room:room-compiler:2.5.2")
//    kapt("androidx.room:room-compiler:2.5.2")

    // Gson
    implementation("com.google.code.gson:gson:2.10.1")

    // Sdp-Ssp UI
    implementation("com.intuit.ssp:ssp-android:1.1.0")
    implementation("com.intuit.sdp:sdp-android:1.1.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")

//     Firebase (uncomment if needed)
     implementation(platform("com.google.firebase:firebase-bom:32.3.1"))
     implementation("com.google.firebase:firebase-analytics")
     implementation("com.google.firebase:firebase-crashlytics")
     implementation("com.google.firebase:firebase-messaging")
     implementation("com.google.firebase:firebase-config")

    // Ads
    implementation("com.google.android.gms:play-services-ads:22.4.0")

    // Shimmer
    implementation("com.facebook.shimmer:shimmer:0.5.0")

    // Lottie Animation
    implementation("com.airbnb.android:lottie:6.0.1")

    // Permissions
    implementation("com.karumi:dexter:6.2.3")
    implementation("com.github.getActivity:XXPermissions:18.3")


    // Camera (uncomment if needed)
//    implementation("androidx.exifinterface:exifinterface:1.3.6")
    implementation("androidx.camera:camera-core:1.3.0-rc01")
    implementation("androidx.camera:camera-camera2:1.3.0-rc01")
    implementation("androidx.camera:camera-lifecycle:1.3.0-rc01")
    implementation("androidx.camera:camera-view:1.3.0-rc01")
    implementation("androidx.concurrent:concurrent-futures-ktx:1.1.0")
    implementation("com.google.guava:guava:31.0.1-android")


    // implementation("dev.priyankvasa.android:cameraview-ex:3.5.5-alpha")
    implementation(projects.commons)
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("com.github.Stericson:RootTools:df729dcb13")
    implementation("com.github.Stericson:RootShell:1.6")
    implementation("me.grantland:autofittextview:0.2.1")

    //Language
    implementation("com.zeugmasolutions.localehelper:locale-helper-android:1.5.1")

    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation(project(":ad-sdk"))
    implementation("com.google.android.gms:play-services-ads:22.6.0")
    implementation("com.facebook.shimmer:shimmer:0.5.0@aar")

}

tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class) {
    kotlinOptions {
        languageVersion = "1.9"
    }
}



