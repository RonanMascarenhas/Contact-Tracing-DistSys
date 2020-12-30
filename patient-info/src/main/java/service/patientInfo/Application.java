/*
id: This field represents a unique field in MongoDB. This field is created by default.
Collection: It is a set of MongoDB documents. It exists with a single database.         [TABLE]
Database: This is the container for collections. Multiple databases can be stored in a mongoDB server.  [DB]
Document: A record in mongoDB is called a document. It containes names and values.      [RECORD]
Field: It is a name-value pair in a document.
*/

package service.patientInfo;

import org.springframework.beans.factory.annotation.Autowired;
//import com.mongodb.*;
//import org.bson.types.ObjectId;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;
//import com.mongodb.client.MongoClient;
//import com.mongodb.client.MongoDatabase;


@SpringBootApplication
public class Application implements CommandLineRunner{

    @Autowired
    private PatientRepository patientRepo;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

//    "Patient[id=%s, info=%s, results=%s, contactTraced=%s]",
//    "PatientInfo[firstName='%s', lastName='%s', age='%d', sex='%c', phoneNumber='%s', address='%s']",
//    "TestResult[Date='%s', Result='%s']",


    @Override
    public void run(String... args) throws Exception {
        patientRepo.deleteAll();

        Date d1 = new Date();
        Date d2 = new Date();

        //    TEST DATA
        String query;
        String testResults;         //change later
        Patient p1 = new Patient("0",new PatientInfo("A", "B", 20, 'M', "0123456789", "012 Main Street"),
                new TestResult(d1, Result.POSITIVE), ContactTraced.YES);
        Patient p2 = new Patient("1",new PatientInfo("X", "Y", 50, 'F', "0987654321", "98 Bridge Street"),
                new TestResult(d2, Result.NEGATIVE), ContactTraced.NO);

//        public ArrayList<Patient> findBySurname(@Param("surname") String surname);

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

        /*
        try {
            MongoClient mongo = new MongoClient("localhost", 27017);
            DB patientdb = mongo.getDB("patientDB");
            DBCollection patients = patientdb.getCollection("patients");
            DBObject patient = new BasicDBObject("id", new ObjectId())
                    .append("FirstName", p1.getFirstName())
                    .append("LastName", p1.getLastName())
                    .append("PhoneNumber", p1.getPhoneNumber());

            patients.insert(patient);
        }
        catch (Exception e) {
            System.out.println(
                    "Connection establishment failed");
            System.out.println(e);
        }
        */

    }

}

