
# credential-management-frontend

This is the frontend microservice for where the ROPC pages are.

## Endpoints

### Guidance

- **URL**: `/credential-management/guidance`
- **Method**: GET
- **Description**: This endpoint provides guidance based on the user's GNAP token for ROPC account management or creation.

## How to test
`sbt clean compile coverage test it/test coverageReport`


### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").