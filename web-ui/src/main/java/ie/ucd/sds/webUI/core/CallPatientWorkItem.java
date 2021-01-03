package ie.ucd.sds.webUI.core;

import java.time.Clock;
import java.time.Instant;

// todo this class could be refined once we decide what we want in it.
public class CallPatientWorkItem {
    private Patient patient;
    private String patientId;
    private Instant created = Clock.systemUTC().instant();
    private Instant lastAccessed;
    private Status status = Status.TODO;

    public CallPatientWorkItem() {
    }

    public CallPatientWorkItem(Patient patient) {
        this.patient = patient;
        this.patientId = patient.getId();
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getLastAccessed() {
        return lastAccessed;
    }

    public void setLastAccessed(Instant lastAccessed) {
        this.lastAccessed = lastAccessed;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public enum Status {
        TODO, IN_PROGRESS, DONE

//        todo add strings to enum constants in r-d-s
//        @Override
//        public String toString() {
//            String value;
//            switch (this) {
//                case TODO:
//                    value = "todo";
//                    break;
//                default:
//                    // todo implement this later if necessary
//                    value = "";
//                    break;
//            }
//            return value;
//        }
    }
}
