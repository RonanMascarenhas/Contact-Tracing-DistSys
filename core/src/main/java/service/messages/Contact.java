package service.messages;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.io.Serializable;
import java.util.UUID;

@Document(collection = "contacts")
public class Contact implements Serializable{

    @Id
    private String uuid; // unique id
    private String firstName;
    private String surname;
    private String phoneNumber;
    private String address;
    private boolean contactedStatus; //Has the person been successfully contacted, True=Yes, False=No
    private ArrayList casesList; //List of patient ids that the person has been in contact with
    private long contactedDate; ////if contacted true then this is the date of contact, if false it is the date of the last attempt at contact
    private long dateOfCase; //date that the person was in contact with the patient (epoch time)
    private int contactAttempts; //The number of times they have attempted to be contacted

    public Contact() {}

    public Contact(String firstName, String surname, String phoneNumber, String address, String caseID) {
        this.uuid = UUID.randomUUID().toString();
        this.firstName = firstName;
        this.surname = surname;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.contactedStatus = false;

        ArrayList<String> caseList = new ArrayList<String>();
        caseList.add(caseID);
        this.casesList = caseList;

        long currentTime = Instant.now().getEpochSecond();

        this.contactedDate = currentTime;
        this.dateOfCase = currentTime;
        this.contactAttempts = 0;
    }

    public Contact(Contact contact){
        this.uuid = contact.getUuid();
        this.firstName = contact.getFirstName();
        this.surname = contact.getSurname();
        this.phoneNumber = contact.getPhoneNumber();
        this.address = contact.getAddress();
        this.contactedStatus = contact.isContactedStatus();
        this.casesList = contact.getCasesList();
        this.contactedDate = contact.getContactedDate();
        this.dateOfCase = contact.getDateOfCase();
        this.contactAttempts = contact.getContactAttempts();
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public long getContactedDate() {
        return contactedDate;
    }

    public void setContactedDate(long contactedDate) {
        this.contactedDate = contactedDate;
    }

    public long getDateOfCase() {
        return dateOfCase;
    }

    public void setDateOfCase(long dateOfCase) {
        this.dateOfCase = dateOfCase;
    }

    public int getContactAttempts() {return contactAttempts;}

    public void setContactAttempts(int contactAttempts) {this.contactAttempts = contactAttempts;}

    @Override
    public String toString() {
        return "Contact{" +
                "uuid = " + uuid +
                ", first name = " + firstName +
                ", surname = " + surname +
                ", phone = " + phoneNumber +
                ", address = " + address +
                ", contactedStatus = " + contactedStatus +
                ", casesList = " + casesList +
                ", contactedDate = " + contactedDate +
                ", dateOfCase = " + dateOfCase +
                ", contactAttempts = " + contactAttempts +
                '}';
    }
}
