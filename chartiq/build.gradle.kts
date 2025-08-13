plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("org.jetbrains.dokka")
    id("maven-publish")
    id("signing")
}

extra.set("version_name", "3.9.0")

android {
    namespace = "com.chartiq.sdk"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles.add(getDefaultProguardFile("proguard-android-optimize.txt"))
            proguardFiles.add(file("proguard-rules.pro"))
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

// Maven Publishing Configuration
afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = "io.github.chartiq"
                artifactId = "sdk"
                version = extra["version_name"] as String

                pom {
                    name.set("ChartIQ Android SDK")
                    description.set("The ChartIQ Android SDK provides a native interface for Android developers to instantiate and interact with a ChartIQ chart.")
                    url.set("https://github.com/ChartIQ/ChartIQ-Android-SDK")
                    inceptionYear.set("2024")
                    licenses {
                        license {
                            name.set("Apache-2.0")
                            url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                            distribution.set("repo")
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
                        connection.set("scm:git:https://github.com/ChartIQ/ChartIQ-Android-SDK.git")
                        developerConnection.set("scm:git:https://github.com/ChartIQ/ChartIQ-Android-SDK.git")
                    }
                }
            }
        }
    }

    signing {
        sign(publishing.publications["release"])
    }
}
