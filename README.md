# prisoner-transactions-api

This service provides access API endpoints for prisoner transactions. This includes magic link generation and validation, barcode generation and barcode validation etc.
The main client is the prisoner-transactions (UI) service.
It is built as  docker image and deployed to the MOJ Cloud Platform.

# Dependencies

This service requires a postgresql database.

# Building the project

Tools required:

* JDK v16+
* Kotlin
* docker
* docker-compose

## Install gradle

`$ ./gradlew`

`$ ./gradlew clean build`

# Running the service

Start up the docker dependencies using the docker-compose file in the `create-and-vary-a-licence` service

There is a script to help, which sets local profiles, port and DB connection properties to the 
values required.

`$ ./run-local.sh`

Or, to run with default properties set in the docker-compose file

`$ docker-compose pull && docker-compose up`

Or, to use default port and properties

`$ SPRING_PROFILES_ACTIVE=dev ./gradlew bootRun`


# Running the unit tests

Unit tests mock all external dependencies and can be run with no dependent containers.

`$ ./gradlew test`

# Running the integration tests

Integration tests use Wiremock to stub any API calls required, and use a local postgres database which is started automatically. To start manually use command `docker-compose up prisoner-transactions-api-db` .

`$ ./gradlew integrationTest`

# Linting

`$ ./gradlew ktlintcheck`

# OWASP vulnerability scanning

`$ ./gradlew dependencyCheckAnalyze`

# Authorisation via Magic Link (CJSM users)

For the `create barcode` user story we verify users by sending a magic link to their CJSM email account. Once the user clicks the link we issue a JWT giving the user authorisation to use the create barcode function. 

## Signing the JWT

In order to sign the JWT generated for Magic Link users there are private/public keys saved in configuration properties `jwt.private.key` and `jwt.public.key`. A different public/private keypair is required locally and for each deployment environment.

To create a public/private keypair for an environment:
* Create a new directory to hold the keys, we'll call this `keys`, and `cd` into the directory.
* Run command `ssh-keygen -t rsa -m PEM`. When prompted enter filename `rsa-key` and leave the passphrase empty.
* Run command `ls` - you should see files `rsa-key` and `rsa-key.pub`
* To generate the public key run command `ssh-keygen -m PKCS8 -e` and when prompted enter the key `rsa-key`. This will produce a public key and print it out to screen. Copy the contents into new file `rsa-key.x509.public`.
* To generate the private key run command `openssl pkcs8 -topk8 -inform pem -in rsa-key -outform pem -nocrypt -out rsa-key.pkcs8.private`
* To convert the public key into a string we can use in a Kubernetes secret run command `cat rsa-key.x509.public | awk 'NR>2 {print last} {last=$0}' | tr -d '\n' | base64 | tr -d '\n'`.
* To convert the private key into a string we can use in a Kubernetes secret run command `cat rsa-key.pkcs8.private | awk 'NR>2 {print last} {last=$0}' | tr -d '\n' | base64 | tr -d '\n'`.
* We now need to save the keys into Kubernetes secrets for the environment. A guide for creating secrets can be found on Cloud Platforms documentation here: https://user-guide.cloud-platform.service.justice.gov.uk/documentation/deploying-an-app/add-secrets-to-deployment.html#adding-a-secret-to-an-application
* The public key should be saved in Kubernetes secret `prisoner-transactions-api` with key `JWT_PUBLIC_KEY`
* The private key should be saved in Kubernetes secret `prisoner-transactions-api` with key `JWT_PRIVATE_KEY`
