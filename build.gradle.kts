import dev.architectury.plugin.ArchitectPluginExtension
import net.fabricmc.loom.api.LoomGradleExtensionAPI
import net.fabricmc.loom.task.RemapJarTask

plugins {
    java
    id("dev.architectury.loom") version "1.2-SNAPSHOT" apply false
    id("architectury-plugin") version "3.4-SNAPSHOT" apply false
    id("io.github.juuxel.loom-quiltflower") version "1.8.0" apply false
}

subprojects {
    apply(plugin = "dev.architectury.loom")
    apply(plugin = "architectury-plugin")
    apply(plugin = "io.github.juuxel.loom-quiltflower")

    val minecraftVersion: String by project
    val isCommon = name == rootProject.projects.common.name

    base {
        archivesName.set(rootProject.name)
    }

    configure<LoomGradleExtensionAPI> {
        silentMojangMappingsLicense()
    }

    repositories {
        maven(url = "https://maven.architectury.dev/")
        maven(url = "https://maven.minecraftforge.net/")
        maven(url = "https://maven.msrandom.net/repository/root")
        maven(url = "https://maven.resourcefulbees.com/repository/maven-public/")
    }

    dependencies {
        val resourcefulLibVersion: String by project
        val hermesLibVersion: String by project

        "minecraft"("::${minecraftVersion}")

        @Suppress("UnstableApiUsage")
        "mappings"(project.the<LoomGradleExtensionAPI>().layered {
            val parchmentVersion: String by project

            officialMojangMappings()

            parchment(create(group = "org.parchmentmc.data", name = "parchment-1.19.3", version = parchmentVersion))
        })

        compileOnly(group = "com.teamresourceful", name = "yabn", version = "1.0.3")
        "modApi"(group = "com.teamresourceful.resourcefullib", name = "resourcefullib-$name-$minecraftVersion", version = resourcefulLibVersion)
        val hermes = "modImplementation"(group = "earth.terrarium.hermes", name = "hermes-$name-$minecraftVersion", version = hermesLibVersion) {
            isTransitive = false
        }
        if (!isCommon) {
            "include"(hermes)
        }
    }

    tasks.jar {
        archiveClassifier.set("dev-${project.name}-$minecraftVersion")
    }

    tasks.named<RemapJarTask>("remapJar") {
        archiveClassifier.set("${project.name}-$minecraftVersion")
    }

    if (!isCommon) {
        configure<ArchitectPluginExtension> {
            platformSetupLoomIde()
        }

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
