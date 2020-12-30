package ie.sds.resultsDiscovery.service;

import ie.sds.resultsDiscovery.core.Patient;
import ie.sds.resultsDiscovery.core.PatientResultWorkItem;

/**
 * A wrapper for a Message-Oriented Middleware Queue
 */
public interface PatientResultsCallQueue {
    /**
     * @param workItem
     */
    void add(PatientResultWorkItem workItem);

    /**
     * @param patient
     */
    void add(Patient patient);

    /**
     * @param workItem
     */
    void addWithPriority(PatientResultWorkItem workItem);

    /**
     * @return
     */
    PatientResultWorkItem remove();

    /**
     *
     */
    boolean isEmpty();
}
