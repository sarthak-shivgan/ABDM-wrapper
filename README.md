This project is created to solve the challenges and issues faced by integrators to bring their systems into ABDM ecosystem.
Wrapper tries to abstract complex workflows and algorithms exposing clean and simple interfaces for integrators.

## Pre-requisites
### 1. Install ABHA SBX PHR App on your mobile.

> https://sandbox.abdm.gov.in/docs/phr_app


### 2. Create ABHA Address

```
* Skip if ABHA Address already exists.

ABHA Address can be created using: 
- Mobile Number
- Aadhaar Number
- E-mail

After creating the ABHA Address, your id should look like "yourAbha@sbx"
```

### 3. Tech Stack required to be installed on your system:
- docker
- docker-compose
- jdk 17

### 4. Register bridge (hostUrl) with ABDM for callbacks.
1. Get Access Token.
```
curl --location 'https://dev.abdm.gov.in/gateway/v0.5/sessions' \
--header 'Content-Type: application/json' \
--data '{
    "clientId": <client id provided>,
    "clientSecret": <client secret provided>
}'
```
2. Register bridge url
```
curl --location --request PATCH 'https://dev.abdm.gov.in/gateway/v1/bridges' \
--header 'Authorization: Bearer <your accessToken>' \
--header 'Content-Type: application/json' \
--data '{
    "url": <your bridge url>
}'
```
### 5. Provide your credentials
- Provide clientId and clientSecret in [application.properties](src/main/resources/application.properties)

## Bring the application up.
```
docker-compose up
```

## Sample HIP
Click [here](https://github.com/NHA-ABDM/ABDM-wrapper/wiki/Sample-HIP) for more details on this.

## Test Discovery and User-Initiated Linking
Click [here](https://github.com/NHA-ABDM/ABDM-wrapper/wiki/Test-Discovery-and-User-Initiated-Linking) to get more details on this.

## [Developer Guide](https://github.com/NHA-ABDM/ABDM-wrapper/wiki/Developer-guide)