import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    val kotlinVersion = "2.1.20"
    val springBootVersion = "3.4.4" // !! sync springBootVersion vals
    id("org.jetbrains.kotlin.jvm") version kotlinVersion
    id("org.jetbrains.kotlin.plugin.spring") version kotlinVersion
    id("org.jetbrains.kotlin.plugin.allopen") version kotlinVersion
    id("org.jetbrains.kotlin.plugin.jpa") version kotlinVersion
    id("org.springframework.boot") version springBootVersion
    id("io.spring.dependency-management") version "1.1.7"
    id("org.ec4j.editorconfig") version "0.1.0"
    id("idea")
    signing
    `maven-publish`
    java
}


idea {
    module {
        excludeDirs = listOf("build", "logs").map { file(it) }.toSet()
    }
}

val springBootVersion = "3.4.4" // !! sync springBootVersion vals
var minorVersion: Int = 2
group = "me.saro"
version = "$springBootVersion.$minorVersion"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework:spring-core")
    implementation("org.springframework:spring-web")
    implementation("org.springframework:spring-webflux")
    implementation("org.springframework:spring-context")
    implementation("io.projectreactor:reactor-core")
    implementation("com.fasterxml.jackson.core:jackson-core")
    implementation("com.fasterxml.jackson.core:jackson-databind")

    testImplementation("org.springframework.boot:spring-boot-starter-webflux")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

tasks.test {
    useJUnitPlatform()
}

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}


publishing {
    publications {
        create<MavenPublication>("maven") {

            groupId = "me.saro"
            artifactId = "spring-rest-web-client"
            version = version

            from(components["java"])

            repositories {
                maven {
                    credentials {
                        try {
                            username = project.property("sonatype.username").toString()
                            password = project.property("sonatype.password").toString()
                        } catch (e: Exception) {
                            println("warn: " + e.message)
                        }
                    }
                    val releasesRepoUrl = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
                    val snapshotsRepoUrl = uri("https://oss.sonatype.org/content/repositories/snapshots/")
                    url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
                }
            }

            pom {
                name.set("Spring RestWebClient")
                description.set("Rest WebClient in Spring")
                url.set("https://saro.me")

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        name.set("PARK Yong Seo")
                        email.set("j@saro.me")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/saro-lab/spring-rest-web-client.git")
                    developerConnection.set("scm:git:git@github.com:saro-lab/spring-rest-web-client.git")
                    url.set("https://github.com/saro-lab/spring-rest-web-client")
                }
            }
        }
    }
}

signing {
    sign(publishing.publications["maven"])
}

tasks.withType<Javadoc>().configureEach {
    options {
        this as StandardJavadocDocletOptions
        addBooleanOption("Xdoclint:none", true)
    }
}

