import net.fabricmc.loom.api.LoomGradleExtensionAPI
import net.fabricmc.loom.task.RemapJarTask

plugins {
    java
    id("dev.architectury.loom") version "1.0-SNAPSHOT" apply false
}

val minecraftVersion: String by project

subprojects {
    apply(plugin = "dev.architectury.loom")

    base {
        archivesName.set(rootProject.name)
    }

    dependencies {
        "minecraft"("::${minecraftVersion}")

        @Suppress("UnstableApiUsage")
        "mappings"(project.the<LoomGradleExtensionAPI>().layered {
            val parchmentVersion: String by project

            officialMojangMappings()

            parchment(create(group = "org.parchmentmc.data", name = "parchment-$minecraftVersion", version = parchmentVersion))
        })
    }

    tasks.jar {
        archiveClassifier.set("dev-${project.name}-$minecraftVersion")
    }

    tasks.named<RemapJarTask>("remapJar") {
        archiveClassifier.set("${project.name}-$minecraftVersion")
    }

    if (name != projects.common.name) {
        sourceSets.main {
            val main = this

            projects.common.dependencyProject.sourceSets.main {
                main.java.source(java)
                main.resources.source(resources)
            }
        }
    }
}
