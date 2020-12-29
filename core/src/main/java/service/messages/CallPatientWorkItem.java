package service.messages;

import java.io.Serializable;
import java.time.Clock;
import java.time.Instant;

// todo take another look at this class
public class CallPatientWorkItem implements Serializable {
    private String patientId;
    private Instant created = Clock.systemUTC().instant();
    private Instant lastAccessed;
    private Status status = Status.TODO;

    public CallPatientWorkItem() {
    }

    public CallPatientWorkItem(String patientId) {
        this.patientId = patientId;
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
        TODO("To Do"),
        IN_PROGRESS("In Progress"),
        DONE("Done");

        private String readable;

        Status(String readable) {
            this.readable = readable;
        }

        @Override
        public String toString() {
            return readable;
        }
    }
}
