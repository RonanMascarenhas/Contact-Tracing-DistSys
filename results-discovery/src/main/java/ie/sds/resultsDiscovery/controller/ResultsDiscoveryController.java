package ie.sds.resultsDiscovery.controller;

import ie.sds.resultsDiscovery.core.CallPatientWorkItem;
import ie.sds.resultsDiscovery.core.PatientResult;
import ie.sds.resultsDiscovery.service.CallPatientQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController(value = "/results")
public class ResultsDiscoveryController {
    private final DiscoveryClient discoveryClient;
    private final CallPatientQueue callQueue;

    @Autowired
    public ResultsDiscoveryController(DiscoveryClient discoveryClient, CallPatientQueue callQueue) {
        this.discoveryClient = discoveryClient;
        this.callQueue = callQueue;
    }

    @PutMapping(name = "/add")
    public ResponseEntity<Long> addResult(@RequestBody PatientResult result) {
        // pass the result to the Patient Info Service
        // queue the result for a call

        // return a link to the new PatientInfo resource (referencing the resource in the PatientInfo Service)
        return new ResponseEntity<>(1L, HttpStatus.CREATED);
    }

    @GetMapping("/workitem")
    public ResponseEntity<CallPatientWorkItem> getCallPatientWorkItem() {
        // get a workItem from the queue
        // return it
        return new ResponseEntity<>(new CallPatientWorkItem(), HttpStatus.OK);
    }

    @PostMapping("/workitem/edit")
    public ResponseEntity editWorkItem(@RequestBody CallPatientWorkItem workItem) {
        // check workItem
        //  done? do nothing
        //  not done? add it back to the queue
        return null;
    }
}
