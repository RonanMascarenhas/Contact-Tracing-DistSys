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
import service.dns.EurekaDNS;
import service.exception.NoSuchContactException;
import service.messages.Contact;

import java.net.URI;

@Controller
@RequestMapping(value = "/contactsdiscovery")
public class ContactsDiscoveryController {

    private EurekaDNS dns;
    private static final Logger logger = LoggerFactory.getLogger(ContactsDiscoveryController.class.getSimpleName());

    @Autowired
    public ContactsDiscoveryController() {

    }

    @PostMapping("contactsdiscovery/patient/{patientId}")
    public String sendPatientInfo(@PathVariable("patientId") String patientId,
                                  @RequestBody Contact contact, Model model) {
        try {
            URI uri = dns.find(Names.CONTACT_DISCOVERY).orElseThrow(dns.getServiceNotFoundSupplier(Names.CONTACT_DISCOVERY));

            String contactsDiscoveryURL = uri + "/contactsdiscovery/patient" + patientId;

            HttpEntity<Contact> request = new HttpEntity<>(contact);

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.postForEntity(contactsDiscoveryURL, request, Contact.class);
        } catch (Exception e) {
            logger.error(String.format("Error sending patient %s to WebUI: %s", patientId, e.getMessage()));
            model.addAttribute("error", e.getClass().getName());

            return "Contact Tracing Error";
        }

        return "redirect:/contactsdiscovery/";
    }

    @GetMapping("contactsdiscovery/patient/{patientId}")
    public String getContactsInfo(@PathVariable("patientId") String patientId, Model model) {
        try {
        URI uri = dns.find(Names.CONTACT_DISCOVERY).orElseThrow(dns.getServiceNotFoundSupplier(Names.CONTACT_DISCOVERY));

        String contactsDiscoveryURL = uri + "/contactsdiscovery/patient/" + patientId;

        RestTemplate restTemplate = new RestTemplate();
        Contact contact = restTemplate.getForObject(contactsDiscoveryURL, Contact.class);

        if (contact == null) throw new NoSuchContactException();

        model.addAttribute("contact", contact);
    } catch (Exception e) {
        logger.error(String.format("Error receiving contact from patient with ID %s: %s", patientId, e.getMessage()));

        model.addAttribute("error", e.getClass().getName());

        return "Contact Tracing Error";
    }

    return "contact";
}