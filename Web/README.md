# Overview

Uses Spring Boot MVC with Thymeleaf for processing HTML templates. Thus,
[as per documentation](https://www.thymeleaf.org/doc/tutorials/3.1/thymeleafspring.html),
all `th:*` expressions are processed as Spring's own
[SpEL](https://docs.spring.io/spring-framework/reference/core/expressions/language-ref.html)
(not Thymeleaf's OGNL).

# Setup, build, and test

1. Install Docker 20+ and Java JDK 21+.

2. Git-checkout the project code.

This is enough to build by running `gradlew build` / `gradlew bootJar` in the project's root directory.

If you use Intellij IDEA, it should automatically run the initial project setup, including downloading all
dependencies etc., on first opening of the project.

# Running

Run locally with `gradlew bootRun`, or, in IntelliJ IDEA, use the green "run" icon at the `main()` function
line in the `Main.kt` file to create a run configuration.

## Deployment

The app is meant to be deployed and run as a Docker container, and has a prepared `Dockerfile` for this
purpose. It can be built and run with standard docker commands (`docker built -t senie .` and `docker run
senie`).

# Configuration

All app configuration is found in the `application.yaml` file in the project resource file, and can be 
overridden with correctly named environment variables as per Spring's 
[relaxed binding rules](https://docs.spring.io/spring-boot/reference/features/external-config.html#features.external-config.typesafe-configuration-properties.relaxed-binding).

The main configuration properties that need to be provided for every environment is the DB connection to the
relevant DB, using `DB_*` environment variables:

* `DB_HOST`
* `DB_PORT`
* `DB_NAME`
* `DB_USER`
* `DB_PASS`