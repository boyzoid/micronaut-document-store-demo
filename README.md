# Micronaut Demo with MySQL Document Store

A demo for using Micronaut to access a MySQL Document Store

## Setup

This setup assumes you already have access to a MySQL database.
* 
* Open MySQL Shell and connect to your MySQL instance using the following command: `\c {user}:{password}@{host}:33060`

    * Where `{user}` is the username, `{password}` is the password, and `{host}` is the server domain name or IP address of your MySQL instance.
* In MySQL Shell, run the command `session.createSchema('mn_demo')` to create the new schema.
* In MySQL Shell, run the following command: `util.importJson( '/absolute/path/to/project/data/scores.json', {schema: 'mn_demo', collection: 'scores'})`

    * If the process runs successfully, you will see output similar to this:
      `Processed 12.65 MB in 17477 documents in 4.7405 sec (3.69K documents/s)  Total successfully imported documents 17477 (3.69K documents/s)`
* Run the command ` ./gradlew run` in the main directory