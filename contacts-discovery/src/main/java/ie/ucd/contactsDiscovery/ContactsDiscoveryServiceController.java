package ie.ucd.contactsDiscovery;

//import ie.sds.scheduler.WorkItemQueuePusher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import service.core.ContactTraced;
import service.core.Names;
import service.core.Patient;
import service.core.Subject;
import service.dns.EurekaDNS;
import service.exception.NoSuchContactException;
import service.exception.NoSuchServiceException;
import service.messages.Contact;
import service.messages.ContactList;
import service.messages.ContactTracingWorkItem;

import javax.jms.ConnectionFactory;
import java.net.URI;

import static service.core.Names.CONTACT_SERVICE;

@RestController
@RequestMapping("/contactsdiscovery")
public class ContactsDiscoveryServiceController {
    private final EurekaDNS dns;
    private final RestTemplateBuilder templateBuilder;
    public static final long FIVE_SECOND_TIMEOUT = 5 * 1000;
    private final Logger logger = LoggerFactory.getLogger(ContactsDiscoveryServiceController.class);
    private final JmsTemplate jmsTemplate;

    @Autowired
    public ContactsDiscoveryServiceController(EurekaDNS dns, RestTemplateBuilder templateBuilder,
                                              @Qualifier("jmsConnectionFactory") ConnectionFactory connectionFactory
    ) {
        this.dns = dns;
        this.templateBuilder = templateBuilder;

        jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setDefaultDestinationName(Names.CONTACT_TRACING_WI_QUEUE);
        jmsTemplate.setReceiveTimeout(FIVE_SECOND_TIMEOUT);
    }

    private boolean jmsQueueIsEmpty() {
        Boolean empty = jmsTemplate.browse((session, queueBrowser) -> !queueBrowser.getEnumeration().hasMoreElements());
        return (empty == null) ? false : empty;
    }

    private ContactTracingWorkItem takeWorkItemFromQueue() {
        return (ContactTracingWorkItem) jmsTemplate.receiveAndConvert();
    }

    // FIXME
    //      Always first check if there is anything in the queue using jmsQueueIsEmpty() from above
    //      Then use takeWorkItemFromQueue() to get a workItem.
    //      takeWorkItemFromQueue() may return a null if there are many queue consumers (for a busy workload). It's
    //      a good idea to check for this -> return 200 if there is a workItem, 204 if not.
    // ENDFIXME

    // Gets next patient info from queue
    // TODO: ?? Talk to Joe about this Im not sure how to receive from his JMS Template
/*    @RequestMapping(value = "/contactsdiscovery", method = RequestMethod.GET)
    ResponseEntity<ContactTracingWorkItem> getPatientInfo()  {
        if (callQueue.isEmpty)
    }*/

    Patient patients = new Patient();

    // Sends patient info to Web UI
    @RequestMapping(value = "/patient/{patientId}", method = RequestMethod.POST)
    public ResponseEntity<Patient> sendPatientInfo(@PathVariable("patientId") String patientId,
                                                   @RequestBody Patient patient) throws java.net.URISyntaxException {

        patients = patient;
        String path = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString() +
                "/patient/" + patient.getId();

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(new URI(path));

        return new ResponseEntity<>(patient, headers, HttpStatus.CREATED);
    }

    ContactList closeContacts = new ContactList();

    // Get contacts info from Web UI
    @RequestMapping(value = "/patient/{patientId}", method = RequestMethod.GET)
    public void getContactsInfo(@PathVariable("patientId") String patientId, @RequestBody Subject subject) throws NoSuchServiceException {
        subject.setCaseId(patientId);

        Contact contact = new Contact(subject.getFirstName(), subject.getSurname(), subject.getPhoneNumber(),
                subject.getAddress(), subject.getCaseId());

        closeContacts.addContact(contact);

        System.out.println(closeContacts);
        RestTemplate restTemplate = new RestTemplate();
        String url = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString() +
                "/patient/" + patientId;

        if (closeContacts == null) throw new NoSuchContactException();

        sendContactsInfo();
        setPatientContactTraced();
    }

    // Send contacts info to Contacts Service
    @PostMapping
    void sendContactsInfo() throws NoSuchServiceException {
        RestTemplate restTemplate = new RestTemplate();

        URI uri = dns.find(CONTACT_SERVICE).orElseThrow(dns.getServiceNotFoundSupplier(CONTACT_SERVICE));
        String contactsDiscoveryURL = uri + "/contacts/getContactList";

        HttpEntity<ContactList> entity = new HttpEntity<>(closeContacts);

        ResponseEntity<HttpStatus> response = restTemplate.exchange(contactsDiscoveryURL, HttpMethod.PUT, entity,
                HttpStatus.class);
        logger.info(String.format("%d contact(s) sent with returned HTTP status code %s", closeContacts.size(),
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
        patients.setCt(ContactTraced.YES);

        RestTemplate template = new RestTemplate();
        URI patientInfoEndpoint = getPatientInfoEndpoint();
        ResponseEntity<Patient> response = template.postForEntity(patientInfoEndpoint, patients, Patient.class);
        logger.info(String.format("Patient with ID %s has been contact traced", patients.getId(),
                response.getStatusCode()));

        return response;
    }
}
