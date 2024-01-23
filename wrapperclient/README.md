## Steps to use Sample Wrapper Client

- Create a valid yaml file of OpenAPI 3.0 spec. You can use https://editor.swagger.io/ for this. And place this yaml
  under`specs` folder. e.g. `hip-facade.yaml`
- build.gradle > openApiGenerate > mention this file name in inputSpec and you can provide some output folder name e.g. 
  `generated` as outputSpec
- Run command ``gradle openApiGenerate``. This should generate client code required to invoke your api inside given output folder/
- setting.gradle > add `include 'generated'`
- Add `implementation project(':generated')` to build.gradle dependencies.
- Now in Main.java create request parameters and provide in respective API's method e.g. `PatientsApi.upsertPatients(patients)`
- Run ABDM wrapper by going to root directory and issuing command ``gradle bootRun``
- Now run command ``./gradlew run``. It should invoke respective api on wrapper.