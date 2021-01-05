package ie.ucd.sds.webUI.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import service.core.Names;
import service.core.Patient;
import service.core.Subject;
import service.dns.EurekaDNS;
import service.exception.NoSuchContactException;
import service.exception.NoSuchServiceException;
import service.messages.Contact;

import java.net.URI;
import java.util.HashMap;

@Controller
@RequestMapping(value = "/contactsdiscovery")
public class ContactsDiscoveryController {

    private EurekaDNS dns;
    private static final Logger logger = LoggerFactory.getLogger(ContactsDiscoveryController.class.getSimpleName());
    private final HashMap<String, Patient> patients;

    @Autowired
    public ContactsDiscoveryController(EurekaDNS dns) {
        this. dns = dns;
        this.patients = new HashMap<>();
    }

    @GetMapping("/patient")
    public String getPatient() {
        String redirectURL = "";
        try {
            URI uri = dns.find(Names.CONTACT_DISCOVERY).orElseThrow(dns.getServiceNotFoundSupplier(Names.CONTACT_DISCOVERY));

            String contactsDiscoveryURL = uri + "/contactsdiscovery/patient";

            System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

            RestTemplate restTemplate = new RestTemplate();

            System.out.println("THE URI IS: " + contactsDiscoveryURL);
            Patient patient = restTemplate.getForObject(contactsDiscoveryURL, Patient.class);

            System.out.println("======================================================================" + patient.getId());
            patients.put(patient.getId(), patient);

            return "redirect:/contactsdiscovery/patient/" + patient.getId();
        } catch (NoSuchServiceException e) {
            e.printStackTrace();
        }

        return redirectURL;
    }

    @GetMapping("/patient/{patientId}")
    public String getPatientInfo(@PathVariable("patientId") String patientId, Model model) {
        if (patients.containsKey(patientId)) {
            Subject subject = new Subject();
            Patient patient = patients.get(patientId);
            model.addAttribute("patient", patient);
            model.addAttribute("subject", subject);
        } else {
            logger.error(String.format("Error finding patient with ID: %s", patientId));

            // TODO error page
        }

        return "contactsdiscovery";
    }

    @PostMapping("/patient/{patientId}")
    public String getContactInfo(@PathVariable("patientId") String patientId, @ModelAttribute Subject subject) {
        try {
            URI uri = dns.find(Names.CONTACT_DISCOVERY).orElseThrow(dns.getServiceNotFoundSupplier(Names.CONTACT_DISCOVERY));

            String contactsDiscoveryURL = uri + "/contactsdiscovery/patient/" + patientId;

            HttpEntity<Subject> request = new HttpEntity<>(subject);

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.postForEntity(contactsDiscoveryURL, request, Subject.class);

            return "redirect:/contactsdiscovery/patient/" + patientId;
        } catch (NoSuchServiceException e) {
            e.printStackTrace();
        }

        return "contactsdiscovery";
    }
//    @PostMapping("/patient/{patientId}")
//    public String sendPatientInfo(@PathVariable("patientId") String patientId,
//                                  @RequestBody Contact contact, Model model) {
//        try {
//            URI uri = dns.find(Names.CONTACT_DISCOVERY).orElseThrow(dns.getServiceNotFoundSupplier(Names.CONTACT_DISCOVERY));
//
//            String contactsDiscoveryURL = uri + "/contactsdiscovery/patient" + patientId;
//
//            HttpEntity<Contact> request = new HttpEntity<>(contact);
//
//            RestTemplate restTemplate = new RestTemplate();
//            restTemplate.postForEntity(contactsDiscoveryURL, request, Contact.class);
//        } catch (Exception e) {
//            logger.error(String.format("Error sending patient %s to WebUI: %s", patientId, e.getMessage()));
//            model.addAttribute("error", e.getClass().getName());
//
//            return "Contact Tracing Error";
//        }
//
//        return "redirect:/contactsdiscovery/";
//    }
//
//    @GetMapping("/patient/{patientId}")
//    public String getContactsInfo(@PathVariable("patientId") String patientId, Model model) {
//        try {
//            URI uri = dns.find(Names.CONTACT_DISCOVERY).orElseThrow(dns.getServiceNotFoundSupplier(Names.CONTACT_DISCOVERY));
//
//            String contactsDiscoveryURL = uri + "/contactsdiscovery/patient/" + patientId;
//
//            RestTemplate restTemplate = new RestTemplate();
//            Contact contact = restTemplate.getForObject(contactsDiscoveryURL, Contact.class);
//
//            if (contact == null) throw new NoSuchContactException();
//
//            model.addAttribute("contact", contact);
//        } catch (Exception e) {
//            logger.error(String.format("Error receiving contact from patient with ID %s: %s", patientId, e.getMessage()));
//
//            model.addAttribute("error", e.getClass().getName());
//
//            return "Contact Tracing Error";
//        }
//
//        return "contact";
//    }
}