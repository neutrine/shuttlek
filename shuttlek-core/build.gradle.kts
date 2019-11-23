plugins {
    kotlin("jvm")
}

dependencies {
    implementation("org.apache.kafka:kafka-clients:2.3.1")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.+")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.9.+")
}