package ie.sds.resultsDiscovery.service;

import ie.sds.resultsDiscovery.core.CallPatientWorkItem;
import ie.sds.resultsDiscovery.core.Patient;

/**
 * A wrapper for a Message-Oriented Middleware Queue
 */
public interface PatientResultsCallQueue {
    /**
     * @param workItem
     */
    void add(CallPatientWorkItem workItem);

    /**
     * @param patient
     */
    void add(Patient patient);

    /**
     * @return
     */
    CallPatientWorkItem remove();

    /**
     *
     */
    boolean isEmpty();
}
