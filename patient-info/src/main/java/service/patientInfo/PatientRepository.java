package service.patientInfo;

import java.util.ArrayList;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.repository.query.Param;

@RepositoryRestResource(collectionResourceRel = "patientinfo", path = "patientinfo")
public interface PatientRepository extends MongoRepository<Patient, String> {
    Patient findByFirstName(@Param("firstName") String firstName);
    Patient findBySurname(@Param("surname") String surname);

}

//    dont need these?? interface already supports CRUD- https://spring.io/guides/gs/accessing-data-mongodb/#scratch
