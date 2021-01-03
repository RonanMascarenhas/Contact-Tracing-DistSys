package service.messages;

import service.core.Patient;
import service.core.Result;

import java.io.Serializable;
import java.time.Clock;
import java.time.Instant;

public abstract class PatientWorkItem implements Serializable {
    private Instant created = Clock.systemUTC().instant();
    private Instant lastAccessed = created;
    private Status status = Status.TODO;

    private String patientId;
    private String name;
    private String phoneNumber;
    private Result result;

    public PatientWorkItem(Patient patient) {
        // todo populate the fields from the patient once the Patient class has been finalized.
    }

    public PatientWorkItem() {
    }

    /**
     * Updates the status of this WorkItem and records the update time.
     *
     * @param status the new status of the WorkItem
     */
    public void updateStatus(Status status) {
        this.status = status;
        lastAccessed = Clock.systemUTC().instant();
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

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public enum Status {
        TODO("To Do"),
        IN_PROGRESS("In Progress"),
        DONE("Done");

        private String readable;

        Status(String readable) {
            this.readable = readable;
        }

        public String getReadable() {
            return this.readable;
        }
    }
}
