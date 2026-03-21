sourceSets {
    create("example") {
        runtimeClasspath += sourceSets["main"].runtimeClasspath
        compileClasspath += sourceSets["main"].compileClasspath
    }
}

loom {
    runs {
        register("example") {
            client()
            ideConfigGenerated(true)
            name = "Run Example"
            source(sourceSets.getByName("example"))
        }
    }
}