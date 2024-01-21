buildscript {
    dependencies {
        classpath("io.github.porum:muddy-gradle-plugin:2.0.2")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.9.9")
        classpath("com.google.firebase:perf-plugin:1.4.2")
    }
}
plugins {
    alias(libs.plugins.android).apply(false)
    alias(libs.plugins.ksp).apply(false)
    alias(libs.plugins.parcelize).apply(false)
    alias(libs.plugins.library).apply(false)
    alias(libs.plugins.kotlinSerialization).apply(false)
    alias(libs.plugins.hiltAndroid).apply(false)
    alias(libs.plugins.kotlinAndroid).apply(false)
    id("com.google.gms.google-services") version "4.3.15" apply false

}

tasks.register<Delete>("clean") {
    delete {
        layout.buildDirectory.asFile
    }
}
