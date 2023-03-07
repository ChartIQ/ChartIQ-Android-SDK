plugins {
    id("com.android.library")
    id("kotlin-android")
    id("com.jfrog.artifactory")
    id("maven-publish")
    id("kotlin-android-extensions")
    id("org.jetbrains.dokka")
}
extra.set("version_name", "4.0.0")

android {
    compileSdk = 33

    defaultConfig {
        minSdk = 21
        targetSdk = 33
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles.add(getDefaultProguardFile("proguard-android.txt"))
            proguardFiles.add(file("proguard-rules.pro"))
        }
    }
}

afterEvaluate {
    publishing {
        publications {
            create("aar", MavenPublication::class.java) {
                groupId = "com.chartiq"
                artifactId = "chartiq"
                version = "${extra["version_name"]}"
                artifact("${project.buildDir}/outputs/aar/chartiq-release.aar")
                pom.withXml {
                    val root = asNode()
                    val license = root.appendNode("licenses").appendNode("license")
                    license.appendNode("name", "MPL, Version 2.0")
                    license.appendNode("url", "https://www.mozilla.org/en-US/MPL/2.0/")
                    license.appendNode("distribution", "repo")
                }

            }
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
