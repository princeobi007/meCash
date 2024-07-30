Please create a `env.properties` file in the root of the application

Provide the following properties in the `env.properties` file

```agsl
DB_USER=<YOUR_DB_USER>
DB_NAME=<YOUR_DB_NAME>
DB_PASSWORD=<DB_PASSWORD>
JWT_SECRET=<YOUR_SECRET>
ADMIN_PASS=<PASSWORD_FOR_ADMIN>
```
Design notes can be found [here](docs/Design.md)

AccountNumber is generated using a ```account_number_seq``` this should be created in you db using the below script:

```agsl
CREATE SEQUENCE public.account_number_seq
    INCREMENT 1
    START 1000000000
    MAXVALUE 9999999991;

ALTER SEQUENCE public.account_number_seq
    OWNER TO <YOUR_DB_USER>;
```

## How to run
The app can be run either clicking the ```bootRun``` gradle task (if you are using intellij IDE) or by running the following command 
```agsl
$ ./gradlew bootRun
```

## API Documentation
Swagger: Access the Swagger API documentation at: http://localhost:8080/mecash-docs/swagger-ui-custom.html when the application is running.

(Note: to test authenticated endpoint, grab the returned JWT token from the login endpoint, then just click on the Authorize button and paste the token)
