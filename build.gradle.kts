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


        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}