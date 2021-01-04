package ie.sds.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;
import service.core.Names;
import service.core.Patient;
import service.dns.DomainNameService;
import service.dns.EurekaDNS;
import service.exception.NoSuchServiceException;
import service.messages.ContactTracingWorkItem;

import javax.jms.ConnectionFactory;
import java.net.URI;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@SpringBootApplication
@EnableScheduling
@Import({EurekaDNS.class})
public class ContactTracingSchedulerApplication {
    public static final long DELAY = 1 * (60 * 1000);
    private static final Logger logger = LoggerFactory.getLogger(ContactTracingSchedulerApplication.class);
    private final DomainNameService dns;
    private final ConnectionFactory connectionFactory;

    @Autowired
    public ContactTracingSchedulerApplication(
            DomainNameService dns, @Qualifier("jmsConnectionFactory") ConnectionFactory connectionFactory
    ) {
        this.dns = dns;
        this.connectionFactory = connectionFactory;
    }

    public static void main(String[] args) {
        SpringApplication.run(ContactTracingSchedulerApplication.class, args);
    }

    @Scheduled(initialDelay = DELAY, fixedDelay = DELAY)
    public void scheduleNonTracedPatientsForTracing() throws NoSuchServiceException {
        // read
        URI patientServiceUri = dns.find(Names.PATIENT_INFO)
                .orElseThrow(dns.getServiceNotFoundSupplier(Names.PATIENT_INFO));
        URI patientServiceEndpoint = URI.create(patientServiceUri + "/patientinfo/listpatients?ct=NO");
        List<Patient> patients = fetchPatientDataFromApi(patientServiceEndpoint);

        JmsTemplate template = new JmsTemplate(connectionFactory);
        template.setDefaultDestinationName(Names.CONTACT_TRACING_WI_QUEUE);

        // write
        for (Patient patient : patients) {
            logger.info("Generating CallPatientWorkItem for Patient id=" + patient.getId());
            ContactTracingWorkItem ctwi = new ContactTracingWorkItem(patient);
            template.convertAndSend(ctwi);
        }
    }

    private List<Patient> fetchPatientDataFromApi(URI url) {
        RestTemplate restTemplate = new RestTemplate();
        Patient[] patients = restTemplate.getForObject(url, Patient[].class);

        if (patients == null) {
            logger.warn("api returned null patient data");
            return new LinkedList<>();
        }
        return Arrays.asList(patients);
    }
}
