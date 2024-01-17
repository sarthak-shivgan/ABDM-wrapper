/* (C) 2024 */
package com.nha.abdm.wrapper.hip.hrp.discover;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nha.abdm.wrapper.common.ErrorResponse;
import com.nha.abdm.wrapper.common.RequestManager;
import com.nha.abdm.wrapper.common.Utils;
import com.nha.abdm.wrapper.common.models.CareContext;
import com.nha.abdm.wrapper.hip.hrp.common.requests.CareContextRequest;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.repositories.PatientRepo;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.services.RequestLogService;
import com.nha.abdm.wrapper.hip.hrp.database.mongo.tables.Patient;
import com.nha.abdm.wrapper.hip.hrp.discover.requests.*;
import com.nha.abdm.wrapper.hip.hrp.discover.responses.DiscoverResponse;
import info.debatty.java.stringsimilarity.JaroWinkler;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class DiscoveryService implements DiscoveryInterface {

  @Autowired PatientRepo patientRepo;
  @Autowired RequestManager requestManager;
  @Autowired RequestLogService requestLogService;
  ErrorResponse errorResponse = new ErrorResponse();
  JaroWinkler jaroWinkler = new JaroWinkler();

  @Value("${onDiscoverPath}")
  public String onDiscoverPath;

  private static final Logger log = LogManager.getLogger(DiscoveryService.class);

  @Override
  public void onDiscover(DiscoverResponse discoverResponse) {
    String receivedAbhaAddress = discoverResponse.getPatient().getId();
    String receivedPatientReference =
        discoverResponse.getPatient().getUnverifiedIdentifiers().isEmpty()
            ? null
            : discoverResponse.getPatient().getUnverifiedIdentifiers().get(0).getValue();
    String receivedYob = discoverResponse.getPatient().getYearOfBirth();
    String receivedGender = discoverResponse.getPatient().getGender();
    String receivedName = discoverResponse.getPatient().getName();

    try {
      Patient patientByAbhaAddress = patientRepo.findByAbhaAddress(receivedAbhaAddress);
      List<Patient> patientsByMobileNumber =
          patientRepo.findByPatientMobile(
              discoverResponse.getPatient().getVerifiedIdentifiers().get(0).getValue());
      Patient patientByReference =
          receivedPatientReference != null
              ? patientRepo.findByPatientReference(receivedPatientReference)
              : null;
      if (Objects.nonNull(patientByAbhaAddress)) {
        log.info("Patient matched with AbhaAddress");
        List<CareContext> careContexts =
            patientByAbhaAddress.getCareContexts().stream()
                .filter(context -> !context.isLinked())
                .collect(Collectors.toList());
        onDiscoverRequest(discoverResponse, patientByAbhaAddress, careContexts);
      } else if (!patientsByMobileNumber.isEmpty()) {
        Optional<Patient> matchingPatient =
            findMatchingPatient(
                patientsByMobileNumber,
                receivedPatientReference,
                receivedGender,
                receivedYob,
                receivedName);
        if (matchingPatient.isPresent()) {
          onDiscoverRequest(
              discoverResponse, matchingPatient.get(), matchingPatient.get().getCareContexts());
        } else {
          errorResponse.setCode(1000);
          errorResponse.setMessage("HIP -> Details mismatch with mobile");
          onDiscoverNoPatientRequest(discoverResponse, errorResponse);
        }
      } else if (Objects.nonNull(patientByReference)) {
        log.info("Patient matched with Patient Identifier");
        if (isGenderMatch(patientByReference, receivedGender)
            && (isYOBInRange(patientByReference, receivedYob)
                && (isFuzzyNameMatch(patientByReference, receivedName)))) {
          List<CareContext> careContexts =
              patientByReference.getCareContexts().stream()
                  .filter(context -> !context.isLinked())
                  .collect(Collectors.toList());
          onDiscoverRequest(discoverResponse, patientByReference, careContexts);
        } else {
          errorResponse.setCode(1000);
          errorResponse.setMessage("HIP : Details mismatch with patientIdentifier");
          onDiscoverNoPatientRequest(discoverResponse, errorResponse);
        }
      } else {
        errorResponse.setCode(1000);
        errorResponse.setMessage("HIP -> Patient Not found");
        onDiscoverNoPatientRequest(discoverResponse, errorResponse);
      }
    } catch (Exception e) {
      log.error("OnDiscover : " + Arrays.toString(e.getStackTrace()));
    }
  }

  private Optional<Patient> findMatchingPatient(
      List<Patient> isMobilePresent,
      String receivedPatientReference,
      String receivedGender,
      String receivedYob,
      String receivedName) {
    if (receivedPatientReference != null) {
      return isMobilePresent.stream()
          .filter(patient -> receivedPatientReference.equals(patient.getPatientReference()))
          .findFirst();
    } else {
      return isMobilePresent.stream()
          .filter(patient -> isGenderMatch(patient, receivedGender))
          .filter(patient -> isYOBInRange(patient, receivedYob))
          .filter(patient -> isFuzzyNameMatch(patient, receivedName))
          .findFirst();
    }
  }

  private void onDiscoverRequest(
      DiscoverResponse discoverResponse, Patient patient, List<CareContext> careContexts) {

    List<CareContextRequest> careContextList = new ArrayList<>();
    for (CareContext careContext : careContexts) {
      careContextList.add(
          CareContextRequest.builder()
              .referenceNumber(careContext.getReferenceNumber())
              .display(careContext.getDisplay())
              .build());
    }

    OnDiscoverPatient onDiscoverPatient =
        OnDiscoverPatient.builder()
            .referenceNumber(patient.getPatientReference())
            .display(patient.getDisplay())
            .onDiscoverCareContexts(careContextList)
            .matchedBy(Arrays.asList("MOBILE"))
            .build();

    OnDiscoverRequest onDiscoverRequest =
        OnDiscoverRequest.builder()
            .requestId(UUID.randomUUID().toString())
            .timestamp(Utils.getCurrentTimeStamp().toString())
            .transactionId(discoverResponse.getTransactionId())
            .onDiscoverPatient(onDiscoverPatient)
            .resp(Response.builder().requestId(discoverResponse.getRequestId()).build())
            .build();
    try {
      ResponseEntity<ObjectNode> responseEntity =
          requestManager.fetchResponseFromPostRequest(onDiscoverPath, onDiscoverRequest);
      log.info(onDiscoverPath + " : onDiscoverCall: " + responseEntity.getStatusCode());
      requestLogService.setDiscoverResponse(discoverResponse);
    } catch (Exception e) {
      log.info("Error: " + e);
    }
  }

  private void onDiscoverNoPatientRequest(
      DiscoverResponse discoverResponse, ErrorResponse errorResponse) {

    OnDiscoverErrorRequest onDiscoverErrorRequest =
        OnDiscoverErrorRequest.builder()
            .requestId(UUID.randomUUID().toString())
            .timestamp(Utils.getCurrentTimeStamp().toString())
            .transactionId(discoverResponse.getTransactionId())
            .error(errorResponse)
            .build();
    try {
      requestManager.fetchResponseFromPostRequest(onDiscoverPath, onDiscoverErrorRequest);
      log.info("Discover: requestId : " + discoverResponse.getRequestId() + ": Patient not found");
    } catch (Exception e) {
      log.error(e);
    }
  }

  private boolean isYOBInRange(Patient patient, String receivedYob) {
    int existingDate = Integer.parseInt(patient.getDateOfBirth().substring(0, 4));
    return Math.abs(existingDate - Integer.parseInt(receivedYob)) <= 5;
  }

  private boolean isGenderMatch(Patient patient, String receivedGender) {
    return receivedGender.equals(patient.getGender());
  }

  private boolean isFuzzyNameMatch(Patient patient, String receivedName) {
    return jaroWinkler.similarity(patient.getName(), receivedName) >= 0.5;
  }
}
