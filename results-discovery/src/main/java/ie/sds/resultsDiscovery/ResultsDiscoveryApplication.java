package ie.sds.resultsDiscovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import service.dns.EurekaDNS;

@SpringBootApplication
@Import(EurekaDNS.class)
public class ResultsDiscoveryApplication {
    public static void main(String[] args) {
        SpringApplication.run(ResultsDiscoveryApplication.class, args);
    }
}
