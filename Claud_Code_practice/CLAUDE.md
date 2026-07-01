# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Layout

The Maven project lives in `spring-practice/demo/` (not the repo root). Run all Maven commands from that directory.

- Main package: `com.example.demo` under `spring-practice/demo/src/main/java/com/example/demo/`
- Entry point: `spring-practice/demo/src/main/java/com/example/demo/DemoApplication.java`
- Config: `spring-practice/demo/src/main/resources/application.properties`
- Templates: `spring-practice/demo/src/main/resources/templates/` (Thymeleaf)
- Static assets: `spring-practice/demo/src/main/resources/static/`
- Tests: `spring-practice/demo/src/test/java/com/example/demo/`

This is currently a fresh Spring Boot skeleton generated from start.spring.io — no controllers, entities, or repositories have been added yet beyond the default `DemoApplication` class and its context-load test.

## Stack

- Java 21, Spring Boot 3.5.0 (Maven, `spring-boot-starter-parent`)
- Dependencies: Spring Web, Spring Data JPA, Thymeleaf, Validation, Actuator, MySQL connector (runtime), Lombok
- Build tool: Maven Wrapper (`mvnw`/`mvnw.cmd`) — use the wrapper rather than a system-installed Maven

## Commands

Run from `spring-practice/demo/`:

```cmd
./mvnw.cmd spring-boot:run          # run the app locally
./mvnw.cmd test                     # run all tests
./mvnw.cmd test -Dtest=ClassName    # run a single test class
./mvnw.cmd test -Dtest=ClassName#methodName   # run a single test method
./mvnw.cmd clean package            # build the jar (target/)

On non-Windows shells use `./mvnw` instead of `./mvnw.cmd`.

## Notes

- No datasource is configured in `application.properties` yet, even though the MySQL driver and Spring Data JPA are on the classpath — expect to add `spring.datasource.*` properties before any JPA repository will work.
- Lombok is present as an optional dependency and excluded from the runnable jar via the `spring-boot-maven-plugin` configuration in `pom.xml`; annotation processing is wired up explicitly in the `maven-compiler-plugin` execution.


## Testing

- Use JUnit 5
- Do not use Mockito unless explicitly required
- Keep tests under src/test/java/com/example/demo
