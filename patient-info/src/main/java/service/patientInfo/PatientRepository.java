package service.patientInfo;

import java.util.ArrayList;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PatientRepository extends MongoRepository<Patient, String>{
    public Patient findByFirstName(String firstName);
    public ArrayList<Patient> findByLastName(String lastName);

    /*
    //web client methods
    public ArrayList<Patient> returnRecords(String query);

//    dont need these?? interface already supports CRUD- https://spring.io/guides/gs/accessing-data-mongodb/#scratch

    //    db methods
    public void addRecord(PatientRecord record);

    //    can also accept Patient/PatientRecord object
    public PatientRecord removeRecord(String query);

    //    can also accept Patient/PatientRecord object
    public PatientRecord retrieveRecord(String query);

    //    can also accept Patient/PatientRecord object
    public PatientRecord editRecord(String query);



    public ArrayList<Patient> retrieveAllPositive();

    //    results discovery methods
    public PatientRecord createRecord(String testResults);

    //    callback scheduler methods
    public ArrayList<Patient> scheduleCallback();
     */
}
