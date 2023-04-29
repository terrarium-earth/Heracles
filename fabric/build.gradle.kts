repositories {
    maven(url = "https://ladysnake.jfrog.io/artifactory/mods")
}

classExtensions {
    registerForSourceSet(sourceSets.main.get(), "earth.terrarium.heracles.fabric.extensions")
}

dependencies {
    val minecraftVersion: String by project
    val fabricLoaderVersion: String by project
    val fabricApiVersion: String by project

    modImplementation(group = "net.fabricmc", name = "fabric-loader", version = fabricLoaderVersion)
    modApi(group = "net.fabricmc.fabric-api", name = "fabric-api", version = "$fabricApiVersion+$minecraftVersion")

    include(modImplementation(group = "dev.onyxstudios.cardinal-components-api", name = "cardinal-components-entity", version = "5.1.0"))
}
