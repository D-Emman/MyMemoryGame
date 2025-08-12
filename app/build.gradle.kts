plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.memorygame"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.memorygame"
        minSdk = 24
        //noinspection EditedTargetSdkVersion
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
}

dependencies {

    implementation( "com.github.bumptech.glide:glide:4.16.0")
    implementation("com.github.bumptech.glide:compiler:4.16.0")
//    implementation(platform("io.github.jan-tennert.supabase:bom:VERSION"))
    implementation("io.github.jan-tennert.supabase:gotrue-kt:2.6.1")
    implementation("io.github.jan-tennert.supabase:storage-kt:3.2.2")
    implementation("io.github.jan-tennert.supabase:postgrest-kt:3.2.2")
    implementation("io.ktor:ktor-client-cio:3.2.0")

    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation(platform("com.google.firebase:firebase-bom:34.0.0"))
    implementation("com.google.android.gms:play-services-ads:24.4.0")
    implementation("androidx.recyclerview:recyclerview:1.3.0")
    implementation("androidx.fragment:fragment:1.8.8")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.viewpager2:viewpager2:1.1.0")
    implementation("com.google.android.gms:play-services-maps:19.2.0")
    implementation("androidx.activity:activity:1.10.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    implementation("com.google.firebase:firebase-firestore-ktx:24.9.0")
    implementation("com.google.firebase:firebase-auth-ktx:22.3.1")
//    implementation("com.google.firebase:firebase-firestore")
//    implementation("com.google.firebase:firebase-storage")

}