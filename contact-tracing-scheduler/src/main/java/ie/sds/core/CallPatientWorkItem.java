package ie.sds.core;

import java.io.Serializable;
import java.time.Clock;
import java.time.Instant;

// todo this class could be refined once we decide what we want in it.
public class CallPatientWorkItem implements Serializable {
    private Patient patient;
    private Instant created = Clock.systemUTC().instant();
    private Instant lastAccessed;
    private Status status = Status.TODO;

    public CallPatientWorkItem() {
    }

    public CallPatientWorkItem(Patient patient) {
        this.patient = patient;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
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
        TODO, IN_PROGRESS, DONE;

        @Override
        public String toString() {
            String value;
            switch (this) {
                case TODO:
                    value = "todo";
                    break;
                default:                     // todo implement this later if necessary
                    value = "";
                    break;
            }
            return value;
        }
    }
}
