package ie.sds.resultsDiscovery.controller;

import ie.sds.resultsDiscovery.core.CallPatientWorkItem;
import ie.sds.resultsDiscovery.core.Patient;
import ie.sds.resultsDiscovery.service.CallPatientQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;
import java.util.logging.Logger;

@RestController(value = "/results")
public class ResultsDiscoveryController {
    private final DiscoveryClient discoveryClient;
    private final CallPatientQueue callQueue;
    private final Logger logger = Logger.getLogger(ResultsDiscoveryController.class.getSimpleName());

    @Autowired
    public ResultsDiscoveryController(DiscoveryClient discoveryClient, CallPatientQueue callQueue) {
        this.discoveryClient = discoveryClient;
        this.callQueue = callQueue;
    }

    @PutMapping
    public ResponseEntity<String> addResult(@RequestBody Patient patient) {
        // pass the result to the Patient Info Service
        // todo extract this logic out into a @Service class
        List<ServiceInstance> serviceInstances = discoveryClient.getInstances("patient-info");
        if (serviceInstances.isEmpty()) {
            logger.warning(String.format("No service instances of type %s could be found.", "patient-info"));
            return new ResponseEntity<>("Couldn't access a PatientInfoService.", HttpStatus.BAD_GATEWAY);
        }
        ServiceInstance patientInfoService = serviceInstances.get(0);

        URI serviceURI = patientInfoService.getUri();
        RestTemplate template = new RestTemplate();
        // todo negotiate this with Ronan
        //  Check what should be the return object
        //  Should contain a link to new Patient object in the Service e.g. (patientinfo/{patientId}}
        template.postForObject(serviceURI, patient, Object.class);
        URI newResourceLocation = URI.create("https://www.google.com/");

        // queue the result for a call
        callQueue.add(patient);

        // return a link to the new PatientInfo resource (In the PatientInfoService)
        return ResponseEntity.noContent()
                .location(newResourceLocation)
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
