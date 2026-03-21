# Olympus

Olympus is a simple, flexible, cross platform UI library for Minecraft Java that allows
you to create GUIs with complex layouts and UI elements with ease. By default, all UI
elements are styled to look like Bedrock Edition, but you can easily customize the
appearance of your UIs to match your mod's theme.

## Features

Olympus currently provides the following UI elements:
- Buttons
- Labels
- Text fields
- Integer fields
- Lists
- Grids
- Multiline Strings
- Minimap
- Dropdowns
- Radio buttons

## Usage

To use Olympus in your mod, add the following to your `build.gradle`:

Add the maven repository to your `repositories` block:

Groovy
```groovy
repositories {
    maven {
        url "https://maven.teamresourceful.com/repository/maven-public/"
    }
}
```

Kotlin Script
```kts
repositories {
    maven(url = "https://maven.teamresourceful.com/repository/maven-public/")
}
```

Then add the Olympus dependency to your `dependencies` block:

> Note: Replace `version` with the version of Olympus you want to use. or use "latest.release" to get the latest version.

### Common 

```kts
dependencies {
    modImplementation(group = "earth.terrarium.olympus", name = "olympus-common-1.21", version = version)
}
```

> We don't plan to release an official jar of Olympus on Modrinth or CurseForge, so you'll need to include the platform specific
> modules in your mod jar.

### Fabric Loom
    
```kts
dependencies {
    include(modImplementation(group = "earth.terrarium.olympus", name = "olympus-fabric-1.21", version = version))
}
```

### NeoForge (Architectury Loom)

```kts
dependencies {
    include(modImplementation(group = "earth.terrarium.olympus", name = "olympus-forge-1.21", version = version))
}
```

### Neoforge (Neogradle)

```kts
dependencies {
    implementation(group = "earth.terrarium.olympus", name = "olympus-forge-1.21", version = version)
    jarJar(group = "earth.terrarium.olympus", name = "olympus-forge-1.21", version = "[version,range)")
}
```

> Note: Olympus does not support Legacy Forge. Please use NeoForge.

