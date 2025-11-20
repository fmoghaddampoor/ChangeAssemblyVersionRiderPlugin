plugins {
    id("org.jetbrains.intellij") version "1.17.3"
    kotlin("jvm") version "1.9.24"
}

group = providers.gradleProperty("pluginGroup").orNull ?: "com.example.assemblyversion"
version = providers.gradleProperty("pluginVersion").orNull ?: "0.1.0"

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(17)
}

intellij {
    // Rider product
    type.set("RD")
    // Choose a Rider version compatible with your environment
    version.set(providers.gradleProperty("platformVersion").orNull ?: "2024.2")
    // Plugin dependencies, if any
    plugins.set(listOf())
}

tasks {
    patchPluginXml {
        sinceBuild.set(providers.gradleProperty("pluginSinceBuild").orNull ?: "242")
        untilBuild.set(providers.gradleProperty("pluginUntilBuild").orNull)
        changeNotes.set(
            """
            Initial version: Add 'Assembly Version…' action to Project View to set Assembly and File versions across .csproj files.
            """.trimIndent()
        )
    }

    runIde {
        // Running Rider requires downloading the IDE; this may take a while on first run.
        jvmArgs = listOf("-Xmx2g")
    }

    signPlugin {
        certificateChain.set(providers.environmentVariable("CERTIFICATE_CHAIN"))
        privateKey.set(providers.environmentVariable("PRIVATE_KEY"))
        password.set(providers.environmentVariable("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(providers.environmentVariable("PUBLISH_TOKEN"))
    }
}
