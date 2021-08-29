import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.21"
    `maven-publish`

    id("io.gitlab.arturbosch.detekt").version("1.18.0")
    // Apply the java-library plugin for API and implementation separation.
    `java-library`
}

group = "com.github.fredgeorge.detektmethodmcc"
version = "1.0"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    // Use the Kotlin JDK 8 standard library.
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("io.gitlab.arturbosch.detekt:detekt-api:1.18.0-RC2")

    testImplementation("io.gitlab.arturbosch.detekt:detekt-test:1.18.0-RC2")

    // Jupiter using JUnit 5
    testImplementation(platform("org.junit:junit-bom:5.7.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()  // Encourages(?) JUnit 5 use by Kotlin
    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "16"
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}
