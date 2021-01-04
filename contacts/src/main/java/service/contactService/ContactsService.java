package service.contactService;

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

import java.util.*;

@RestController
public class ContactsService {


    @Autowired
    private ContactRepository contactRepo;
    
    ArrayList<Contact> toBeContacted = new ArrayList<Contact>();
    HashMap<String, Contact> dupeCheck = new HashMap<String, Contact>();

    @RequestMapping
    public void contactDetailsReceived(ContactList contactArray){
        //TODO: Link with Husni's service
        /*
        RestTemplate restTemplate = new RestTemplate();
        ContactList contactList = restTemplate.getForObject("http://localhost:8084/contactsdiscovery/", ContactList.class);
        System.out.println(contactList);
        */
        for (Contact c : contactArray.getContacts()) {
            ArrayList<String> tempList = c.getCasesList();
            String caseID = tempList.get(0).toString();
            System.out.println(c);
            if (checkContactExists(c.getPhoneNumber())) {
                Contact tempContact = contactRepo.findByPhoneNumber(c.getPhoneNumber());
                ArrayList<String> caseList = tempContact.getCasesList();
                caseList.add(caseID);
                tempContact.setCasesList(caseList);
                contactRepo.save(tempContact);
            }
            if (checkPatientExists(c.getPhoneNumber())) {
                c.setContactedStatus(true);
                contactRepo.insert(c);
                System.out.println("contact made, patient existed");
            } else {
                contactRepo.insert(c);
                toBeContacted.add(c);
                dupeCheck.put(c.getPhoneNumber(), c);
                System.out.println(c);
            }
        }
    }

    //Checks patient DB to see if phone number exists in there
    @RequestMapping(value="/contacts/checkPatient/{phoneNumber}", method=RequestMethod.GET)
    public boolean checkPatientExists(@PathVariable String phoneNumber){
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity response = restTemplate.getForEntity("http://localhost:8082/patientinfo/" + phoneNumber, Patient.class);
        String status = response.getStatusCode().toString();
        if(status.equals("200 OK")){
            return true;
        }
        else{
            return false;
        }
    }

    //Checks contacts DB to see if phone number exists there
    @RequestMapping(value = "contacts/checkContact/{phoneNumber}", method = RequestMethod.GET)
    public boolean checkContactExists(@PathVariable String phoneNumber){
        Contact temp = contactRepo.findByPhoneNumber(phoneNumber);
        System.out.println(temp);
        if(temp == null){
            return false;
        }
        else{
            return true;
        }
    }

    //TODO: trigger this periodically
    //TODO: REST Triggers
    public void contactRetry(){
        List<Contact> dbList = contactRepo.findAll();
        long currentTime = Instant.now().getEpochSecond();
        // day in seconds 86400
        // TODO: change to days

        for (Contact c : dbList){
            if(!c.isContactedStatus() && !dupeCheck.containsKey(c.getPhoneNumber())){
                if(c.getContactAttempts() == 1 && (c.getContactedDate()+561600)<currentTime && currentTime<(c.getContactedDate()+648000)){
                    toBeContacted.add(c);
                    dupeCheck.put(c.getPhoneNumber(), c);
                }
                if(c.getContactAttempts() == 2 && (c.getContactedDate()+1166400)<currentTime && currentTime<(c.getContactedDate()+1252800)){
                    toBeContacted.add(c);
                    dupeCheck.put(c.getPhoneNumber(), c);
                }
                if(c.getContactAttempts() == 3 && (c.getContactedDate()+1771200)<currentTime && currentTime<(c.getContactedDate()+1857600)){
                    toBeContacted.add(c);
                    dupeCheck.put(c.getPhoneNumber(), c);
                }
            }
        }

    }

    // FIXME: Contact does not instantiate correctly, janky work around implemented but could be better
    @RequestMapping(value="/contacts", method=RequestMethod.POST)
    public ResponseEntity<Contact> createContact(@RequestBody Contact contact){
        System.out.println("\nCONTROLLER-ADD: CONTACT RECEIVED: " + contact.getFirstName() + contact.getSurname());

        String path = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString()+ "/contacts/"
                +contact.getPhoneNumber();
        HttpHeaders headers = new HttpHeaders();
        try	{
            headers.setLocation(new URI(path));
        }
        catch(URISyntaxException e){
            System.out.println("\nCONTROLLER-ADD ERROR: " + e);
        }

        contact.setUuid(UUID.randomUUID().toString());

        long currentTime = Instant.now().getEpochSecond();

        contact.setContactedDate(currentTime);
        contact.setDateOfCase(currentTime);

        contactRepo.save(contact);
        System.out.println(contact);
        return new ResponseEntity<>(headers, HttpStatus.CREATED);

    }


    @RequestMapping(value = "/contact/updateCaseList", method = RequestMethod.PUT)
    public Contact updateCaseList(@PathVariable Contact contact, String caseID){
        ArrayList<String> caseList = contact.getCasesList();
        caseList.add(caseID);
        contact.setCasesList(caseList);
        return contact;
    }


    @RequestMapping(value = "/contacts/getOutputList/{num}", method = RequestMethod.GET)
    public ContactList getContactTracingContacts(@PathVariable int num) {
        int numToSend = toBeContacted.size() < num ? toBeContacted.size() : num;

        List<Contact> subList = toBeContacted.subList(0, numToSend);
        ContactList contacts = new ContactList(new ArrayList<Contact>(subList));
        subList.clear();

        return contacts;
    }

    // Contacts may have been not contacted, could be named better.
    @PutMapping("/contacts/returnedContacts")
    public ResponseEntity<String> receiveContactedContacts(@RequestBody ContactList contacts) {
        processContacts(contacts);

        return new ResponseEntity<String>(HttpStatus.OK);
    }

    public void processContacts(ContactList contacts){
        for(Contact c : contacts.getContacts()){
            contactRepo.save(c);
        }
    }

    @RequestMapping(value = "/contacts/test", method = RequestMethod.GET)
    public void test(){
        Contact c1 = new Contact("Finn", "O'Neill", "0000", "abd", "123456");
        Contact c2 = new Contact("Jim", "ahern", "1111", "xyz", "123456");
        ContactList testList = new ContactList();
        testList.addContact(c1);
        testList.addContact(c2);
        System.out.println(checkPatientExists(c1.getPhoneNumber()));

        contactDetailsReceived(testList);
    }
}
