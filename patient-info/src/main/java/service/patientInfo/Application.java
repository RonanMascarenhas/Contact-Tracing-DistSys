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
//import com.mongodb.client.MongoClient;
//import com.mongodb.client.MongoDatabase;


@SpringBootApplication
public class Application implements CommandLineRunner{

    @Autowired
    private PatientRepository patientRepo;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        patientRepo.deleteAll();

        //    TEST DATA
        String query;
        String testResults;         //cchange later
        Patient p1 = new Patient("0","A","B","0123456789");
        Patient p2 = new Patient("1","X","Y","0987654321");

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
        System.out.println(patientRepo.findByFirstName("A"));

        System.out.println("Customers found with findByLastName('Y'):");
        System.out.println("--------------------------------");
        for (Patient p : patientRepo.findByLastName("Y")) {
            System.out.println(p);
        }

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

