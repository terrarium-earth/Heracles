enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "hercules"

pluginManagement {
    repositories {
        maven(url = "https://maven.architectury.dev/")
        maven(url = "https://maven.fabricmc.net/")
        maven(url = "https://maven.minecraftforge.net/")
        maven(url = "https://maven.msrandom.net/repository/root")
        gradlePluginPortal()
    }
}

include("common", "fabric", "forge")
