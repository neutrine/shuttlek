plugins {
    base
    java
    kotlin("jvm") version "1.3.50"
}

allprojects {
    group = "com.neutrine.shuttlek"
    version = "1.0.0-alpha-1-SNAPSHOT"

    repositories {
        jcenter()
    }
}

subprojects {
    apply(plugin = "java")

    val spekVersion = "2.0.8"

    dependencies {
        implementation(kotlin("stdlib-jdk8"))
        testImplementation(kotlin("reflect"))
        testImplementation(kotlin("test"))
        testImplementation("io.mockk:mockk:1.9.3")
        testImplementation("org.spekframework.spek2:spek-dsl-jvm:$spekVersion")
        testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:$spekVersion")
    }

    tasks.withType<Test> {
        useJUnitPlatform {
            includeEngines("spek2")
        }
    }

    configure<JavaPluginConvention> {
        sourceCompatibility = JavaVersion.VERSION_1_8
    }
}