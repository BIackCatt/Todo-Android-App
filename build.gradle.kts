buildscript {
    extra.apply {
        set("nav_version", "2.8.4")
        set("room_version", "2.6.1")
    }
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") } // For third-party libraries
    }

}

plugins {
    // Android plugins
    id("com.android.application") version "8.7.3" apply false
    id("com.android.library") version "8.7.3" apply false
    id("org.jetbrains.kotlin.android") version "2.1.0" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.0" apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
}