package ie.ucd.contactsDiscovery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import service.core.*;
import service.dns.EurekaDNS;
import service.exception.NoSuchContactException;
import service.exception.NoSuchServiceException;
import service.messages.Contact;
import service.messages.ContactList;

import java.net.URI;
import java.util.ArrayList;

import static service.core.Names.CONTACT_SERVICE;

@RestController
@RequestMapping("/contactsdiscovery")
public class ContactsDiscoveryServiceController {

    private final EurekaDNS dns;
    private final RestTemplateBuilder templateBuilder;
    private final Logger logger = LoggerFactory.getLogger(ContactsDiscoveryServiceController.class);

    @Autowired
    public ContactsDiscoveryServiceController(EurekaDNS dns, RestTemplateBuilder templateBuilder) {
        this.dns = dns;
        this.templateBuilder = templateBuilder;
    }

//    private RestTemplate getRestTemplate() {
//        return templateBuilder.errorHandler(new RestTemplateServerErrorHandler()).build();
//    }

    // Gets next patient info from queue
    // @RequestMapping(value = "/contactsdiscovery", method = RequestMethod.GET)
    // TODO: ?? Talk to Joe about this Im not sure how to receive from his JMS Template
    void getPatientInfo()  {

    }

    // Sends patient info to Web UI
    @RequestMapping(value = "/contactsdiscovery/patient/{patientId}", method = RequestMethod.POST)
    public ResponseEntity<Patient> sendPatientInfo(@PathVariable("patientId") String patientId,
                                                   @RequestBody Contact contact) throws java.net.URISyntaxException {
        // Test Patient
        Patient patient1 = new Patient("0", "patient", "name", "test", Result.POSITIVE, ContactTraced.NO);

        String path = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString() +
                "/contactsdiscovery/patient/" + patient1.getId();

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(new URI(path));

        return new ResponseEntity<>(patient1, headers, HttpStatus.CREATED);
    }

    ContactList closeContacts = new ContactList();

    // Get contacts info from Web UI
    @RequestMapping(value = "/contactsdiscovery/patient/{patientId}", method = RequestMethod.GET)
    public ContactList getContactsInfo(@PathVariable("patientId") String patientId) {
        // Test Patient and Contacts
        Patient patient1 = new Patient("0", "patient", "name", "test", Result.POSITIVE, ContactTraced.YES);
        Contact contact1 = new Contact("1", "Hi", "There", "uwu", "Street", false, new ArrayList(), 0, 23423414243L);
        Contact contact2 = new Contact("2", "Hey", "Hmm", "Yikes", "Road", false, new ArrayList(), 0, 23423414243L);

        ArrayList<String> test = new ArrayList<>();
        test.add(patient1.getId());

        ArrayList<String> test2 = new ArrayList<>();
        test2.add(patientId);
        contact1.setCasesList(test);
        //contact1.setCasesList(patientId);
        contact2.setCasesList(test2);
        closeContacts.addContact(contact1);
        closeContacts.addContact(contact2);

        RestTemplate restTemplate = new RestTemplate();
        String url = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString() +
                "/";

        if (closeContacts == null) throw new NoSuchContactException();

        return closeContacts;
    }

    // Send contacts info to Contacts Service
    @PostMapping
    void sendContactsInfo() throws NoSuchServiceException {
        RestTemplate restTemplate = new RestTemplate();

        URI uri = dns.find(CONTACT_SERVICE).orElseThrow(dns.getServiceNotFoundSupplier(CONTACT_SERVICE));
        String contactsDiscoveryURL = uri + "/contacts/";

        HttpEntity<ContactList> entity = new HttpEntity<>(closeContacts);

        ResponseEntity<HttpStatus> response = restTemplate.exchange(contactsDiscoveryURL, HttpMethod.PUT, entity,
                HttpStatus.class);
        logger.info(String.format("%d contacts sent with returned HTTP status code %s", closeContacts.size(),
                response.getStatusCode()));
        if (response.getStatusCode().is2xxSuccessful()) {
            closeContacts.getContacts().clear();
        }
    }

    private URI getPatientInfoEndpoint() throws NoSuchServiceException {
        URI patientInfoServiceURI = dns.find(Names.PATIENT_INFO)
                .orElseThrow(dns.getServiceNotFoundSupplier(Names.PATIENT_INFO));
        return patientInfoServiceURI.resolve("/patientinfo");
    }

    // Changes the patient's contact traced status to 'YES' and updates the PatientInfo Database
    @PutMapping
    public ResponseEntity<Patient> setPatientContactTraced() throws NoSuchServiceException {
        // TEST PATIENT
        Patient patient1 = new Patient("200", "Hi", "There", "UWU", Result.POSITIVE, ContactTraced.NO);

        patient1.setCt(ContactTraced.YES);

        RestTemplate template = new RestTemplate();

        URI patientInfoEndpoint = getPatientInfoEndpoint();

        ResponseEntity<Patient> response = template.postForEntity(patientInfoEndpoint, patient1, Patient.class);
        
        return response;
    }
}
