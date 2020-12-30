package service.messages;

import service.entities.TestResult;

/**
 * A class to describe outstanding Contact Tracing Call tasks to be done.
 */
public class ContactTracingWorkItem extends WorkItem {
    private String patientId;
    private String name;
    private String phoneNumber;
    private TestResult result;

    public ContactTracingWorkItem() {
    }

    public ContactTracingWorkItem(String patientId, String name, String phoneNumber, TestResult result) {
        this.patientId = patientId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.result = result;
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

    public TestResult getResult() {
        return result;
    }

    public void setResult(TestResult result) {
        this.result = result;
    }
}
