pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "happyinninobsbot"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":app")
include(":core:designsystem")
include(":core:network")
include(":core:common")
include(":core:data")
include(":core:model")
include(":core:database")
include(":core:datastore")
include(":core:datastore-proto")
include(":core:navigation")
include(":feature")
include(":feature:foryou")
include(":feature:foryou:api")
include(":feature:foryou:impl")
include(":feature:bookmarks")
include(":feature:bookmarks:api")
include(":feature:bookmarks:impl")
include(":feature:interests")
include(":feature:search")
include(":feature:settings")
include(":feature:topic")
include(":feature:interests:api")
include(":feature:interests:impl")
include(":feature:topic:api")
include(":feature:settings:impl")
include(":feature:search:api")
include(":feature:search:impl")
include(":feature:topic:impl")


check(JavaVersion.current().isCompatibleWith(JavaVersion.VERSION_17)) {
    """
    Now in Android requires JDK 17+ but it is currently using JDK ${JavaVersion.current()}.
    Java Home: [${System.getProperty("java.home")}]
    https://developer.android.com/build/jdks#jdk-config-in-studio
    """.trimIndent()
}
include(":core:ui")
include(":core:analytics")
include(":core:domain")
include(":core:notifications")
include(":sync")
include(":sync:work")
