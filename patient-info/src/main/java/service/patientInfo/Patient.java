package service.patientInfo;

import org.springframework.data.annotation.Id;
import java.io.Serializable;

enum ContactTraced  {
    YES,
    NO
}

public class Patient implements Serializable{

    @Id
    private String id;  //for internal use by mongodb(see https://spring.io/guides/gs/accessing-data-mongodb/#scratch)
    private ContactTraced ct;

    private PatientInfo info;
    private TestResult result;


    public Patient(){};

    public Patient(String id, PatientInfo info, TestResult result, ContactTraced ct){
        this.id = id;
        this.info = info;
        this.result = result;
        this.ct = ct;
    };


    public String getId()   { return id; }
    public void setId(String id)  { this.id=id; }

    public ContactTraced getContactTraced()   { return ct; }
    public void setContactTraced(ContactTraced ct)  { this.ct=ct; }

    @Override
    public String toString() {
        return String.format(
                "Patient[id=%s, info=%s, results=%s, contactTraced=%s]",
                id, info.toString(), result.toString(), ct.toString());
    }

}
