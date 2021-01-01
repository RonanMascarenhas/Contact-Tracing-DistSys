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
    private String firstName;
    private String surname;
    private String phoneNumber;
    private Result result;

    public PatientWorkItem(Patient patient) {
        this.patientId = patient.getId();
        this.firstName = patient.getFirstName();
        this.surname = patient.getSurname();
        this.phoneNumber = patient.getPhoneNumber();
        this.result = patient.getResult();
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
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

        @Override
        public String toString() {
            return readable;
        }
    }
}
