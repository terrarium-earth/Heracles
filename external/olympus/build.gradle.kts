import com.teamresourceful.publishing.GitHubPom
import com.teamresourceful.publishing.javaPublishing
import com.teamresourceful.utils.getPlatform
import groovy.json.StringEscapeUtils
import net.fabricmc.loom.api.LoomGradleExtensionAPI

plugins {
    java
    id("maven-publish")
    alias(libs.plugins.resourceful.loom)
    alias(libs.plugins.resourceful.gradle)
}

subprojects {
    apply(plugin = "maven-publish")

    val platform = getPlatform()
    val mcVersion = rootProject.libs.versions.minecraft.get()
    val rlibversion = rootProject.libs.versions.rlib.get()

    dependencies {
        if (platform == com.teamresourceful.utils.Platform.COMMON) {
            "modCompileOnly"(group = "tech.thatgravyboat", name = "commonats", version = "4.0")
        }

        "modApi"(group = "com.teamresourceful.resourcefullib", name = "resourcefullib-${platform.id}-${mcVersion}", version = rlibversion)
    }

    javaPublishing {
        artifactId = "${rootProject.name}-${platform.name}-${mcVersion}".lowercase()

        pom = GitHubPom(
            "Olympus",
            "A UI library for Minecraft mods",
            "MIT",
            "https://github.com/terrarium-earth/Olympus"
        )

        repo = "https://maven.teamresourceful.com/repository/terrarium/"
    }
}


resourcefulGradle {
    templates {
        register("discord") {

            val changelog: String = file("changelog.md").readText(Charsets.UTF_8)

            source = file("templates/embed.json.template")
            injectedValues = mapOf(
                "version" to version,
                "minecraft" to libs.versions.minecraft.get(),
                "neoforge" to libs.versions.neoforge.get(),
                "fabric" to libs.versions.fabric.api.get(),
                "changelog" to StringEscapeUtils.escapeJava(changelog),
            )
        }
    }
}