plugins {
    id("java")
}

group = "de.another"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(files("libs/Server/HytaleServer.jar"))
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

val deployMod by tasks.registering(Copy::class) {
    group = "hytale"
    description = "Builds the jar and copies it to the run/mods directory."
    dependsOn("jar")
    from(tasks.jar)
    into(file("run/mods"))
}

tasks.register<JavaExec>("runServer") {
    dependsOn(deployMod)
    group = "hytale"
    description = "Runs the Hytale server with the plugin for debugging."

    // Define working directory
    val runDir = file("run")
    workingDir = runDir

    doFirst {
        runDir.mkdirs()
    }

    // Main class
    mainClass.set("com.hypixel.hytale.Main")

    // Classpath including the HytaleServer.jar and the compiled plugin
    classpath = files("libs/Server/HytaleServer.jar")

    // JVM Arguments
    jvmArgs("-Xmx4G", "-Xms2G")

    // Program Arguments
    val assetsFile = file("libs/Assets.zip")
    args("--allow-op", "--disable-sentry", "--accept-early-plugins", "--assets=${assetsFile.absolutePath}")

    // Set environment variable if needed.
    // Adapting from VS Code config, you might need to adjust this path if the server requires the full Hytale installation.
    // environment("HYTALE_HOME", "C:/Users/AnotherPC/AppData/Roaming/Hytale/install")

    standardInput = System.`in`
}
