package ie.sds.resultsDiscovery.service;

import ie.sds.resultsDiscovery.core.CallPatientWorkItem;

public class JmsCallPatientQueue implements CallPatientQueue {
    @Override
    public void add(CallPatientWorkItem workItem) {

    }

    @Override
    public CallPatientWorkItem remove() {
        return null;
    }
}
