package nha.abdm.hiu;

import com.nha.abdm.wrapper.client.api.ConsentApi;
import com.nha.abdm.wrapper.client.invoker.ApiException;
import com.nha.abdm.wrapper.client.model.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/v1")
public class HIUController {
    private static final String requestId = "876ad129-ffb9-4c7d-b5bc-e099577e7e99";

    @PostMapping({"/test-wrapper/consent-init"})
    public FacadeResponse initiateConsent() throws ApiException {
        InitConsentRequest initConsentRequest = new InitConsentRequest();
        initConsentRequest.setRequestId(requestId);
        initConsentRequest.setTimestamp(DateTimeFormatter.ISO_INSTANT.format(Instant.now()));

        ConsentRequest consentRequest = new ConsentRequest();
        Purpose purpose = new Purpose();
        purpose.setCode("CAREMGT");
        purpose.setText("string");
        consentRequest.setPurpose(purpose);


        IdRequest idRequest = new IdRequest();
        idRequest.setId("atul_kumar13@sbx");
        consentRequest.setPatient(idRequest);


        IdRequest idRequest2 = new IdRequest();
        idRequest2.setId("Predator_HIP");
        consentRequest.setHiu(idRequest2);

        ConsentRequester consentRequester = new ConsentRequester();
        consentRequester.setName("Some requesterss");
        ConsentRequestIdentifier consentRequestIdentifier = new ConsentRequestIdentifier();
        consentRequestIdentifier.setSystem("https://www.mciindia.org");
        consentRequestIdentifier.setType("REG_NO");
        consentRequestIdentifier.setValue("MH1001");
        consentRequester.setIdentifier(consentRequestIdentifier);

        consentRequest.setRequester(consentRequester);

        List<String> hiTypes = new ArrayList<>();
        hiTypes.add("OPConsultation");

        consentRequest.setHiTypes(hiTypes);

        Permission permission = new Permission();
        permission.setAccessMode("VIEW");

        DateRange dateRange = new DateRange();
        dateRange.setFrom("2021-09-25T12:52:34.925Z");
        dateRange.setTo("2024-02-04T12:52:34.925Z");
        permission.setDateRange(dateRange);

        permission.setDataEraseAt("2024-11-25T12:52:34.925Z");

        Frequency frequency = new Frequency();
        frequency.setUnit("HOUR");
        frequency.setValue(1);
        frequency.setRepeats(0);
        permission.setFrequency(frequency);

        consentRequest.setPermission(permission);

        initConsentRequest.setConsent(consentRequest);

        ConsentApi consentApi = new ConsentApi();
        return consentApi.initConsent(initConsentRequest);
    }

    @GetMapping({"/test-wrapper/consent-status"})
    public ConsentStatusResponse consentStatus() throws ApiException {

        ConsentApi consentApi = new ConsentApi();
        return consentApi.consentStatusRequestIdGet(requestId);
    }
}
