package ie.sds.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.batch.api.chunk.ItemProcessor;

// todo check if ItemProcessor is appropriate here
public class PatientCallScheduler implements ItemProcessor {
    private static final Logger log = LoggerFactory.getLogger(PatientCallScheduler.class);

    @Override
    public Object processItem(Object o) throws Exception {
        // takes in PatientInfo or Result?

        // If it has not had a follow up call schedule a call => add it to the queue

        // Write the object back to the PatientInfoService, marked as having been called for contact tracing
        return null;
    }
}
