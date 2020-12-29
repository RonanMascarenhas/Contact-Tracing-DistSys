package service.contactService;

import java.util.ArrayList;

import com.sun.org.apache.xerces.internal.util .SynchronizedSymbolTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application implements CommandLineRunner {

    @Autowired
    private ContactRepository contactRepo;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception{

        contactRepo.deleteAll();

        ArrayList<String> caseList = new ArrayList<String>();

        Contact c1 = new Contact("0", "Finn", "O'Neill", "000000000", "xyz", false, caseList, 1607352927, 1607352927 );
        Contact c2 = new Contact("1", "Jim", "Ahern", "100000000", "wxyz", false, caseList, 1607352927, 1607352927 );

        contactRepo.save(c1);
        contactRepo.save(c2);

        System.out.println("Contacts found with findAll():");
        System.out.println("-------------------------------");
        for (Contact c : contactRepo.findAll()) {
            System.out.println(c);
        }
        System.out.println();

        System.out.println("Contact found with findByFirstName('Finn'):");
        System.out.println("--------------------------------");
        System.out.println(contactRepo.findByFirstName("Finn"));


    }




}