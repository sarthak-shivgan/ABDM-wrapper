# ABDM-Wrapper
## HIP Wrapper
The HIP Wrapper consists of FACADE And HRP, FACADE for interacting with HIP's and HRP for interacting with ABDM gateway.

- This Module consists of User Initiated Linking, which is handled by HRP.

- The HRP has the CareContexts and basic demographic details of the patient in the wrappers DB.
## Pre-requisites
- Setup call back server using clientId and clientSecret.
```
Api for getting accessToken.

curl --location 'https://dev.abdm.gov.in/gateway/v0.5/sessions' \
--header 'Content-Type: application/json' \
--data '{
     "clientId": "your_ClientId",
    "clientSecret": "your_ClientSecret"
}'


```
```
Api for registering bridge URL

curl --location --request PATCH 'https://dev.abdm.gov.in/devservice/v1/bridges' \
--header 'Authorization: Bearer eyJhbGciOiJSUzI1NiIsInRuMRgiZHEER567YW-oiOw' \
--header 'Content-Type: application/json' \
--data '{
    "url": "https://stag.ngrok-free.app"
}'
```
```
Api for registering Facility

curl --location --request PUT 'https://dev.abdm.gov.in/devservice/v1/bridges/addUpdateServices' \
--header 'Authorization: Bearer HtNMgqeXw8YbiI4LKC3OSrLPM8dHEER567YW-oiOw' \
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
### Testing of the Discovery Linking

Since initially wrapper doesn't have any care context, add the below JSON document into MongoDb

```{
"_id": {
"$oid": "65831b2c6620dc37541f8ea8"
},
"name": "your name",
"gender": "M",
"dateOfBirth": "your DOB in yyyy-mm-dd",
"patientReference": "your patient reference",
"patientDisplay": "Venu",
"_class": "com.nha.abdm.wrapper.hrp.mongo.tables.Patient",
"careContext": [
{
"referenceNumber": " test visit-f9af123c-a723-45c2-b50b-617f4b1a5be0",
"display": "Consultation on Mon Jul 31 2023 08:18:37 GMT+0530 (India Standard Time)",
"_class": "com.nha.abdm.wrapper.hrp.hipInitiatedLinking.responses.LinkRecordsResponse$CareContext",
"isLinked": true
},
{
"referenceNumber": "test visit-f9af123c-a823-49c2-b50b-617f4b1a5be0",
"display": "Consultation on Mon Jul 31 2023 08:18:37 GMT+0530 (India Standard Time)",
"isLinked": true
},
{
"referenceNumber": "test visit-f7ag123c-a723-45c2-b50b-617f4b1a5be0",
"display": "Consultation on Mon Jul 31 2023 08:18:37 GMT+0530 (India Standard Time)",
"isLinked": true
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
- After starting the server, Login into the PHR app and using the details which are stored in DB.
- Search the HIP in PHR app : Demo Ajitesh HIP / your facility