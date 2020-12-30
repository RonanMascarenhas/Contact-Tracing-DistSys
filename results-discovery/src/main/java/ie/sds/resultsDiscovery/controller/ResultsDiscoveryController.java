package ie.sds.resultsDiscovery.controller;

import ie.sds.resultsDiscovery.core.CallPatientWorkItem;
import ie.sds.resultsDiscovery.core.Patient;
import ie.sds.resultsDiscovery.service.CallPatientQueue;
import ie.sds.resultsDiscovery.service.DomainNameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import service.exception.NoSuchServiceException;

import java.net.URI;
import java.util.Objects;
import java.util.logging.Logger;

@RestController
@RequestMapping("/result")
public class ResultsDiscoveryController {
    private final DomainNameService dns;
    private final CallPatientQueue callQueue;
    private final Logger logger = Logger.getLogger(ResultsDiscoveryController.class.getSimpleName());

    @Autowired
    public ResultsDiscoveryController(DomainNameService dns, CallPatientQueue callQueue) {
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

        // todo Discuss with Ronan
        //  200 => resource updated
        //  201 => resource created
        //  422 => correct resource syntax, incorrect semantics
        // If there was an error
        if (response.getStatusCode().value() >= 300) {
            return ResponseEntity.unprocessableEntity().build();
        }

        // Otherwise all is well
        if (response.getStatusCode() == HttpStatus.CREATED) {
            // queue the result for a call
            callQueue.add(patient);
        }
        // todo negotiate this with Ronan
        //  Check what should be the return object
        //  Should contain a link to new Patient object in the Service e.g. (patientinfo/{patientId}}
        // return a link to the new PatientInfo resource (In the PatientInfoService)
        return ResponseEntity.status(response.getStatusCode())
                .location(Objects.requireNonNull(response.getHeaders().getLocation()))
                .build();
    }

    @GetMapping("/workitem")
    public ResponseEntity<CallPatientWorkItem> getCallPatientWorkItem() {
        CallPatientWorkItem workItem = callQueue.remove();
        return new ResponseEntity<>(workItem, HttpStatus.OK);
    }

    @PostMapping("/workitem")
    public ResponseEntity<String> editWorkItem(@RequestBody CallPatientWorkItem workItem) {
        // check workItem
        //  not done? add it back to the queue
        if (workItem.getStatus() != CallPatientWorkItem.Status.DONE) {
            callQueue.add(workItem);
        }

        return ResponseEntity.accepted().build();
    }
}
