plugins {
    alias(libs.plugins.happyinninobsbot.jvm.library)
    alias(libs.plugins.happyinninobsbot.hilt)
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
}