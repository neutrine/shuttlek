plugins {
    kotlin("jvm") version "1.7.10"
    jacoco
    id("org.jlleitschuh.gradle.ktlint") version "10.3.0"
    id("org.sonarqube") version "2.8"
    id("org.jetbrains.dokka") version "1.7.10"
}

allprojects {
    group = "com.neutrine.shuttlek"
    version = "1.0.0-SNAPSHOT"

    apply(plugin = "org.jetbrains.dokka")

    repositories {
        mavenCentral()
    }
}

sonarqube {
    properties {
        property("sonar.projectKey", "neutrine_shuttlek")
        property("sonar.junit.reportPaths", "build/test-results/test")
        property("sonar.coverage.jacoco.xmlReportPaths", "build/reports/jacoco/test/jacocoTestReport.xml")
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    apply(plugin = "jacoco")

    val spekVersion = "2.0.8"

    dependencies {
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

    tasks.jacocoTestReport {
        reports {
            xml.isEnabled = true
        }
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_11.toString()
        kotlinOptions.freeCompilerArgs += "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
    }

    tasks {
        dokkaGfm {
            outputDirectory.set(file("$rootDir/docs/api"))
        }
    }

    ktlint {
        verbose.set(true)
        outputToConsole.set(true)
        outputColorName.set("RED")
        disabledRules.add("import-ordering")
    }
}
