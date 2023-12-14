// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    project.extra["kotlin_version"] = "1.6.20"

    repositories {
        google()
        jcenter()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.0.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${rootProject.extra["kotlin_version"]}")
        classpath("org.jfrog.buildinfo:build-info-extractor-gradle:4.1.1")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.5.0")
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:1.4.32")


        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        mavenCentral()
    }
}
tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}