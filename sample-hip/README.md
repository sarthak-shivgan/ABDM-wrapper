## Steps to use Sample HIP

### Here are the apis exposed by wrapper which an HIP application needs to call:
- add-patients
- link-carecontexts
- verify-otp
- link-status/{requestId}

To do this, we have a way to use generated code based on Open API 3.0 spec apis.
HIP needs to implement get patient api and expose an endpoint:
- patients/{abhaAddress}

### A lot of pieces are already done like creating of spec and generating of code and a springboot application demonstrating how to use them. If you are starting from scratch, then follow below steps:

- Create a valid yaml file of OpenAPI 3.0 spec. You can use https://editor.swagger.io/ for this. And place this yaml
  under`specs` folder. e.g. `hip-facade.yaml`
- build.gradle > openApiGenerate > mention this file name in inputSpec, and you can provide some output folder name e.g. 
  `generated` as outputSpec
- Run command ``gradle openApiGenerate``. This should generate client code required to invoke your api inside given output folder/
- setting.gradle > add `include 'generated'`
- Add `implementation project(':generated')` to build.gradle dependencies.
- Now in `PatientController.java` create request parameters and provide in respective API's method e.g. `PatientsApi.upsertPatients(patients)`

#### Bring the applications up to test your changes.
- Run ABDM wrapper by going to root directory and issuing command ``gradle bootRun``
- Now change directory to sample-hip and run command ``gradle bootRun``. It should bring up sample hip application.
- You can test respective api methods by calling apis on PatientController.

### Please note that if you are making changes to specs/hip-facade.yaml, then you need to regenerate the source files using `gradle openApiGenerate`

### If you wish to generate client code in some other language, you can do so by specifying language at build.gradle > openApiGenerate() -> generatorName.set("java")