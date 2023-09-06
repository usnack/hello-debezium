plugins {
	java
	id("org.springframework.boot") version "3.1.3"
	id("io.spring.dependency-management") version "1.1.3"
}

group = "com.hello"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}


val	DEBEZIUM_VERSION = "2.3.2.Final"

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-docker-compose")
	runtimeOnly("com.mysql:mysql-connector-j")
	runtimeOnly("com.oracle.database.jdbc:ojdbc8")
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	annotationProcessor("org.projectlombok:lombok")
	testAnnotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("org.testcontainers:mysql")
	testImplementation("org.testcontainers:oracle-xe")
	// https://mvnrepository.com/artifact/org.slf4j/slf4j-api
	implementation("org.slf4j:slf4j-api")

	// https://mvnrepository.com/artifact/io.debezium/debezium-api
	implementation("io.debezium:debezium-api:${DEBEZIUM_VERSION}")
	// https://mvnrepository.com/artifact/io.debezium/debezium-embedded
	testImplementation("io.debezium:debezium-embedded:${DEBEZIUM_VERSION}")
	// https://mvnrepository.com/artifact/io.debezium/debezium-connector-oracle
	implementation("io.debezium:debezium-connector-oracle:${DEBEZIUM_VERSION}")
	implementation("io.debezium:debezium-connector-mysql:${DEBEZIUM_VERSION}")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
