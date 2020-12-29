package ie.sds.core;

import java.io.Serializable;

// This is a slightly edited version of Ronan's class
// todo, get rid of this and rely on a patient from Core
public class Patient implements Serializable {

    //Rem-best practice is public fields??
    private String id;  //for internal use by mongodb(see https://spring.io/guides/gs/accessing-data-mongodb/#scratch)

    private String firstName;
    private String lastName;
    private String phoneNumber;

    public Patient() {
    }

    public Patient(String id, String firstName, String lastName, String phoneNumber) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
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

    @Override
    public String toString() {
        return String.format(
                "Patient[id=%s, firstName='%s', lastName='%s', phoneNumber='%s']",
                id, firstName, lastName, phoneNumber);
    }

}

