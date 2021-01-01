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

    // FIXME addResult name makes no sense
    @PostMapping
    public ResponseEntity<String> addResult(@RequestBody Patient patient) throws NoSuchServiceException {
        // pass the result to the Patient Info Service
        URI patientInfoServiceURI = dns.find(Names.PATIENT_INFO)
                .orElseThrow(dns.getServiceNotFoundSupplier(Names.PATIENT_INFO));

        // todo check if patient present already
        //  if not, Post, then return 201
        //  if so, Get, then return 200

        RestTemplate template = new RestTemplate();
        ResponseEntity<?> response = template.postForEntity(patientInfoServiceURI.resolve("/patientinfo"), patient, Object.class);
        int statusCode = response.getStatusCodeValue();

        // todo proliferate errors through the system. An error from patient-info should trickle back to the web-ui
        //  at present, errors are mutating into other errors

        // todo fix this to reflect today's conversation - check before adding
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

        logger.info("Finished in 'POST /result'");
        // todo rewire this to not throw an exception over the null location
        return ResponseEntity.status(response.getStatusCode())
                .location(Objects.requireNonNull(response.getHeaders().getLocation()))
                .build();
    }

    @GetMapping("/workitem")
    public ResponseEntity<PatientResultCallWorkItem> getCallPatientWorkItem() {
        if (callQueue.isEmpty()) {
            // todo use the constant in service.core.Name instead
            logger.info(String.format("No items in %s", Names.PATIENT_RESULTS_CALL_WI_QUEUE));
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
