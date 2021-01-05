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
import java.util.Objects;

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

            RestTemplate restTemplate = new RestTemplate();

            Patient patient = restTemplate.getForObject(contactsDiscoveryURL, Patient.class);

            if (Objects.isNull(patient)) {
                return "patients/noPatients";
            }

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
        } else if (patients.isEmpty()) {
            return "patients/noPatients";
        }
        else {
                logger.error(String.format("Error finding patient with ID: %s", patientId));
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

    @GetMapping("/patient/null")
    public String nullPatient() {
        return "patients/noPatients";
    }

    @GetMapping("/patient/remove/{patientId}")
    public String removePatient(@PathVariable("patientId") String patientId) {
        logger.info(String.format("The removed patient has map value %s", patients.get(patientId)));
        patients.remove(patientId);

        return "redirect:/contactsdiscovery/patient";
    }
}