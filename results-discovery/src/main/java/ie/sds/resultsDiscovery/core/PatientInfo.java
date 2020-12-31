package ie.sds.resultsDiscovery.core;

public class PatientInfo {

    private String firstName;
    private String lastName;
    private String phoneNumber;

    public PatientInfo() {
    }

    public PatientInfo(String id, String firstName, String lastName, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return String.format("PatientInfo[firstName='%s', lastName='%s', phoneNumber='%s']", firstName, lastName, phoneNumber);
    }
}
