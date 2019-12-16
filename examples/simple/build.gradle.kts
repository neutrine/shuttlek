plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":shuttlek-core"))
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.apache.kafka:kafka-clients:2.3.1")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.+")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.9.+")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.2")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
