package ie.ucd.sds.webUI.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import service.core.Names;
import service.dns.EurekaDNS;
import service.exception.NoContactsAvailableException;
import service.exception.NoSuchContactException;
import service.messages.Contact;

import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static service.core.Names.CONTACT_TRACING;

@Controller
@RequestMapping(value = "/contacttracing")
public class ContactTracingController {

    private EurekaDNS dns;

    @Autowired
    public ContactTracingController(EurekaDNS dns) {
        this.dns = dns;
    }

    public ContactTracingController() {
    }

    @GetMapping("/")
    public String showFollowUpRequest() {
        return "contacttracinghome";
    }

    @GetMapping("/contact")
    public String assignFollowUpContact(Model model) {
        String id;
        try {
            URI uri = dns.find(CONTACT_TRACING).orElseThrow(dns.getServiceNotFoundSupplier(CONTACT_TRACING));

            String contactTracingURL = uri + "/contacttracingservice/contact/";

            RestTemplate restTemplate = new RestTemplate();

            id = restTemplate.getForObject(contactTracingURL, String.class);

            if (id == null)
                throw new NoContactsAvailableException();
        } catch (Exception e) {
            model.addAttribute("error", e.getClass().getName());
            return "contacttracingerror";
        }

        return "redirect:/contacttracing/contact/" + id;
    }

    @GetMapping("/contact/{id}")
    public String getFollowUpContact(@PathVariable("id") String id, Model model) {
        try {
            URI uri = dns.find(CONTACT_TRACING).orElseThrow(dns.getServiceNotFoundSupplier(CONTACT_TRACING));

            String contactTracingURL = uri + "/contacttracingservice/contact/" + id;

            RestTemplate restTemplate = new RestTemplate();
            Contact contact = restTemplate.getForObject(contactTracingURL, Contact.class);
            if(contact == null) throw new NoSuchContactException();

            Date date = new Date((long) contact.getDateOfCase() * 1000L);
            DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss z");
            format.setTimeZone(TimeZone.getTimeZone("Europe/Dublin"));
            String formattedDate = format.format(date);
            model.addAttribute("caseDate", formattedDate);
            model.addAttribute("contact", contact);

        } catch (Exception e) {
            model.addAttribute("error", e.getClass().getName());
            return "contacttracingerror";
        }

        return "contact";
    }

    @PostMapping("/contact/{id}")
    public String updateContactFollowUpStatus(@PathVariable("id") String id,
                                              @RequestParam("contactedStatus") boolean isContacted,
                                              Model model) {
        try {
            URI uri = dns.find(Names.CONTACT_TRACING).orElseThrow(dns.getServiceNotFoundSupplier(Names.CONTACT_TRACING));

            String contactTracingURL = uri + "/contacttracingservice/contact/" + id;

            HttpEntity<Boolean> request = new HttpEntity<>(isContacted);

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.postForEntity(contactTracingURL, request, Contact.class);

        } catch (Exception e) {
            model.addAttribute("error", e.getClass().getName());
            return "contacttracingerror";
        }

        return "redirect:/contacttracing/";
    }
}