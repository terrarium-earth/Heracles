import dev.architectury.plugin.ArchitectPluginExtension
import net.fabricmc.loom.api.LoomGradleExtensionAPI
import net.fabricmc.loom.task.RemapJarTask
import net.fabricmc.loom.task.RemapSourcesJarTask

plugins {
    java
    id("maven-publish")
    id("dev.architectury.loom") version "1.2-SNAPSHOT" apply false
    id("architectury-plugin") version "3.4-SNAPSHOT" apply false
    id("io.github.juuxel.loom-quiltflower") version "1.8.0" apply false
}

subprojects {
    apply(plugin = "maven-publish")
    apply(plugin = "dev.architectury.loom")
    apply(plugin = "architectury-plugin")
    apply(plugin = "io.github.juuxel.loom-quiltflower")

    val minecraftVersion: String by project
    val isCommon = name == rootProject.projects.common.name

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

    java {
        withSourcesJar()
    }

    tasks.jar {
        archiveClassifier.set("dev")
        archiveBaseName.set("${rootProject.name}-${project.name}-$minecraftVersion")
    }

    tasks.named<RemapJarTask>("remapJar") {
        archiveClassifier.set(null as String?)
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

    publishing {
        publications {
            create<MavenPublication>("maven") {
                artifactId = "${rootProject.name}-${project.name}-${minecraftVersion}"
                artifact(tasks.named<RemapJarTask>("remapJar"))
                artifact(tasks.named<Jar>("sourcesJar")) {
                    builtBy(tasks.named<RemapSourcesJarTask>("remapSourcesJar"))
                }

                pom {
                    name.set("Heracles ${project.name}")
                    url.set("https://github.com/terrarium-earth/${rootProject.name}")

                    scm {
                        connection.set("git:https://github.com/terrarium-earth/${rootProject.name}.git")
                        developerConnection.set("git:https://github.com/terrarium-earth/${rootProject.name}.git")
                        url.set("https://github.com/terrarium-earth/${rootProject.name}")
                    }

                    licenses {
                        license {
                            name.set("MIT")
                        }
                    }
                }
            }
        }
        repositories {
            maven {
                setUrl("https://maven.resourcefulbees.com/repository/terrarium/")
                credentials {
                    username = System.getenv("MAVEN_USER")
                    password = System.getenv("MAVEN_PASS")
                }
            }
        }
    }
}
