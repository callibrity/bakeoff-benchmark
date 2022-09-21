# Bake-Off Benchmark

This project will be used to benchmark different service implementations of the Bake-Off Service [OpenAPI Specification](openapi.json)

## The API

The API should support the following endpoints:

* `GET  /api/artists/`
* `POST /api/artists/`
* `GET  /api/artists/{id}`
* `PUT  /api/artists/{id}`
* `DELETE /api/artists/{id}`

## Implementation Instructions
All service implementations must:

* Be able to connect to a Postgres (version 14.5) database.
* Automatically create the schema necessary (with multi-instance support) to support the service's API.
* Provide a build that generates a container image (will be pushed to Docker )
* Be configurable via environment variables.

### Environment Variables

* DB_HOST - database host name
* DB_PORT - the database port
* DB_NAME - the name of the database
* DB_USER - the database username
* DB_PASS - the database password
* PORT - the port where the service should listen