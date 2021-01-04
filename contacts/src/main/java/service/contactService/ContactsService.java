package service.contactService;

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
public class ContactsService {


    @Autowired
    private ContactRepository contactRepo;

    private final ArrayList<Contact> toBeContacted;
    private final HashMap<String, Contact> dupeCheck;
    private final EurekaDNS dns;

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
        //TODO: Link with Husni's service

        for (Contact c : contactArray.getContacts()) {
            ArrayList<String> tempList = c.getCasesList();
            String caseID = tempList.get(0).toString();
            if (checkContactExists(c.getPhoneNumber())) {
                Contact tempContact = new Contact(contactRepo.findByPhoneNumber(c.getPhoneNumber()));
                System.out.println("Temp contact is :" + tempContact);
                System.out.println("Contact from DB is :" + contactRepo.findByPhoneNumber(c.getPhoneNumber()));
                ArrayList<String> caseList = tempContact.getCasesList();
                caseList.add(caseID);
                tempContact.setCasesList(caseList);
                contactRepo.save(tempContact);
            }
            else if (checkPatientExists(c.getPhoneNumber())) {
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
    public boolean checkPatientExists(@PathVariable String phoneNumber) throws NoSuchServiceException {
        RestTemplate restTemplate = new RestTemplate();
        URI uri = dns.find(PATIENT_INFO).orElseThrow(dns.getServiceNotFoundSupplier(PATIENT_INFO));
        ResponseEntity response = restTemplate.getForEntity(uri + "/patientinfo/" + phoneNumber, Patient.class);
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

    //triggered each time donal makes a call, too often?
    //could always run but have it sleep for an hour at a time
    //TODO: REST Triggers
    @RequestMapping(value = "/contacts/contactRetry", method = RequestMethod.PUT)
    public void contactRetry(){
        System.out.println("in to retry");
        List<Contact> dbList = contactRepo.findAll();
        long currentTime = Instant.now().getEpochSecond();
        // day in seconds 86400
        for (Contact c : dbList){
            if( !c.isContactedStatus() && !dupeCheck.containsKey(c.getPhoneNumber())) {
                if ( (c.getContactAttempts() < 3) && ((currentTime < c.getContactedDate()+86400))) {
                    toBeContacted.add(c);
                    dupeCheck.put(c.getPhoneNumber(), c);
                    System.out.println("retry triggered");
                }
            }
        }

    }

    /*
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

    }*/


    @RequestMapping(value = "/contact/updateCaseList", method = RequestMethod.PUT)
    public Contact updateCaseList(@PathVariable Contact contact, String caseID){
        ArrayList<String> caseList = contact.getCasesList();
        caseList.add(caseID);
        contact.setCasesList(caseList);
        return contact;
    }

    @RequestMapping(value = "/contacts/getOutputList/{num}", method = RequestMethod.GET)
    public ContactList getContactTracingContacts(@PathVariable int num) {
        contactRetry();
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
    public void test() throws NoSuchServiceException {
        Contact c1 = new Contact("Finn", "O'Neill", "0000", "abd", "123456");
        Contact c2 = new Contact("Jim", "Ahern", "1111", "xyz", "123456");
        ContactList testList = new ContactList();
        testList.addContact(c1);
        testList.addContact(c2);
        System.out.println(checkPatientExists(c1.getPhoneNumber()));

        contactDetailsReceived(testList);
    }
}
