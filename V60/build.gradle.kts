val antlrVersion: String by rootProject
val kwebVersion: String by rootProject

plugins {
    kotlin("jvm") version "1.4.10"
}

repositories {
    mavenLocal()
    mavenCentral()
    jcenter()
    maven("https://jitpack.io")
}

group = "com.github.dcc"
version = "1.0.0"

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))

    implementation("org.antlr:antlr4:$antlrVersion")
    implementation("org.antlr:antlr4-runtime:$antlrVersion")

    implementation("com.github.kwebio:kweb-core:$kwebVersion")

    implementation(project(":libdcc"))
}
