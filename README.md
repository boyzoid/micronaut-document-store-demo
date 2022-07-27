# Micronaut Demo with MySQL Document Store

A demo for using Micronaut to access a MySQL Document Store

## Setup

This setup assumes you already have access to a MySQL database.

* Copy the `src/main/resources/application-template.yml` file to `src/main/resources/application-dev.yml` and fill in the values for the database config under the `demo` block.
* Run the command ` ./gradlew run` in the main directory
* Make a GET call to `localhost:8080/init` to load data into the database.
