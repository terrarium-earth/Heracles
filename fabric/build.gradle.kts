architectury {
    platformSetupLoomIde()
}

classExtensions {
    registerForSourceSet(sourceSets.main.get(), "earth.terrarium.heracles.fabric.extensions")
}

dependencies {
    val fabricLoaderVersion: String by project
    val fabricApiVersion: String by project

    modImplementation(group = "net.fabricmc", name = "fabric-loader", version = fabricLoaderVersion)
    modApi(group = "net.fabricmc.fabric-api", name = "fabric-api", version = fabricApiVersion)
}

tasks.processResources {
    inputs.property("version", version)

    filesMatching("fabric.mod.json") {
        expand("version" to version)
    }
}
