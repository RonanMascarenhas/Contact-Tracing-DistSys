package ie.sds.resultsDiscovery.core;

// todo discuss this with Ronan and move to 'core'


import java.io.Serializable;

enum ContactTraced {
    YES,
    NO
}

public class Patient implements Serializable {

    //Rem-best practice is public fields??
//    @Id
    private String id;  //for internal use by mongodb(see https://spring.io/guides/gs/accessing-data-mongodb/#scratch)
    private ContactTraced ct;

    private PatientInfo info;
    private TestResult result;


    public Patient() {
    }

    public Patient(String id, PatientInfo info, TestResult result, ContactTraced ct) {
        this.id = id;
        this.info = info;
        this.result = result;
        this.ct = ct;
//        this.firstName = firstName;
//        this.lastName = lastName;
//        this.phoneNumber = phoneNumber;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ContactTraced getContactTraced() {
        return ct;
    }

    public void setContactTraced(ContactTraced ct) {
        this.ct = ct;
    }

    public ContactTraced getCt() {
        return ct;
    }

    public void setCt(ContactTraced ct) {
        this.ct = ct;
    }

    public PatientInfo getInfo() {
        return info;
    }

    public void setInfo(PatientInfo info) {
        this.info = info;
    }

    public TestResult getResult() {
        return result;
    }

    public void setResult(TestResult result) {
        this.result = result;
    }

    //    private String firstName;
//    private String lastName;
//    private String phoneNumber;

    /*
    public String getFirstName()   { return firstName; }
    public void setFirstName(String firstName)  {
        this.firstName=firstName;
    }

    public String getLastName()   {
        return lastName;
    }
    public void setLastName(String lastName)  {
        this.lastName=lastName;
    }

    public String getPhoneNumber()   {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber)  {
        this.phoneNumber=phoneNumber;
    }
    */

    @Override
    public String toString() {
        return String.format(
                "Patient[id=%s, info=%s, results=%s]",
                id, info.toString(), result.toString());
    }

}
