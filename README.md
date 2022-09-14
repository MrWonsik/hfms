# Home Finances Management System

This is a backend part of the application that allows managing your home finances.
The frontend part is available [here](https://github.com/MrWonsik/hfms-front) - under that link, there are also some demo photos of the application.

## Technologies:

* Java 15
* Spring + Spring Boot
* REST API
* Lombok
* PostgreSQL
* jUnit

## Required properties
Properties that must be set:
* DB_IP - database physical address
* DB_PORT - database port (default database is postgres)
* DB_NAME - database name
* DB_USER - database user
* DB_PASSWORD - database user's password
* CREATE_EXAMPLE_USER - true of false, if you want to create example user.
* STORAGE_PATH - physical path to storage receipt files
* DDL_AUTO - equivalent spring.jpa.hibernate.ddl-auto