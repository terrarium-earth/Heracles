import net.fabricmc.loom.api.LoomGradleExtensionAPI
import net.fabricmc.loom.task.RemapJarTask

plugins {
    java
    id("jvm-class-extensions") version "1.3"
    id("dev.architectury.loom") version "1.0-SNAPSHOT" apply false
    id("io.github.juuxel.loom-quiltflower") version "1.8.0" apply false
}

subprojects {
    apply(plugin = "jvm-class-extensions")
    apply(plugin = "dev.architectury.loom")
    apply(plugin = "io.github.juuxel.loom-quiltflower")

    val minecraftVersion: String by project

    base {
        archivesName.set(rootProject.name)
    }

    configure<LoomGradleExtensionAPI> {
        silentMojangMappingsLicense()
    }

    repositories {
        maven(url = "https://maven.architectury.dev/")
        maven(url = "https://maven.fabricmc.net/")
        maven(url = "https://maven.minecraftforge.net/")
        maven(url = "https://maven.parchmentmc.org/")
        maven(url = "https://repo.spongepowered.org/repository/maven-public/")
        maven(url = "https://maven.msrandom.net/repository/root")
        maven(url = "https://maven.resourcefulbees.com/repository/maven-public/")
    }

    dependencies {
        val resourcefulLibVersion: String by project

        "minecraft"("::${minecraftVersion}")

        @Suppress("UnstableApiUsage")
        "mappings"(project.the<LoomGradleExtensionAPI>().layered {
            val parchmentVersion: String by project

            officialMojangMappings()

            parchment(create(group = "org.parchmentmc.data", name = "parchment-$minecraftVersion", version = parchmentVersion))
        })

        "modApi"(group = "com.teamresourceful.resourcefullib", name = "resourcefullib-$name-$minecraftVersion", version = resourcefulLibVersion)
    }

    tasks.jar {
        archiveClassifier.set("dev-${project.name}-$minecraftVersion")
    }

    tasks.named<RemapJarTask>("remapJar") {
        archiveClassifier.set("${project.name}-$minecraftVersion")
    }

    if (name != rootProject.projects.common.name) {
        sourceSets.main {
            val main = this

            rootProject.projects.common.dependencyProject.sourceSets.main {
                main.java.source(java)
                main.resources.source(resources)
            }
        }

        dependencies {
            compileOnly(rootProject.projects.common)
        }
    }
}
