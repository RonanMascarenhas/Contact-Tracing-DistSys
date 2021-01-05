package ie.ucd.contacttracing.controller;

import ie.ucd.contacttracing.exception.NoContactServiceAvailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import static service.core.Names.CONTACT_SERVICE;

@RestController
@RequestMapping(value = "/contacttracingfollowupservice")
public class ContactTracingFollowupServiceController {

    private final EurekaDNS dns;
    private final HashMap<String, Contact> contactsPendingContact;
    private final Queue<String> pendingContactQueue;
    private final ContactList updatedContacts;
    private final Logger logger = LoggerFactory.getLogger(ContactTracingFollowupServiceController.class.getSimpleName());


    @Autowired
    public ContactTracingFollowupServiceController(EurekaDNS dns) {
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
                logger.info(String.format("Pending Contact Cache has %d contacts. Requesting %d additional contacts.",
                        pendingContactQueue.size(), CACHE_MIN_THRESHOLD));
                getContacts(CACHE_MIN_THRESHOLD);
            } catch (NoContactServiceAvailableException | NoSuchServiceException e) {
                // Continue using cached contacts is available.
                logger.error(String.format("Exception encountered when requesting contacts: %s", e.toString()));
            }
        }

        if (pendingContactQueue.isEmpty()) {
            logger.info(String.format("No contacts remaining to be contacted."));
            throw new NoContactsAvailableException();
        }

        String id = pendingContactQueue.remove();
        logger.info(String.format("Contact %s allocated to be contacted.", id));
        return id;
    }

    @GetMapping("/contact/{id}")
    public Contact getFollowUpContact(@PathVariable("id") String id, Model model) {
        if (!contactsPendingContact.containsKey(id)) {
            logger.error(String.format("No contact with ID %s is pending contact.", id));
            throw new NoSuchContactException();
        }

        logger.info(String.format("Contact with ID %s returned.", id));
        return contactsPendingContact.get(id);
    }

    @PostMapping("/contact/{id}")
    public ResponseEntity<Contact> updateContactFollowUpStatus(@PathVariable("id") String id,
                                              @RequestBody() boolean isContacted)
            throws URISyntaxException, NoSuchServiceException {

        if (!contactsPendingContact.containsKey(id)) {
            logger.error(String.format("No contact with ID %s is pending contact.", id));
            throw new NoSuchContactException();
        }

        Contact contact = contactsPendingContact.remove(id);
        contact.setContactedStatus(isContacted);
        // Changing time unit to seconds.
        contact.setContactedDate(Instant.now().getEpochSecond());
        contact.setContactAttempts(contact.getContactAttempts() + 1);

        updatedContacts.addContact(contact);
        sendUpdatedContacts();

        String path = String.format("%s/contact/%s", ServletUriComponentsBuilder.fromCurrentContextPath().
                build().toUriString(), id);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(new URI(path));
        logger.info(String.format("Contacted status updated for ID %s", id));
        return new ResponseEntity<>(contact, headers, HttpStatus.OK);
    }

    public void getContacts(int num) throws NoSuchServiceException {
        RestTemplate restTemplate = new RestTemplate();

        URI uri = dns.find(CONTACT_SERVICE).orElseThrow(dns.getServiceNotFoundSupplier(CONTACT_SERVICE));

        String contactsServiceURL = String.format("%s/contacts/outputList/{num}", uri);

        ContactList contacts = restTemplate.getForObject(contactsServiceURL, ContactList.class, num);

        if (contacts != null) {
            logger.info(String.format("%d contacts retrieved from %s", contacts.size(), CONTACT_SERVICE));
            for (Contact contact : contacts.getContacts()) {
                contactsPendingContact.put(contact.getUuid(), contact);
                pendingContactQueue.add(contact.getUuid());
            }
        }
    }

    public void sendUpdatedContacts() throws NoSuchServiceException {
        RestTemplate restTemplate = new RestTemplate();

        URI uri = dns.find(CONTACT_SERVICE).orElseThrow(dns.getServiceNotFoundSupplier(CONTACT_SERVICE));
        String contactsServiceURL = String.format("%s/contacts/returnedContacts", uri);

        HttpEntity<ContactList> entity = new HttpEntity<ContactList>(updatedContacts);
        ResponseEntity<String> response = restTemplate.exchange(contactsServiceURL, HttpMethod.PUT, entity,
                String.class);
        logger.info(String.format("%d contacts sent with returned HTTP status code %s", updatedContacts.size(),
                response.getStatusCode()));
        if (response.getStatusCode().is2xxSuccessful()) {
            updatedContacts.getContacts().clear();
        }
    }
}
