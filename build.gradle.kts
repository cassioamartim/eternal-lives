plugins {
    id("java")
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
    id("com.gradleup.shadow") version "8.3.3"
}

group = "br.martim.dev"
version = "DEV-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.codemc.org/repository/maven-public/")
}

dependencies {
    val lombok = "org.projectlombok:lombok:1.18.36"

    compileOnly(lombok)
    annotationProcessor(lombok)

    compileOnly("org.spigotmc:spigot-api:1.21.3-R0.1-SNAPSHOT")

    implementation("dev.jorel:commandapi-bukkit-shade:10.0.0")
}

bukkit {
    name = "eternal-lives"
    main = "br.martim.dev.Eternal"
    author = "Cássio Martim"
    version = project.version as String
    apiVersion = "1.20"
}

tasks.register<Copy>("copyJar") {
    dependsOn(tasks.shadowJar) // Garante que o jar esteja construído

    val outputJar = tasks.shadowJar.get().archiveFile.get().asFile

    from(outputJar)
    into("server/plugins")

    doLast {
        println("Copiado com sucesso para server/plugins: ${outputJar.name}")
    }
}

tasks.shadowJar {
    archiveClassifier.set("")

    relocate("dev.jorel.commandapi", "${project.group}.libs.commandapi")
}