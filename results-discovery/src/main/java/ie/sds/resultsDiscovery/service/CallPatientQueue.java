package ie.sds.resultsDiscovery.service;

import ie.sds.resultsDiscovery.core.CallPatientWorkItem;
import org.springframework.stereotype.Service;

/**
 * A wrapper for a Message-Oriented Middleware Queue
 */
@Service
public interface CallPatientQueue {
    /**
     * @param workItem
     */
    void add(CallPatientWorkItem workItem);

    /**
     * @return
     */
    CallPatientWorkItem remove();
}
