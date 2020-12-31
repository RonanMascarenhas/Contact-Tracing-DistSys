package service.patientInfo;

import java.util.ArrayList;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.repository.query.Param;

@RepositoryRestResource(collectionResourceRel = "patientinfo", path = "patientinfo")
public interface PatientRepository extends MongoRepository<Patient, String> {
    Patient findFirstByFirstName(@Param("firstName") String firstName);
    Patient findFirstBySurname(@Param("surname") String surname);
    Patient findFirstByPhoneNumber(@Param("phoneNumber") String phoneNumber);

}

//    dont need these?? interface already supports CRUD- https://spring.io/guides/gs/accessing-data-mongodb/#scratch

//Use findByFirst___ instead of findBy___
// avoids incorrectresultsizedataaccessexception when you get more than 1 match for an input phoneNumber/name
//https://stackoverflow.com/questions/50108238/occasional-incorrectresultsizedataaccessexception-when-running-a-mongo-query
