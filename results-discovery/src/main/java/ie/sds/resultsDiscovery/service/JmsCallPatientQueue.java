package ie.sds.resultsDiscovery.service;

import ie.sds.resultsDiscovery.core.CallPatientWorkItem;
import ie.sds.resultsDiscovery.core.Patient;
import org.springframework.stereotype.Service;

@Service
public class JmsCallPatientQueue implements CallPatientQueue {
    public JmsCallPatientQueue() {
        // todo set up the connection to the Message Broker
    }

    @Override
    public void add(CallPatientWorkItem workItem) {
        // todo remove this if it's not used
    }

    @Override
    public void add(Patient patient) {
        CallPatientWorkItem workItem = new CallPatientWorkItem(patient);

        // pass this to JMS
    }

    @Override
    public CallPatientWorkItem remove() {
        return null;
    }
}
