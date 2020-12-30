package service.messages;

import service.entities.Patient;

public class PatientResultCallWorkItem extends PatientWorkItem {
    public PatientResultCallWorkItem() {
        super();
    }

    public PatientResultCallWorkItem(Patient patient) {
        super(patient);
    }
}
