/*
id: This field represents a unique field in MongoDB. This field is created by default.
Collection: It is a set of MongoDB documents. It exists with a single database.         [TABLE]
Database: This is the container for collections. Multiple databases can be stored in a mongoDB server.  [DB]
Document: A record in mongoDB is called a document. It containes names and values.      [RECORD]
Field: It is a name-value pair in a document.
*/

package service.patientInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;


@SpringBootApplication
public class Application {

//    @Autowired
//    private PatientRepository patientRepo;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);

        //    TEST DATA
        Date d1 = new Date();
        Date d2 = new Date();
        Patient p1 = new Patient("0", "A", "Z", "0123456789", Result.POSITIVE, ContactTraced.YES);
        Patient p2 = new Patient("1", "B", "Y", "0123456789", Result.NEGATIVE, ContactTraced.NO);

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<Patient> request = new HttpEntity<>(p1);
        HttpEntity<Patient> request1 = new HttpEntity<>(p2);

        System.out.println("APPLICATION-PASSING PATIENT TO CONTROLLER\n");
        ResponseEntity<Patient> patientEntity = restTemplate.postForEntity("http://localhost:8080/patientinfo", request, Patient.class);
        Patient p0 = patientEntity.getBody();
        System.out.println("APPLICATION: PATIENT RECEIVED: " + p0.getFirstName() + p0.getSurname());

        System.out.println("APPLICATION-PASSING PATIENT TO CONTROLLER\n");
        ResponseEntity<Patient> patientEntity1 = restTemplate.postForEntity("http://localhost:8080/patientinfo", request1, Patient.class);
        Patient p9 = patientEntity1.getBody();
        System.out.println("APPLICATION: PATIENT RECEIVED: " + p9.getFirstName() + p9.getSurname());


//        HttpEntity<Student> body = new HttpEntity(student);
//URL url = new URL("http://localhost:8080/students/"+
//                                          student.getId());
//ResponseEntity<String> result =
//              restTemplate.putForEntity(uri, body, String.class);

//        HttpEntity<ClientInfo> request = new HttpEntity<>(c);
//			//POST call to broker, returning completed client application
//            ClientApplication ca = restTemplate.postForObject("http://localhost:8080/applications", request, ClientApplication.class);

    /*
        patientRepo.save(p1);
        patientRepo.save(p2);

        System.out.println("Patients found with findAll():");
        System.out.println("-------------------------------");
        for (Patient p : patientRepo.findAll()) {
            System.out.println(p);
        }
        System.out.println();

        // fetch an individual patient/patients based on query
        System.out.println("Patients found with findByFirstName('A'):");
        System.out.println("--------------------------------");
//        System.out.println(patientRepo.findByFirstName("A"));

        System.out.println("Customers found with findBySurname('Y'):");
        System.out.println("--------------------------------");
//        for (Patient p : patientRepo.findBySurname("Y")) {
//            System.out.println(p);
//        }
    */
    }

}

