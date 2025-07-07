plugins {
    id("com.vanniktech.maven.publish")
    id("signing")
    id("com.android.library")
    id("kotlin-android")
    id("com.jfrog.artifactory")
    id("kotlin-android-extensions")
    id("org.jetbrains.dokka")
}
extra.set("version_name", "3.8.0")

android {
    compileSdk = 34

    defaultConfig {
        minSdk = 24
        targetSdk = 34
    }
    
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles.add(getDefaultProguardFile("proguard-android.txt"))
            proguardFiles.add(file("proguard-rules.pro"))
        }
    }
    
    namespace = "com.chartiq.sdk"
}

// Maven Central Publishing 
mavenPublishing {
    publishToMavenCentral()
    
    //signing
    signAllPublications()
    
    // sdk name, version
    coordinates("io.github.chartiq", "sdk", extra["version_name"].toString())
    
    // pom metadata
    pom {
        name.set("ChartIQ Android SDK")
        description.set("The ChartIQ Android SDK provides a native interface for Android developers to instantiate and interact with a ChartIQ chart")
        inceptionYear.set("2024")
        url.set("https://github.com/ChartIQ/ChartIQ-Android-SDK")
        
        licenses {
            license {
                name.set("Apache-2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("https://repo1.maven.org/maven2/")
            }
        }
        
        developers {
            developer {
                id.set("jacob_richards")
                name.set("Jacob Richards")
                email.set("jacob.richards@spglobal.com")
            }
        }
        
        scm {
            url.set("https://github.com/ChartIQ/ChartIQ-Android-SDK")
            connection.set("scm:git:git://github.com/ChartIQ/ChartIQ-Android-SDK.git")
            developerConnection.set("scm:git:ssh://git@github.com/ChartIQ/ChartIQ-Android-SDK.git")
        }
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.3.0")
    implementation("androidx.core:core-ktx:1.5.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:${rootProject.extra["kotlin_version"]}")
    implementation("com.google.code.gson:gson:2.8.6")
    api("commons-lang:commons-lang:2.6")
}

tasks.withType<org.jetbrains.dokka.gradle.DokkaTask>().configureEach {
    dokkaSourceSets {
        named("main") {
            moduleName.set("ChartIQ SDK")
            includes.from("README_Modules.md")
            suppressInheritedMembers.set(true)
            includeNonPublic.set(false)
        }
    }
}
