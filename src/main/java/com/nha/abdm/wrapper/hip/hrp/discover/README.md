# Discover Care  Contexts
## HIP Wrapper
The HIP Wrapper consists of FACADE And HRP, FACADE for interacting with HIP's/Facility and HRP for interacting with ABDM gateway.

- This Module consists of User Initiated Linking, which is handled by HRP.

- The HRP has the Care Contexts and basic demographic details of the patient in the wrappers DB.
## Pre-requisites
### 1. ABHA SBX PHR App

> https://sandbox.abdm.gov.in/docs/phr_app


### 2. Creating ABHA Address

```
* Skip if ABHA Address already exits.

ABHA Address can be created using: 
- Mobile Number
- Aadhaar Number
- E-mail

After creating the ABHA Address, your id will look like "yourAbha@sbx"
```

### 3. Run MongoDb Community Server using docker
> docker pull mongodb/mongodb-community-server

> docker run --name mongo -p 27017:27017 -d mongodb/mongodb-community-server:latest

### 3. Install MongoDB Compass - GUI (optional) if you wish to read, insert, update records from GUI. 
>https://www.mongodb.com/try/download/compass

### 5. Create a database "abdm_wrapper" with a collection "patients"
### 6. Add careContext in patient collection of "abdm_wrapper"
- Since initially wrapper doesn't have any care context, add the below JSON document into **"patients"** collection
- patientReference should be unique either integer or String.

```
{
  "name": "patient's name",
  "gender": "M",
  "dateOfBirth": "DOB in yyyy-mm-dd",
  "patientReference": "patient's reference",
  "patientDisplay": "patient's display name",
  "careContext": [
    {
      "referenceNumber": " test visit-f9af123c-a723-45c2-b50b-617f4b1a5be0",
      "display": "Consultation on Mon Jul 31 2023 08:18:37 GMT+0530 (India Standard Time)",
      "isLinked": false
    },
    {
      "referenceNumber": "test visit-f9af123c-a823-49c2-b50b-617f4b1a5be0",
      "display": "Consultation on Mon Jul 31 2023 08:18:37 GMT+0530 (India Standard Time)",
      "isLinked": false
    }
  ],
  "patientMobile": "patient's mobile number",
  "abhaAddress": "patientAbhaAddress@sbx"
}
```

### 7. Setup call back server using clientId and clientSecret.
```
API for getting accessToken.

curl --location 'https://dev.abdm.gov.in/gateway/v0.5/sessions' \
--header 'Content-Type: application/json' \
--data '{
    "clientId": <client id provided>,
    "clientSecret": <client secret provided>
}'
```
```
API for registering bridge URL

curl --location --request PATCH 'https://dev.abdm.gov.in/devservice/v1/bridges' \
--header 'Authorization: Bearer your accessToken' \
--header 'Content-Type: application/json' \
--data '{
    "url": "https://stag.ngrok-free.app"
}'
```
```
API for registering Facility

curl --location --request PUT 'https://dev.abdm.gov.in/devservice/v1/bridges/addUpdateServices' \
--header 'Authorization: Bearer your accessToken' \
--header 'Content-Type: application/json' \
--data '[
    {
        "id": "Demo_Ajitesh_HIP",
        "name": "Demo Ajitesh HIP",
        "type": "HIP",
        "active": true,
        "alias": [
            "Demo_Ajitesh_HIP"
        ]
    }
]'
```
```
API to fetch the facility details.

curl --location 'https://dev.abdm.gov.in/devservice/v1/bridges/getServices' \
--header 'X-CM-ID: sbx' \
--header 'Authorization: Bearer your accessToken' \
--data ''

check for the bridgeUrl and facility in the response for confirmation.
```
### Testing of the Discovery Linking

- Run the application using **gradle bootRun**.

- After starting the server, Login into the PHR app using the details which are stored in DB which is your ABHA Address.
- Search the HIP in PHR app : `Linked Facility` > Click on `(+)` -> Search for the facility (name of the registered facility)
- Select the facility from searched results and then hit `Fetch Records`.
- The wrapper responses with a set of careContexts to the PHR
- Select few / all careContexts and click `Link Records`
- For OTP if the facility sends OTP enter it, else enter a dummy otp ie: "**123456**"
- After confirmation, a message will be displayed  saying **"Successfully Linked"**.