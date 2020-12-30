package ie.ucd.contacttracingfollowup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import service.messages.Contact;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

// TODO: Add RequestMappings for WebUI
@Controller
@RequestMapping(value = "/followup")
public class FollowUpService {

    HashMap<String, Contact> contactsMap;

    @Autowired
    public FollowUpService() {
        contactsMap = new HashMap<>();
    }

    @GetMapping( "/")
    public String showFollowUpRequest() {
        return "default";
    }

    @GetMapping("/contact")
    public String assignFollowUpContact() {
        List<Contact> contacts = getContacts(1);
        if (contacts.isEmpty()) {
            // TODO: Handle case where no contact
            return "redirect:/";
        }

        Contact contact = contacts.get(0);

        contactsMap.put(contact.getId(), contact);
        return "redirect:/followup/contact/" + contact.getId();
    }

    @GetMapping("/contact/{id}")
    public String getFollowUpContact(@PathVariable("id") String id, Model model) {
        if (!contactsMap.containsKey(id)) {
            // TODO: Handle error
            return "contact not available for follow up. ID: " + id;
        }

        model.addAttribute("contact", contactsMap.get(id));

        Date date = new Date((long) contactsMap.get(id).getDateOfCase() * 1000L);
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss z");
        format.setTimeZone(TimeZone.getTimeZone("Europe/Dublin"));
        String formattedDate = format.format(date);
        model.addAttribute("caseDate", formattedDate);

        return "contact";
    }

    @PostMapping("/contact/{id}")
    public String updateContactFollowUpStatus(@PathVariable("id") String id,
                                              @RequestParam("contactedStatus") boolean isContacted) {
        Contact contact = contactsMap.get(id);
        contact.setContactedStatus(isContacted);

        // TODO: Send updated contact to contacts service (in new thread). Possible batch together for an update.

        return "redirect:/followup/";
    }

    @PostMapping("/contacttest/{id}")
    public String testUpdateContactFollowUpStatus(@ModelAttribute("contact") Contact contact) {
        contact.setContactedDate((double) Instant.now().toEpochMilli()/ 1000L);
        System.out.println(contact.toString());
        // TODO: Send updated contact to contacts service (in new thread). Possible batch together for an update.

        return "redirect:/followup/";
    }

//    @RequestMapping(value = "/contact", method = RequestMethod.GET)
//    public String allocateContact() {
//        List<Contact> contacts = getContacts(1);
//        if (contacts.isEmpty()) {
//            return "No contacts to be followed up with.";
//        }
//
//        Contact contact = contacts.get(0);
//
//        contactsMap.put(contact.getId(), contact);
//        return contact.toString();
//    }
//
//    @RequestMapping(value = "/contact/{contact-id}", method = RequestMethod.POST)
//    public String updateContactFollowUpStatus(@PathVariable("contact-id") String contactID) {
//        // TODO: Consider changing from method get to remove method if individual updates
//        Contact contact = contactsMap.get(contactID);
//        if (contact == null) {
//            return "Contact not pending update. Contact ID: " + contactID;
//        }
//
//        contact.setContactedStatus(true);
//
//        // TODO: send update to ContactsService. Either individual or batch.
//        updateFollowUpSuccessStatus(contact);
//
//        return "Contact updated.";
//    }

    public List<Contact> getContacts(int numContacts) {
        //TODO: Remove dummy data

        ArrayList<Contact> dummyContacts = new ArrayList<>();
        Contact contact = new Contact();
        contact.setFirstName("John");
        contact.setLastName("Smith");
        contact.setPhoneNumber("0861234567");
        contact.setId("100");
        contact.setContactedStatus(false);
        contact.setCasesList(new ArrayList<String>());
        contact.getCasesList().add("101");
        contact.getCasesList().add("102");
        contact.setAddress("101 Covid Lane");
        contact.setDateOfCase(1592226600L);

        dummyContacts.add(contact);
        return dummyContacts;

//        RestTemplate restTemplate = new RestTemplate();
//        HttpEntity<Integer> request = new HttpEntity<>(numContacts);
//
//        // TODO: Get service URL from Eureka server
//        String contactService = "service/follow-up-contact";
//
//        // TODO: Agree on approach for getting many contacts
//        Contact[] contacts = restTemplate.postForObject(contactService, request, Contact[].class);
//
//        return Arrays.asList(contacts);
    }

    public void updateFollowUpSuccessStatus(Contact contact) {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<Contact> request = new HttpEntity<>(contact);

        // TODO: Get service URL from Eureka server
        String contactService = "service/follow-up-status/" + contact.getId();

        contact.setContactedStatus(true);

        restTemplate.put(contactService, contact);
    }
}
