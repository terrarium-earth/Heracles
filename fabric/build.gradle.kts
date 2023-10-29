architectury {
    fabric()
}

dependencies {
    val fabricLoaderVersion: String by project
    val fabricApiVersion: String by project

    modImplementation(group = "net.fabricmc", name = "fabric-loader", version = fabricLoaderVersion)
    modApi(group = "net.fabricmc.fabric-api", name = "fabric-api", version = fabricApiVersion)

    compileOnly("com.teamresourceful:yabn:1.0.3")
    compileOnly("com.teamresourceful:bytecodecs:1.0.2")
}

tasks.processResources {
    inputs.property("version", version)

    filesMatching("fabric.mod.json") {
        expand("version" to version)
    }
}
