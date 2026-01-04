plugins {
    alias(libs.plugins.happyinninobsbot.android.library)
    alias(libs.plugins.happyinninobsbot.hilt)
}

android{
    namespace = "com.obsbot.happyinn.apps.happyinninobsbot.core.common"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.core)
    testImplementation(libs.turbine)
}