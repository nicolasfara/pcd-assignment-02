plugins {
    kotlin("jvm") version "1.3.72"
    id("application")
    id("org.openjfx.javafxplugin") version "0.0.8"
}

group = "it.unibo.pcd"
version = "0.0.1"

val junitVersion: String = "5.3.2"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.4")
    implementation( "org.jsoup:jsoup:1.13.1")
    implementation("org.jgrapht:jgrapht-core:1.4.0")
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("no.tornado:tornadofx:1.7.20")
    implementation("io.reactivex.rxjava3:rxkotlin:3.0.0")
    implementation("io.vertx:vertx-core:3.9.0")
    implementation("io.vertx:vertx-web-client:3.9.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
}

application {
    mainClassName = "it.unibo.MainView"
}

javafx {
    version = "13"
    modules = listOf("javafx.controls", "javafx.fxml")
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "13"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "13"
    }
}