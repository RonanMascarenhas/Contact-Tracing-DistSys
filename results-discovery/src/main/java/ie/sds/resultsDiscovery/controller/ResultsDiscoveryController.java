package ie.sds.resultsDiscovery.controller;

import ie.sds.resultsDiscovery.service.DomainNameService;
import ie.sds.resultsDiscovery.service.PatientResultsCallQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import service.core.Names;
import service.core.Patient;
import service.exception.NoSuchServiceException;
import service.messages.PatientResultCallWorkItem;

import java.net.URI;
import java.util.Objects;

@RestController
@RequestMapping("/result")
public class ResultsDiscoveryController {
    private final DomainNameService dns;
    private final PatientResultsCallQueue callQueue;
    private final Logger logger = LoggerFactory.getLogger(ResultsDiscoveryController.class);

    @Autowired
    public ResultsDiscoveryController(DomainNameService dns, PatientResultsCallQueue callQueue) {
        this.dns = dns;
        this.callQueue = callQueue;
    }

    private RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    /**
     * Takes an existing {@code ResponseEntity<Patient>}, and copies its status, location
     * and body into a new {@code ResponseEntity<Patient>},
     *
     * @param patientResponse the {@code ResponseEntity<Patient>} you wish to refresh
     * @return the refreshed {@code ResponseEntity<Patient>}
     */
    private static ResponseEntity<Patient> refreshResponseEntity(ResponseEntity<Patient> patientResponse) {
        ResponseEntity.BodyBuilder builder = ResponseEntity.status(patientResponse.getStatusCode());
        if (patientResponse.getStatusCode().is2xxSuccessful()) {
            builder.location(
                    Objects.requireNonNull(patientResponse.getHeaders().getLocation(),
                            String.format("Null location from %s", Names.PATIENT_INFO)
                    )
            );
        }
        return builder.body(patientResponse.getBody());
    }

    private URI getPatientInfoEndpoint() throws NoSuchServiceException {
        URI patientInfoServiceURI = dns.find(Names.PATIENT_INFO)
                .orElseThrow(dns.getServiceNotFoundSupplier(Names.PATIENT_INFO));
        return patientInfoServiceURI.resolve("/patientinfo");
    }

    private ResponseEntity<Patient> getExistingPatient(String phoneNumber, RestTemplate template)
            throws NoSuchServiceException {
        String thisPatientEndpoint = getPatientInfoEndpoint() + "/" + phoneNumber;
        return template.getForEntity(thisPatientEndpoint, Patient.class);
    }

    @PostMapping
    public ResponseEntity<Patient> addResultForPatient(@RequestBody Patient patient)
            throws NoSuchServiceException {
        // find the patient-info service
        // check if the Patient exists already
        RestTemplate template = getRestTemplate();
        URI patientInfoEndpoint = getPatientInfoEndpoint();
        ResponseEntity<Patient> patientResponse = getExistingPatient(patient.getPhoneNumber(), template);
        boolean existingPatientFound = patientResponse.getStatusCode().equals(HttpStatus.OK);

        // if not, add it
        if (!existingPatientFound) {
            logger.debug(String.format("Adding %s to %s", patient, patientInfoEndpoint));
            patientResponse = template.postForEntity(patientInfoEndpoint, patient, Patient.class);
        }

        // Add a new CallPatientResultWorkItem to the queue
        HttpStatus status = patientResponse.getStatusCode();
        if (status.is2xxSuccessful()) {
            logger.debug(String.format("Queueing %s for a Results Call", patient));
            callQueue.add(patientResponse.getBody());
        } else logger.warn(String.format("Service at %s returned status %s", patientInfoEndpoint, status));

        logger.debug("Finished in 'POST /result'");
        return refreshResponseEntity(patientResponse);
    }

    @GetMapping("/workitem")
    public ResponseEntity<PatientResultCallWorkItem> getCallPatientWorkItem() {
        if (callQueue.isEmpty()) {
            logger.info(String.format("No items in %s", Names.PATIENT_RESULTS_CALL_WI_QUEUE));
            return ResponseEntity.noContent().build();
        }
        PatientResultCallWorkItem workItem = callQueue.remove();
        logger.info("Finished in 'GET /result/workitem'");
        return ResponseEntity.ok(workItem);
    }

    @PostMapping("/workitem")
    public ResponseEntity<String> editWorkItem(@RequestBody PatientResultCallWorkItem workItem) {
        // check workItem
        //  not done? add it back to the queue
        if (workItem.getStatus() != PatientResultCallWorkItem.Status.DONE) {
            logger.info(String.format("Adding PatientResultWorkItem with id=%s back to the queue", workItem.getPatientId()));
            callQueue.add(workItem);
        }

        logger.info("Finished in 'POST /result/workitem'");
        return ResponseEntity.ok().build();
    }
}
