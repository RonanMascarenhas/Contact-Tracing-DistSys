package ie.sds.resultsDiscovery.service;

import service.core.Patient;
import service.messages.PatientResultCallWorkItem;

/**
 * A wrapper for a Message-Oriented Middleware Queue
 */
public interface PatientResultsCallQueue {
    /**
     * @param workItem
     */
    void add(PatientResultCallWorkItem workItem);

    /**
     * @param patient
     */
    void add(Patient patient);

    /**
     * @param workItem
     */
    void addWithPriority(PatientResultCallWorkItem workItem);

    /**
     * @return
     */
    PatientResultCallWorkItem remove();

    /**
     *
     */
    boolean isEmpty();
}
