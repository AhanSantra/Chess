plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("org.postgresql:postgresql:42.7.3")
    implementation("org.json:json:20230227")
    implementation("org.xerial:sqlite-jdbc:3.42.0.0")
}

tasks.test {
    useJUnitPlatform()
    useJUnitPlatform()
    failOnNoDiscoveredTests = false
}
