# CarbonTrack Backend

Spring Boot REST API using MySQL and cookie-based login sessions.

## Local development

Configure the MySQL connection using `SPRING_DATASOURCE_URL`,
`SPRING_DATASOURCE_USERNAME`, and `SPRING_DATASOURCE_PASSWORD`, or provide
`MYSQLHOST`, `MYSQLPORT`, `MYSQLDATABASE`, `MYSQLUSER`, and `MYSQLPASSWORD`.
Then run:

```sh
./mvnw spring-boot:run
```

On Windows, use `mvnw.cmd` instead of `./mvnw`.

## Deploy to Railway

1. Push the contents of this directory to
   `Visesha1710-source/carbontrack-backend`.
2. Create a Railway project from that repository. Railway builds the included
   `Dockerfile` and supplies the service `PORT`.
3. Add a MySQL service. In the backend service variables, reference the
   database service's `MYSQLHOST`, `MYSQLPORT`, `MYSQLDATABASE`, `MYSQLUSER`,
   and `MYSQLPASSWORD` values using Railway variable references, for example
   `${{MySQL.MYSQLHOST}}` (replace `MySQL` with your database service name).
4. Generate a public domain for the backend and set that origin as Vercel's
   `BACKEND_URL`.

The service also supports `SPRING_DATASOURCE_URL`,
`SPRING_DATASOURCE_USERNAME`, and `SPRING_DATASOURCE_PASSWORD` if you prefer
to configure the JDBC connection directly. Do not commit database
credentials.
