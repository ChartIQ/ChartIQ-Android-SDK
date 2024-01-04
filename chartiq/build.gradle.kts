plugins {
    id("maven-publish")
    id("signing")
    id("com.android.library")
    id("kotlin-android")
    id("com.jfrog.artifactory")
    id("kotlin-android-extensions")
    id("org.jetbrains.dokka")
}
extra.set("version_name", "3.3.1")

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
signing {
    sign(publishing.publications)
}

publishing {
    publications {
        create("aar", MavenPublication::class.java) {
            groupId = "io.github.chartiq"
            artifactId = "sdk"
            version = "${extra["version_name"]}"
            artifact("${project.buildDir}/outputs/aar/chartiq-release.aar")
            pom {
                name.set("ChartIQ SDK")
                description.set("The ChartIQ Android SDK provides a native interface for Android developers to instantiate and interact with a ChartIQ chart")
                url.set("https://github.com/ChartIQ/ChartIQ-Android-SDK")
                licenses {
                    license {
                        name.set("Apache-2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                scm {
                    connection.set("scm:git:github.com/ChartIQ/ChartIQ-Android-SDK.git")
                    developerConnection.set("scm:git:ssh://github.com/ChartIQ/ChartIQ-Android-SDK.git")
                    url.set("https://github.com/ChartIQ/ChartIQ-Android-SDK")
                }
                developers {
                    developer {
                        name.set("Jacob Richards")
                        email.set("jacob.richards@spglobal.com")
                        id.set("jacob_richards")

                    }
                }
            }
            repositories {
                maven {
                    name = "OSSRH"
                    url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                    credentials {
//                        username = project.properties["ossrhUsername"] as String
//                        password = project.properties["ossrhPassword"] as String
                    }
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
