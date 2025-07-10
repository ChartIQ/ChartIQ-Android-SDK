// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    project.extra["kotlin_version"] = "1.6.0"

    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.2.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${rootProject.extra["kotlin_version"]}")
        classpath("org.jfrog.buildinfo:build-info-extractor-gradle:5.2.5")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.5.0")
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:1.4.32")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        // mavenLocal() // Enable this to use the local published SDK
    }
}

// nmcp configuration
plugins {
    id("com.gradleup.nmcp.aggregation").version("1.0.1")
}

nmcpAggregation {
    centralPortal {
        username = findProperty("mavenCentralUsername") as String? ?: System.getenv("SONATYPE_USERNAME") ?: ""
        password = findProperty("mavenCentralPassword") as String? ?: System.getenv("SONATYPE_PASSWORD") ?: ""
        publishingType = "USER_MANAGED" // "AUTOMATIC"
    }

    publishAllProjectsProbablyBreakingProjectIsolation()
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}