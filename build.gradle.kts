import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.net.HttpURLConnection
import java.net.URI
import java.util.Base64

plugins {
    val kotlinVersion = "2.3.0-Beta2"
    val springBootVersion = "4.0.0-M3" // !! sync springBootVersion vals
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

val springBootVersion = "4.0.0" // !! sync springBootVersion vals
val minorVersion: Int = 0
val projectGroupId = "me.saro"
val projectArtifactId = "spring-rest-web-client"
val projectVersion = "$springBootVersion.$minorVersion"

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
        languageVersion.set(JavaLanguageVersion.of(23))
    }
    withJavadocJar()
    withSourcesJar()
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_23)
    }
}

tasks.test {
    useJUnitPlatform()
    //jvmArgs = listOf("-Dspring.profiles.active=test", "-XX:+EnableDynamicAgentLoading")
}

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_23
    targetCompatibility = JavaVersion.VERSION_23
}

publishing {
    publications {
        create<MavenPublication>("maven") {

            groupId = projectGroupId
            artifactId = projectArtifactId
            version = projectVersion

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
                    name = "ossrh-staging-api"
                    url = uri("https://ossrh-staging-api.central.sonatype.com/service/local/staging/deploy/maven2/")
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

tasks.named("publish").configure {
    doLast {
        println("Ready, upload to Central Portal")
        val username = project.property("sonatype.username").toString()
        val password = project.property("sonatype.password").toString()
        val connection = URI.create("https://ossrh-staging-api.central.sonatype.com/manual/upload/defaultRepository/$projectGroupId").toURL().openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString("$username:$password".toByteArray()))
        connection.setRequestProperty("Content-Type", "application/json")
        connection.doOutput = true
        connection.outputStream.write("""{"publishing_type": "automatic"}""".toByteArray())
        val responseCode = connection.responseCode
        if (responseCode in 200..299) {
            println("Successfully uploaded to Central Portal")
        } else {
            throw GradleException("Failed to upload to Central Portal: $responseCode - ${connection.inputStream?.bufferedReader()?.readText()}")
        }
    }
}

