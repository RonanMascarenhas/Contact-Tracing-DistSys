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
//        Date d1 = new Date();
//        Date d2 = new Date();
        Patient p1 = new Patient("0", "A", "Z", "0123456789", Result.POSITIVE, ContactTraced.YES);
        Patient p2 = new Patient("1", "B", "Y", "0987654321", Result.POSITIVE, ContactTraced.NO);

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<Patient> request = new HttpEntity<>(p1);
        HttpEntity<Patient> request1 = new HttpEntity<>(p2);

        System.out.println("\nAPPLICATION-PASSING PATIENT TO CONTROLLER\n");
        ResponseEntity<Patient> pEntityOne= restTemplate.postForEntity("http://localhost:8080/patientinfo", request, Patient.class);
        Patient pResponse1 = pEntityOne.getBody();
        System.out.println("\nAPPLICATION: PATIENT RECEIVED: " + pResponse1.getFirstName() + pResponse1.getSurname());

        System.out.println("\nAPPLICATION-PASSING PATIENT TO CONTROLLER\n");
        ResponseEntity<Patient> pEntityTwo = restTemplate.postForEntity("http://localhost:8080/patientinfo", request1, Patient.class);
        Patient pResponse2 = pEntityTwo.getBody();
        System.out.println("\nAPPLICATION: PATIENT RECEIVED: " + pResponse2.getFirstName() + pResponse2.getSurname());


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

