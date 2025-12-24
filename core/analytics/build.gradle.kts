plugins {
    alias(libs.plugins.happyinninobsbot.android.library)
    alias(libs.plugins.happyinninobsbot.android.library.compose)
    alias(libs.plugins.happyinninobsbot.hilt)
}

android {
    namespace = "com.obsbot.happyinn.apps.happyinninobsbot.core.analytics"
}

dependencies {
    implementation(libs.androidx.compose.runtime)

//    prodImplementation(platform(libs.firebase.bom))
//    prodImplementation(libs.firebase.analytics)
}