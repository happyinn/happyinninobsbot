plugins {
    alias(libs.plugins.happyinninobsbot.android.library)
    alias(libs.plugins.happyinninobsbot.android.library.jacoco)
    id("com.google.devtools.ksp")
}

android {
    namespace = "ccom.obsbot.happyinn.apps.happyinninobsbot.core.domain"
}

dependencies {
    api(projects.core.data)
    api(projects.core.model)

    implementation(libs.javax.inject)

//    testImplementation(projects.core.testing)
}