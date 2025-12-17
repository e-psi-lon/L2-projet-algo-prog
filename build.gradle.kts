plugins {
    id("java")
    application
}

group = "io.github.e_psi_lon"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

dependencies {
    // Database: SQLite for development, MySQL for final
    implementation("org.xerial:sqlite-jdbc:3.51.0.0")
    implementation("com.mysql:mysql-connector-j:9.5.0")
    implementation("org.jetbrains:annotations:15.0")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("io.github.e_psi_lon.wordcrafter.WordCrafterApp")
}

