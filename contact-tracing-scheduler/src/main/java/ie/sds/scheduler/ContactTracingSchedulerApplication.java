package ie.sds.scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ContactTracingSchedulerApplication {
    public static void main(String[] args) {
        System.exit(SpringApplication.exit(SpringApplication.run(ContactTracingSchedulerApplication.class, args)));
    }
}
