plugins {
  id("uk.gov.justice.hmpps.gradle-spring-boot") version "3.3.14"
  kotlin("plugin.spring") version "1.5.31"
  kotlin("plugin.jpa") version "1.5.31"
}

configurations {
  testImplementation { exclude(group = "org.junit.vintage") }
}

dependencies {
  annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

  // Spring boot dependencies
  implementation("org.springframework.boot:spring-boot-starter-webflux")
  implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
  implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation("org.springframework.boot:spring-boot-starter-mail")
  implementation("org.springframework.boot:spring-boot-starter-data-redis")

  // Database dependencies
  runtimeOnly("org.flywaydb:flyway-core")
  runtimeOnly("org.postgresql:postgresql:42.3.1")

  implementation("com.google.code.gson:gson:2.8.9")
  implementation("io.jsonwebtoken:jjwt:0.9.1")
  implementation("com.google.zxing:core:3.4.1")
  implementation("com.google.zxing:javase:3.4.1")
  implementation("net.sourceforge.barbecue:barbecue:1.5-beta1")
  implementation("net.sf.barcode4j:barcode4j:2.1")

  // OpenAPI
  implementation("org.springdoc:springdoc-openapi-ui:1.5.12")
  implementation("org.springdoc:springdoc-openapi-data-rest:1.5.12")
  implementation("org.springdoc:springdoc-openapi-kotlin:1.5.12")

  // Test dependencies
  testImplementation("com.github.tomakehurst:wiremock-standalone:2.27.2")
  testImplementation("org.springframework.security:spring-security-test")
  testImplementation("org.awaitility:awaitility-kotlin:4.1.1")
  testImplementation("net.javacrumbs.json-unit:json-unit-assertj:2.28.0")
  testImplementation("io.swagger.parser.v3:swagger-parser-v2-converter:2.0.28")
  testImplementation("org.mockito:mockito-inline:4.0.0")
  testImplementation("io.projectreactor:reactor-test")
  testImplementation("it.ozimov:embedded-redis:0.7.3")
  testImplementation("org.testcontainers:postgresql:1.16.2")
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
