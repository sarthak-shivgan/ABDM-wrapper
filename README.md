# ABDM-Wrapper
## HIP Wrapper
The HIP Wrapper consists of FACADE And HRP, FACADE for interacting with HIP's/Facility and HRP for interacting with ABDM gateway.

- This Module consists of User Initiated Linking, which is handled by HRP.

- The HRP has the CareContexts and basic demographic details of the patient in the wrappers DB.
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

After creating the ABHA Address, your id looks like "yourAbha@sbx"
```

    

### 3. Install MongoDB Compass - GUI
>https://www.mongodb.com/try/download/compass

### 4. Install MongoDb Community Server
>https://www.mongodb.com/try/download/community

### 5. Create a dataBase "ABDM_WRAPPER" with a collection "patient"
### 6. Add careContext in patient collection of "ABDM_WRAPPER"
- Since initially wrapper doesn't have any care context, add the below JSON document into **"patient"** collection
- patientReference should be unique either integer or String.

```{
"_id": {
"$oid": "65831b2c6620dc37541f8ea8"
},
"name": "your name",
"gender": "M",
"dateOfBirth": "your DOB in yyyy-mm-dd",
"patientReference": "your patient reference",
"patientDisplay": "your wanna be display name",
"_class": "com.nha.abdm.wrapper.hip.hrp.database.mongo.tables.Patient",
"careContext": [
{
"referenceNumber": " test visit-f9af123c-a723-45c2-b50b-617f4b1a5be0",
"display": "Consultation on Mon Jul 31 2023 08:18:37 GMT+0530 (India Standard Time)",
"_class": "com.nha.abdm.wrapper.hrp.hipInitiatedLinking.responses.LinkRecordsResponse$CareContext",
"isLinked": false
},
{
"referenceNumber": "test visit-f9af123c-a823-49c2-b50b-617f4b1a5be0",
"display": "Consultation on Mon Jul 31 2023 08:18:37 GMT+0530 (India Standard Time)",
"isLinked": false
},
{
"referenceNumber": "test visit-f7ag123c-a723-45c2-b50b-617f4b1a5be0",
"display": "Consultation on Mon Jul 31 2023 08:18:37 GMT+0530 (India Standard Time)",
"isLinked": false
},
{
"referenceNumber": " test visit-f9af123c-a723-46c2-b50b-617f4b1a5be5",
"display": "Consultation on Mon Jul 31 2023 08:18:37 GMT+0530 (India Standard Time)",
"isLinked": false
},
{
"referenceNumber": " test visit-f9af113c-a723-45c2-b50b-617f4b1a5ie0",
"display": "Consultation on Mon Jul 31 2023 08:18:37 GMT+0530 (India Standard Time)",
"isLinked": false
}
],
"patientMobile": "your mobile number",
"abhaAddress": "yourAbhaAddress@sbx"
}
```

### 7. Setup call back server using clientId and clientSecret.
```
API for getting accessToken.

curl --location 'https://dev.abdm.gov.in/gateway/v0.5/sessions' \
--header 'Content-Type: application/json' \
--data '{
     "clientId": "your_ClientId",
    "clientSecret": "your_ClientSecret"
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
- Search the HIP in PHR app : your_facility (name of your registered facility)
- The wrapper responses with a set of careContexts to the PHR
- Select few / all careContexts and click **"Link Records"**
- For OTP if the facility sends OTP enter it, else enter a dummy otp ie: "**123456**"
- After confirmation, a message will be displayed  saying **"Successfully Linked"**.



## Spotless
To apply spotless plugin run ```gradle spotlessApply```