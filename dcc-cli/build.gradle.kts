
val jUnitVersion:  String by rootProject
val tinyLogVersion: String by rootProject
val antlrVersion: String by rootProject
val cliKtVersion: String by rootProject

plugins {
    application
    kotlin("jvm") version "1.4.10"
}

repositories {
    mavenLocal()
    mavenCentral()
    jcenter()
}

group = "com.github.dcc"
version = "1.0.0"

application {
    mainClassName = "com.github.dcc.MainKt"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        apiVersion = "1.4"
        languageVersion = "1.4"
        allWarningsAsErrors = true
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))

    implementation("org.antlr:antlr4:$antlrVersion")
    implementation("org.antlr:antlr4-runtime:$antlrVersion")

    implementation("com.github.ajalt.clikt:clikt:$cliKtVersion")

    implementation(project(":libdcc"))

}
