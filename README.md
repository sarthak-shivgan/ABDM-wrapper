# ABDM-Wrapper
This project is created to solve the challenges and issues faced by integrators to bring their systems into ABDM ecosystem.
Wrapper tries to abstract complex workflows and algorithms exposing clean and simple interfaces for integrators.

## Discovery and User-Initiated Linking
Click [here](src/main/java/com/nha/abdm/wrapper/hip/hrp/discover/README.md) to get details on testing this workflow.
## HIP-Initiated Linking
Click [here](src/main/java/com/nha/abdm/wrapper/hip/hrp/link/hipInitiated/README.md) to get details on testing this workflow.

## Pre-requisite to start the application
- Start Mongodb server and make sure that the port and database name matches with those provided in application.properties
- Provide your credentials(clientId and clientSecret) in application.properties

## Sample HIP
Click [here](sample-hip/README.md) to get details on how to run sample hip application. 

## Docker
Instead of running docker command to bring mongodb container up, you can bring both wrapper and mongodb services up by running these commands 
(Pre-requisites: You need to have docker and docker-compose installed on your system):
- ``gradle build``
- ``docker-compose up``

## Spotless
To apply spotless plugin run ```gradle spotlessApply```