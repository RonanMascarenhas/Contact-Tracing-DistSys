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

    @PostMapping
    public ResponseEntity<String> addResult(@RequestBody Patient patient) throws NoSuchServiceException {
        // pass the result to the Patient Info Service
        URI patientInfoServiceURI = dns.find("patient-info")
                .orElseThrow(dns.getServiceNotFoundSupplier("patient-info"));

        RestTemplate template = new RestTemplate();
        ResponseEntity<?> response = template.postForEntity(patientInfoServiceURI.resolve("patientinfo"), patient, Object.class);
        int statusCode = response.getStatusCodeValue();

        // todo Discuss with Ronan
        //  200 => resource updated
        //  201 => resource created
        //  422 => correct resource syntax, incorrect semantics
        // If there was an error
        if (statusCode >= 300) {
            logger.warn(String.format("Service at %s returned status %d: %s", patientInfoServiceURI, statusCode, response.getBody()));
            return ResponseEntity.unprocessableEntity().build();
        }

        // Otherwise all is well
        if (response.getStatusCode() == HttpStatus.CREATED) {
            // queue the result for a call
            logger.info(String.format("Queueing patient %s %s for a Results Call", patient.getFirstName(), patient.getSurname()));
            callQueue.add(patient);
        }

        // todo negotiate this with Ronan
        //  Check what should be the return object
        //  Should contain a link to new Patient object in the Service e.g. (patientinfo/{patientId}}
        // return a link to the new PatientInfo resource (In the PatientInfoService)
        logger.info("Finished in 'POST /result'");
        return ResponseEntity.status(response.getStatusCode())
                .location(Objects.requireNonNull(response.getHeaders().getLocation()))
                .build();
    }

    @GetMapping("/workitem")
    public ResponseEntity<PatientResultCallWorkItem> getCallPatientWorkItem() {
        if (callQueue.isEmpty()) {
            // todo use the constant in service.core.Name instead
            logger.info(String.format("No items in %s", "Patient_Results_Call_WorkItem_Queue"));
            return ResponseEntity.notFound().build();
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
            callQueue.addWithPriority(workItem);
        }

        logger.info("Finished in 'POST /result/workitem'");
        return ResponseEntity.ok().build();
    }
}
