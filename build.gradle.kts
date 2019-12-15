plugins {
    base
    java
    kotlin("jvm") version "1.3.50"
    jacoco
    id("org.jlleitschuh.gradle.ktlint") version "9.1.1"
    id("org.sonarqube") version "2.8"
    id("org.jetbrains.dokka") version "0.10.0"
}

allprojects {
    group = "com.neutrine.shuttlek"
    version = "1.0.0-alpha-1-SNAPSHOT"

    apply(plugin = "org.jetbrains.dokka")

    repositories {
        jcenter()
    }

    tasks.dokka {
        outputFormat = "gfm"
        outputDirectory = "$buildDir/dokka"
        outputDirectory = "$rootDir/docs/api"
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
    apply(plugin = "java")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    apply(plugin = "jacoco")

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

    tasks.jacocoTestReport {
        reports {
            xml.isEnabled = true
        }
    }

    configure<JavaPluginConvention> {
        sourceCompatibility = JavaVersion.VERSION_1_8
    }

    ktlint {
        verbose.set(true)
        outputToConsole.set(true)
        outputColorName.set("RED")
        disabledRules.add("import-ordering")
    }
}
