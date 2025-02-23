architectury {
    neoForge()
}

loom {
    neoForge {
        // mixinConfig("heracles-common.mixins.json")
    }
}

dependencies {
    val neoforgeVersion: String by project

    neoForge(group = "net.neoforged", name = "neoforge", version = neoforgeVersion)

    compileOnly("com.teamresourceful:yabn:1.0.3")
    forgeRuntimeLibrary("com.teamresourceful:yabn:1.0.3")
    compileOnly("com.teamresourceful:bytecodecs:1.0.2")
    forgeRuntimeLibrary("com.teamresourceful:bytecodecs:1.0.2")
}

tasks.processResources {
    inputs.property("version", version)

    filesMatching("META-INF/mods.toml") {
        expand("version" to version)
    }
}
