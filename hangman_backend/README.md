# Hangman Backend

> By Fabulous Stars

The Hangman backend is written in Java17 and runs on Google Cloud. It uses the following services:

* Cloud Datastore
* App Engine Standard

For documentation see:

* [java-datastore](https://cloud.google.com/appengine/docs/java/datastore/)
* [ae-docs](https://cloud.google.com/appengine/docs/standard/java-gen2/services/access)

## Running locally

This example uses the
[Cloud SDK Maven plugin](https://cloud.google.com/appengine/docs/java/tools/using-maven).
To run this server locally:

```
mvn package appengine:run
```

To see the results of the server application, open
[localhost:8080](http://localhost:8080) in a web browser.

## Deploying

### Appengibe Cron Configuration

Set cron to call */api/cleanup*.


### Maven

```
mvn clean package appengine:deploy
mvn appengine:deployIndex

```
