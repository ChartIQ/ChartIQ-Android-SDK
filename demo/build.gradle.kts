plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs")
}
android {
    compileSdk = 33

    defaultConfig {
        applicationId = "com.chartiq.demo"
        minSdk = 21
        targetSdk = 32
        versionCode = 12
        versionName = "3.5.0"

        buildConfigField(
            "String",
            "DEFAULT_CHART_URL",
            "\"https://mobile.demo.chartiq.com/android/3.5.0/sample-template-native-sdk.html\""

        )
    }
    signingConfigs {
        create("release") {
            storeFile = file("path to keystore")
            storePassword = ""
            keyAlias = ""
            keyPassword = ""
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles.add(getDefaultProguardFile("proguard-android-optimize.txt"))
            proguardFiles.add(file("proguard-rules.pro"))
//            signingConfig = signingConfigs.getByName("release")
        }
    }
    flavorDimensions += listOf("version")

    productFlavors {
        create("single_page_demo")
        {
            dimension = "version"
            buildConfigField("boolean", "NEEDS_BACK_NAVIGATION", "true")
            buildConfigField("boolean", "NEEDS_CHAR_STYLE_OPTION", "false")

        }
        /*create("tabs_demo") {
            dimension = "version"
            buildConfigField("boolean", "NEEDS_BACK_NAVIGATION", "false")
            buildConfigField("boolean", "NEEDS_CHAR_STYLE_OPTION", "true")
        }*/
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8

    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
        freeCompilerArgs = listOf("-Xjvm-default=all")
    }

    buildFeatures {
        viewBinding = true
    }
    namespace = "com.chartiq.demo"
    defaultConfig {
        vectorDrawables.useSupportLibrary = true
    }
}

dependencies {
    implementation(project(":chartiq"))

    // Core
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${rootProject.extra["kotlin_version"]}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0")
    implementation("androidx.core:core-ktx:1.5.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation ("androidx.work:work-runtime-ktx:2.7.0")

    // UI
    implementation("com.google.android.material:material:1.3.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.fragment:fragment-ktx:1.3.0")

    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.3.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.3.5")

    // Network
    implementation("com.squareup.okhttp3:okhttp:4.9.0")
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:0.9.2")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")

    // Localization
    implementation("dev.b3nedikt.restring:restring:5.1.0")
    implementation("dev.b3nedikt.viewpump:viewpump:4.0.3")
    implementation("dev.b3nedikt.reword:reword:3.1.0")
}
