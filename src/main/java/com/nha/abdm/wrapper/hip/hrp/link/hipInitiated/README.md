# HIP Initiated Linking
## HIP Wrapper
The HIP Wrapper consists of FACADE And HRP, FACADE for interacting with HIP's/Facility and HRP for interacting with ABDM gateway.

- This Module consists of HIP Initiated Linking which is handled by both Facade and HRP

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
### Testing of the HIP Initiated Linking

- Run the application using **gradle bootRun**.
- Since it is running locally the API the baseUrl will be localhost:{yourPort}
```
API to link records via HIP Initiated Linking.

curl --location 'localhost:8080/v1/care-contexts/link-records' \
--header 'Content-Type: application/json' \
--data-raw '{
    "requestId": "b847df83-caf7-4812-8193-fbe39e62b06f",
	"requesterId":"yourFacilityId",
    "abhaAddress":"yourAbhaAddress@sbx",
    "authMode":"DEMOGRAPHICS", //MOBILE_OTP
	"patient": {
            "careContexts": [
                {
                    "referenceNumber": "visit-21/1/2024",
                    "display": "visit on 21/1/2024"
                }
            ]
	}
}
'
```
- The requestId and status of initiation is provided in the response of link-records. 
- If the authMode in /link-records is MOBILE_OTP, The wrapper exposes an API verify-OTP.
```
API to authenticate the user with OTP.

curl --location 'localhost:8080/v1/care-contexts/verify-otp' \
--header 'Content-Type: application/json' \
--data '{
    "loginHint": "hipLinking",
    "requestId":"d9f13e8e-a965-466b-9d96-f0c7d4c8d8f6", 
    "authCode":"074749" 
}
'

- The requestId is from the response of /link-records.
- authCode is the OTP sent by ABDM

```

```
To check the status of linking with abhaAddress :
- Pass the requestId as a perameter in the url.
GET method

curl --location 'localhost:8080/v1/care-contexts/link-status/{requestId}'
```
- If the response of /get-status is successful, the careContexts are linked with abhaAddress successfully.
- The linked careContexts are visible on the PHR app as well as SMS will be sent to the user.
