package ie.ucd.sds.webUI.core;

import java.io.Serializable;

public class Patient implements Serializable {
    // todo remove this once Ronan migrates to the core
//    @Id
    private String id;  //for internal use by mongodb(see https://spring.io/guides/gs/accessing-data-mongodb/#scratch)
    private String firstName;
    private String surname;
    private String phoneNumber;
    private Result result;
    private ContactTraced ct;

    public Patient() {
    }

    public Patient(String id, String firstName, String surname, String phoneNumber, Result result, ContactTraced ct) {
        this.id = id;
        this.firstName = firstName;
        this.surname = surname;
        this.phoneNumber = phoneNumber;
        this.result = result;
        this.ct = ct;
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

    public ContactTraced getCt() {
        return ct;
    }

    public void setCt(ContactTraced ct) {
        this.ct = ct;
    }

    @Override
    public String toString() {
        return String.format(
                "Patient[id=%s, firstName=%s, surname=%s, phoneNumber=%s]",
                id, firstName, surname, phoneNumber);
    }
}
