package service.messages;

import java.io.Serializable;
import java.util.ArrayList;

public class Contact implements Serializable {

    private String id; //numerical id
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;
    private boolean contactedStatus; //Has the person been successfully contacted, True=Yes, False=No
    private ArrayList casesList; //List of patient ids that the person has been in contact with
    private double contactedDate; //date the person was last contacted on
    private double dateOfCase; //date that the person was in contact with the patient

    public Contact() {}

    public Contact(String id, String firstName, String lastName, String phoneNumber, String address, boolean contactedStatus, ArrayList casesList, double contactedDate, double dateOfCase) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.contactedStatus = contactedStatus;
        this.casesList = casesList;
        this.contactedDate = contactedDate;
        this.dateOfCase = dateOfCase;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isContactedStatus() {
        return contactedStatus;
    }

    public void setContactedStatus(boolean contactedStatus) {
        this.contactedStatus = contactedStatus;
    }

    public ArrayList getCasesList() {
        return casesList;
    }

    public void setCasesList(ArrayList casesList) {
        this.casesList = casesList;
    }

    public double getContactedDate() {
        return contactedDate;
    }

    public void setContactedDate(double contactedDate) {
        this.contactedDate = contactedDate;
    }

    public double getDateOfCase() {
        return dateOfCase;
    }

    public void setDateOfCase(double dateOfCase) {
        this.dateOfCase = dateOfCase;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", address='" + address + '\'' +
                ", contactedStatus=" + contactedStatus +
                ", casesList=" + casesList +
                ", contactedDate=" + contactedDate +
                ", dateOfCase=" + dateOfCase +
                '}';
    }
}
