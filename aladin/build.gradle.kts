plugins {
    kotlin("jvm") version "1.5.21"
}

version = "1.0.1"

repositories {
    mavenCentral()

    flatDir {
        dirs("libs")
    }

}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(fileTree("libs") { include("*.jar") })
}
