plugins {
    kotlin("jvm") version "2.0.21"
    kotlin("plugin.serialization") version "2.0.21"
    `java-library`
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xskip-metadata-version-check")
    }
}

repositories {
    mavenCentral()
    mavenLocal()
    ivy {
        url = uri("https://github.com/risa-labs-inc/boss-plugin-api/releases/download")
        patternLayout {
            artifact("v[revision]/[artifact]-[revision].[ext]")
            artifact("[revision]/[artifact]-[revision].[ext]")
        }
        metadataSources { artifact() }
    }
    flatDir {
        dirs("libs")
    }
}

dependencies {
    compileOnly("ai.rever.boss:boss-plugin-api:1.0.93")
    testImplementation("ai.rever.boss:boss-plugin-api:1.0.93")
    compileOnly("org.slf4j:slf4j-api:2.0.18")
    testImplementation("org.slf4j:slf4j-api:2.0.18")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
}

tasks.test {
    useJUnitPlatform()
}
