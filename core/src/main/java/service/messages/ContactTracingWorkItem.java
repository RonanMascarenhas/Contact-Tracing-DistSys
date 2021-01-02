package service.messages;

import service.core.Patient;

/**
 * A class to describe outstanding Contact Tracing Call tasks to be done.
 */
public class ContactTracingWorkItem extends PatientWorkItem {
    public ContactTracingWorkItem() {
        super();
    }

    public ContactTracingWorkItem(Patient patient) {
        super(patient);
    }
}
