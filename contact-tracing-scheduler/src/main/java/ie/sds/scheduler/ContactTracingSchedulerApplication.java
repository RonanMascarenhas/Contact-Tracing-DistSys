package ie.sds.scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import service.dns.EurekaDNS;

@SpringBootApplication
@Import({EurekaDNS.class})
public class ContactTracingSchedulerApplication {
    public static void main(String[] args) {
        System.exit(SpringApplication.exit(SpringApplication.run(ContactTracingSchedulerApplication.class, args)));
    }
}
