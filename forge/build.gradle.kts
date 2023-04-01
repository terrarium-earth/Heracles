classExtensions {
    registerForSourceSet(sourceSets.main.get(), "earth.terrarium.hercules.forge.extensions")
}

dependencies {
    val minecraftVersion: String by project
    val forgeVersion: String by project

    forge(group = "net.minecraftforge", name = "forge", version = "$minecraftVersion-$forgeVersion")
}
