package ie.ucd.contacttracingfollowup;

import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import service.messages.Contact;

import java.util.Arrays;
import java.util.List;

// TODO: Add RequestMappings for WebUI
@RestController
public class FollowUpService {

    public List<Contact> getContacts(int numContacts) {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<Integer> request = new HttpEntity<>(numContacts);

        // TODO: Get service URL from Eureka server
        String contactService = "service/follow-up-contact";

        // TODO: Agree on approach for getting many contacts
        Contact[] contacts = restTemplate.postForObject(contactService, request, Contact[].class);

        return Arrays.asList(contacts);
    }

    public void updateFollowUpSuccessStatus(Contact contact) {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<Contact> request = new HttpEntity<>(contact);

        // TODO: Get service URL from Eureka server
        String contactService = "service/follow-up-status/" + contact.getContactId();

        contact.setContacted(true);

        restTemplate.put(contactService, contact);
    }
}
