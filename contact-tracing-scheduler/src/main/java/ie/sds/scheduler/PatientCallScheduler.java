package ie.sds.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import service.core.Patient;
import service.messages.ContactTracingWorkItem;

/**
 * Maps patients to CallPatientWorkItems
 * <p>
 * This process occurs overnight, after a person has been diagnosed with Covid-19.
 * The generated WorkItem is queued for processing by a contact tracer.
 */
public class PatientCallScheduler implements ItemProcessor<Patient, ContactTracingWorkItem> {
    private static final Logger log = LoggerFactory.getLogger(PatientCallScheduler.class);

    @Override
    public ContactTracingWorkItem process(Patient patient) {
        log.info("Generating CallPatientWorkItem for Patient id=" + patient.getId());
        return new ContactTracingWorkItem(patient);
    }
}
