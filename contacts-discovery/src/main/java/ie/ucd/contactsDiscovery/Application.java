package ie.ucd.contactsDiscovery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
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

import java.net.URI;

import static service.core.Names.CONTACT_SERVICE;

@SpringBootApplication
@Import(EurekaDNS.class)
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}