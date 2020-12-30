package service.patientInfo;

import java.util.ArrayList;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.repository.query.Param;

@RepositoryRestResource(collectionResourceRel = "patientinfo", path = "patientinfo")
public interface PatientRepository extends MongoRepository<Patient, String>{
//    public Patient findByFirstName(String firstName);
//    public ArrayList<Patient> findBySurname(@Param("surname") String surname);

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
