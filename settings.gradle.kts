rootProject.name = "heracles"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        maven(url = "https://maven.architectury.dev/")
        maven(url = "https://maven.neoforged.net/releases")
        maven(url = "https://maven.resourcefulbees.com/repository/maven-public/")
        gradlePluginPortal()
    }
}

include("common")
include("fabric")
include("neoforge")
includeBuild("external/olympus")
