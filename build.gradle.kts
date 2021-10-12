plugins {
  id("uk.gov.justice.hmpps.gradle-spring-boot") version "3.3.9"
  kotlin("plugin.spring") version "1.5.31"
}

configurations {
  testImplementation { exclude(group = "org.junit.vintage") }
}

dependencies {
  annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

  // Spring boot dependencies
  implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("org.springframework.boot:spring-boot-starter-webflux")
  implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
  implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation("org.springframework.boot:spring-boot-starter-actuator")
  implementation("org.springframework.boot:spring-boot-starter-mail")

  // GOVUK Notify:
  implementation("uk.gov.service.notify:notifications-java-client:3.17.2-RELEASE")

  // Database dependencies
  runtimeOnly("com.h2database:h2")
//  runtimeOnly("org.flywaydb:flyway-core")
  runtimeOnly("org.postgresql:postgresql:42.2.24")

  implementation("com.google.code.gson:gson:2.8.8")
  implementation("io.arrow-kt:arrow-core:1.0.0")
  implementation("io.jsonwebtoken:jjwt:0.9.1")

  // OpenAPI
  implementation("org.springdoc:springdoc-openapi-ui:1.5.11")
  implementation("org.springdoc:springdoc-openapi-data-rest:1.5.11")
  implementation("org.springdoc:springdoc-openapi-kotlin:1.5.11")

  // Test dependencies
  testImplementation("com.github.tomakehurst:wiremock-standalone:2.27.2")
  testImplementation("org.springframework.security:spring-security-test")
  testImplementation("org.awaitility:awaitility-kotlin:4.1.0")
  testImplementation("net.javacrumbs.json-unit:json-unit-assertj:2.28.0")
  testImplementation("io.swagger.parser.v3:swagger-parser-v2-converter:2.0.28")
  testImplementation("org.mockito:mockito-inline:4.0.0")
  testImplementation("io.projectreactor:reactor-test")
}

java {
  toolchain.languageVersion.set(JavaLanguageVersion.of(16))
}

tasks {
  withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
      jvmTarget = "16"
    }
  }
}
