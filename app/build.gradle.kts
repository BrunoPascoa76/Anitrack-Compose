plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.apollographql.apollo3") version "3.8.5"
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "cm.project.anitrack_compose"
    compileSdk = 35


    defaultConfig {
        applicationId = "cm.project.anitrack_compose"
        manifestPlaceholders["appAuthRedirectScheme"] = "myapp"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}


dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.paging.runtime.ktx)
    implementation(libs.androidx.paging.compose)
    implementation(libs.androidx.ui.test.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.appauth)
    implementation(libs.coil.compose)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.apollo.runtime)
    implementation(libs.apollo.api)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.apollo.normalized.cache)
    implementation(libs.apollo.http.cache)
    implementation(libs.hilt.android.v250)
    implementation(libs.androidx.hilt.navigation.compose)
    kapt(libs.hilt.compiler.v250)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.compose.ratingbar)
    implementation(libs.compose.wheel.picker.v100rc02)
    implementation(libs.composecalendar)
}

apollo {
    service("anilist") {
        packageName.set("cm.project.anitrack_compose.graphql")
        introspection {
            endpointUrl.set("https://graphql.anilist.co")
        }
    }
}
