package ie.ucd.sds.webUI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import service.dns.EurekaDNS;

@SpringBootApplication
@Import(EurekaDNS.class)
public class WebUIApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebUIApplication.class, args);
    }
}
