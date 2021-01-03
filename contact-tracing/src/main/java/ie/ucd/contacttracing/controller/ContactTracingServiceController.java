package ie.ucd.contacttracing.controller;

import ie.ucd.contacttracing.exception.NoContactServiceAvailableException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import service.dns.EurekaDNS;
import service.exception.NoContactsAvailableException;
import service.exception.NoSuchContactException;
import service.exception.NoSuchServiceException;
import service.messages.Contact;
import service.messages.ContactList;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.*;

import static service.core.Names.CONTACT_SERVICE;

@RestController
@RequestMapping(value = "/contacttracingservice")
public class ContactTracingServiceController {

    private final EurekaDNS dns;
    private final HashMap<String, Contact> contactsPendingContact;
    private final Queue<String> pendingContactQueue;
    private final ContactList updatedContacts;

    @Autowired
    public ContactTracingServiceController(EurekaDNS dns) {
        this.dns = dns;
        this.contactsPendingContact = new HashMap<>();
        this.pendingContactQueue = new LinkedList<>();
        this.updatedContacts = new ContactList();
    }

    @GetMapping("/contact")
    public String assignFollowUpContact() throws NoContactServiceAvailableException {

        int CACHE_MIN_THRESHOLD = 5;
        if (pendingContactQueue.size() < CACHE_MIN_THRESHOLD) {
            try {
                getContacts(CACHE_MIN_THRESHOLD);
            } catch (NoContactServiceAvailableException | NoSuchServiceException e) {
                // Continue using cached contacts is available.
            }
        }

        if (pendingContactQueue.isEmpty()) {
            throw new NoContactsAvailableException();
        }

        return pendingContactQueue.remove();
    }

    @GetMapping("/contact/{id}")
    public Contact getFollowUpContact(@PathVariable("id") String id, Model model) {
        if (!contactsPendingContact.containsKey(id)) {
            throw new NoSuchContactException();
        }

        return contactsPendingContact.get(id);
    }

    @PostMapping("/contact/{id}")
    public ResponseEntity<Contact> updateContactFollowUpStatus(@PathVariable("id") String id,
                                              @RequestBody() boolean isContacted) throws URISyntaxException {
        if (!contactsPendingContact.containsKey(id)) {
            throw new NoSuchContactException();
        }
        Contact contact = contactsPendingContact.remove(id);
        contact.setContactedStatus(isContacted);
        // Changing time unit to seconds.
        contact.setContactedDate((double) Instant.now().toEpochMilli()/ 1000L);

        updatedContacts.addContact(contact);
        //TODO: Uncomment
        //sendUpdatedContacts();

        String path = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString()
                + "/contact/" + id;
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(new URI(path));

        return new ResponseEntity<>(contact, headers, HttpStatus.OK);
    }

    public void getContacts(int num) throws NoSuchServiceException {
        ContactList contacts = new ContactList();
        Contact contact1 = new Contact("101", "John", "Smith", "086111", "101 my lane", false, new ArrayList(), 0, 23423414243L);
        Contact contact2 = new Contact("102", "Tom", "Smith", "086112", "102 my lane", false, new ArrayList(), 0, 53423414243L);
        Contact contact3 = new Contact("103", "Bob", "Smith", "086113", "103 my lane", false, new ArrayList(), 0, 73423414243L);
        contacts.addContact(contact1);
        contacts.addContact(contact2);
        contacts.addContact(contact3);

//        RestTemplate restTemplate = new RestTemplate();
//
//        // TODO: Update CONTACT_SERVICE Constant
//        URI uri = dns.find(CONTACT_SERVICE).orElseThrow(dns.getServiceNotFoundSupplier(CONTACT_SERVICE));
//
//        String contactsServiceURL = uri + "/contactTracing/{num}";
//
//        ContactList contacts = restTemplate.getForObject(contactsServiceURL, ContactList.class, num);
//
        if (contacts != null) {
            for (Contact contact : contacts.getContacts()) {
                contactsPendingContact.put(contact.getId(), contact);
                pendingContactQueue.add(contact.getId());
            }
        }
    }

    public void sendUpdatedContacts() throws NoSuchServiceException {
        RestTemplate restTemplate = new RestTemplate();

        // TODO: Update CONTACT_SERVICE Constant
        URI uri = dns.find(CONTACT_SERVICE).orElseThrow(dns.getServiceNotFoundSupplier(CONTACT_SERVICE));
        String contactsServiceURL = uri + "/contactTracing/";

        HttpEntity<ContactList> entity = new HttpEntity<ContactList>(updatedContacts);
        ResponseEntity<HttpStatus> response = restTemplate.exchange(contactsServiceURL, HttpMethod.PUT, entity, HttpStatus.class);
        if (Objects.equals(response.getBody(), HttpStatus.OK)) {
            updatedContacts.getContacts().clear();
        }
    }
}