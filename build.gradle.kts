plugins {
    kotlin("jvm") version "1.3.72"
}

group = "it.unibo.pcd"
version = "0.0.1"

val junitVersion: String = "5.3.2"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation( "org.jsoup:jsoup:1.13.1")
    implementation("org.jgrapht:jgrapht-core:1.4.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    implementation("com.google.code.gson:gson:2.8.6")
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}