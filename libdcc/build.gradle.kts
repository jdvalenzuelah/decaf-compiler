import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.File

val jUnitVersion:  String by rootProject
val tinyLogVersion: String by rootProject
val antlrVersion: String by rootProject

plugins {
    antlr
    idea
    java

    kotlin("jvm") version "1.4.10"
}

repositories {
    mavenLocal()
    mavenCentral()
    jcenter()
}

group = "com.github.dcc"
version = "1.0.0"

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))

    implementation("org.tinylog:tinylog-api-kotlin:$tinyLogVersion")
    implementation("org.tinylog:tinylog-impl:$tinyLogVersion")
    implementation("org.tinylog:slf4j-tinylog:$tinyLogVersion")

    antlr("org.antlr:antlr4:$antlrVersion")
    implementation("org.antlr:antlr4:$antlrVersion")
    implementation("org.antlr:antlr4-runtime:$antlrVersion")

    implementation(project(":aladin"))

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:$jUnitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$jUnitVersion")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile> {

    kotlinOptions {
        jvmTarget = "1.8"
        apiVersion = "1.4"
        languageVersion = "1.4"
        allWarningsAsErrors = true
    }

    dependsOn("generateGrammarSource")
}

tasks.test {
    useJUnitPlatform {
        includeEngines("junit-jupiter","spek2")
    }

    testLogging {
        exceptionFormat = TestExceptionFormat.FULL
        events("passed", "failed", "skipped")
    }
}

tasks.generateGrammarSource {
    maxHeapSize = "128m"
    arguments.addAll(listOf("-package","com.github.dcc.parser", "-visitor"))
    outputDirectory = File("src/main/java/com/github/dcc/parser")
}

tasks.compileJava {
    dependsOn("generateGrammarSource")
}

tasks.clean {
    delete("kotlin/generated")
}