pluginManagement {
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

rootProject.name = "thuchanh2"
include(":app")


dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            library("ktor-client-core", "io.ktor:ktor-client-core:2.3.3")
            library("ktor-client-cio", "io.ktor:ktor-client-cio:2.3.3")
            library("ktor-client-android", "io.ktor:ktor-client-android:2.3.3")
            library("ktor-client-content-negotiation", "io.ktor:ktor-client-content-negotiation:2.3.3")
            library("ktor-serialization-gson", "io.ktor:ktor-serialization-gson:2.3.3")
        }
    }
}
