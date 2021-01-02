package ie.ucd.sds.webUI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"service.dns"})
public class WebUIApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebUIApplication.class, args);
    }
}
