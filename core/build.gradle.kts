version = "0.1.0"

plugins {
    id("java-library")
    id("com.github.johnrengelman.shadow") version ("7.0.0")
}

dependencies {
    api(project(":api"))
    shade(files("../libs/weave.jar"))

    // If you want to use external libraries, you can do that here.
    // The dependencies that are specified here are loaded into your project but will also
    // automatically be downloaded by labymod, but only if the repository is public.
    // If it is private, you have to add and compile the dependency manually.
    // You have to specify the repository, there are getters for maven central and sonatype, every
    // other repository has to be specified with their url. Example:
    // maven(mavenCentral(), "org.apache.httpcomponents:httpclient:4.5.13")
}

labyModProcessor {
    referenceType = net.labymod.gradle.core.processor.ReferenceType.DEFAULT
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        archiveBaseName.set("core")

        dependencyFilter.exclude {
            !(it.moduleGroup.startsWith("Cubepanion") || it.moduleGroup.equals("org.cubepanion"))
        }
    }

    getByName("jar").finalizedBy("shadowJar")
}
