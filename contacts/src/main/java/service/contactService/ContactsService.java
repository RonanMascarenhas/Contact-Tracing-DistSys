package service.contactService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.dns.EurekaDNS;
import service.exception.NoSuchServiceException;
import service.messages.ContactList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import service.core.Patient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import service.messages.Contact;
import javax.annotation.PostConstruct;
import java.lang.reflect.Array;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.*;
import static service.core.Names.*;

import java.util.*;

@RestController
@RequestMapping(value = "/contacts")
public class ContactsService {


    @Autowired
    private ContactRepository contactRepo;

    private final ArrayList<Contact> toBeContacted;
    private final HashMap<String, Contact> dupeCheck;
    private final EurekaDNS dns;
    private final Logger logger = LoggerFactory.getLogger(ContactsService.class.getSimpleName());


    @Autowired
    public ContactsService(EurekaDNS dns){
        this.dns = dns;
        this.toBeContacted = new ArrayList<Contact>();
        this.dupeCheck = new HashMap<String, Contact>();
    }

    @PutMapping("/contacts/getContactList")
    public ResponseEntity<String> receiveContactList(@RequestBody ContactList contacts) throws NoSuchServiceException {
        contactDetailsReceived(contacts);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public void contactDetailsReceived(ContactList contactArray) throws NoSuchServiceException {
        for (Contact c : contactArray.getContacts()) {
            ArrayList<String> tempList = c.getCasesList();
            String caseID = tempList.get(0).toString();
            if (checkContactExists(c.getPhoneNumber())) {
                Contact tempContact = new Contact(contactRepo.findByPhoneNumber(c.getPhoneNumber()));
                ArrayList<String> caseList = tempContact.getCasesList();
                caseList.add(caseID);
                tempContact.setCasesList(caseList);
                contactRepo.save(tempContact);
                logger.info(String.format("Contact with Id = %s was updated in the database", tempContact.getUuid()));
            }
            else if (checkPatientExists(c.getPhoneNumber())) {
                c.setContactedStatus(true);
                contactRepo.insert(c);
                logger.info(String.format("Contact with Id = %s was an existing patient, added to contact DB", c.getUuid()));
            } else {
                contactRepo.insert(c);
                toBeContacted.add(c);
                dupeCheck.put(c.getPhoneNumber(), c);
                logger.info(String.format("Contact with Id = %s was added to contact DB", c.getUuid()));
            }
        }
    }

    //Checks patient DB to see if phone number exists in there
    @RequestMapping(value="/Patient/{phoneNumber}", method=RequestMethod.GET)
    public boolean checkPatientExists(@PathVariable String phoneNumber) throws NoSuchServiceException {
        RestTemplate restTemplate = new RestTemplate();
        URI uri = dns.find(PATIENT_INFO).orElseThrow(dns.getServiceNotFoundSupplier(PATIENT_INFO));
        ResponseEntity response = restTemplate.getForEntity(uri + "/patientinfo/" + phoneNumber, Patient.class);
        String status = response.getStatusCode().toString();
        System.out.println(status);
        if(status.equals("200 OK")){
            return true;
        }
        else if(status.equals("204 NO CONTENT")){
            return false;
        }
        else{
            logger.error(String.format("PatientInfo returned unrecognised status. Status returned: %s", status));
            return false;
        }
    }

    //Checks contacts DB to see if phone number exists there
    @RequestMapping(value = "/Contact/{phoneNumber}", method = RequestMethod.GET)
    public boolean checkContactExists(@PathVariable String phoneNumber){
        Contact temp = contactRepo.findByPhoneNumber(phoneNumber);
        if(temp == null){
            return false;
        }
        else{
            return true;
        }
    }

    @RequestMapping(value = "/contactRetry", method = RequestMethod.PUT)
    public void contactRetry(){
        logger.info(String.format("Contact Retry triggered."));
        List<Contact> dbList = contactRepo.findAll();
        long currentTime = Instant.now().getEpochSecond();
        int i = 0;
        // day in seconds 86400
        for (Contact c : dbList){
            if( !c.isContactedStatus() && !dupeCheck.containsKey(c.getPhoneNumber())) {
                if ( (c.getContactAttempts() < 3) && ((currentTime < c.getContactedDate()+86400))) {
                    i++;
                    toBeContacted.add(c);
                    dupeCheck.put(c.getPhoneNumber(), c);
                }
            }
        }
        logger.info(String.format("%d contacts added to toBeContacted list."));
    }

    @RequestMapping(value = "/outputList/{num}", method = RequestMethod.GET)
    public ContactList getContactTracingContacts(@PathVariable int num) {
        contactRetry();
        int numToSend = toBeContacted.size() < num ? toBeContacted.size() : num;
        List<Contact> subList = toBeContacted.subList(0, numToSend);
        ContactList contacts = new ContactList(new ArrayList<Contact>(subList));
        subList.clear();
        logger.info(String.format("%d contacts outputted to be contacted.", numToSend));
        return contacts;
    }

    // Contacts may have been not contacted, could be named better.
    @PutMapping("/returnedContacts")
    public ResponseEntity<String> receiveContactedContacts(@RequestBody ContactList contacts) {
        processContacts(contacts);
        logger.info(String.format("List of contacts returned"));
        return new ResponseEntity<String>(HttpStatus.OK);
    }

    public void processContacts(ContactList contacts){
        for(Contact c : contacts.getContacts()){
            contactRepo.save(c);
        }
        logger.info(String.format("Returned contacts updated in database."));
    }

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public void test() throws NoSuchServiceException {
        Contact c1 = new Contact("Finn", "O'Neill", "0000", "abd", "123456");
        Contact c2 = new Contact("Jim", "Ahern", "1111", "xyz", "123456");
        ContactList testList = new ContactList();
        testList.addContact(c1);
        testList.addContact(c2);
        contactDetailsReceived(testList);
    }
}
