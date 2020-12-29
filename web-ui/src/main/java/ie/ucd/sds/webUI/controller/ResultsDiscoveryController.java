package ie.ucd.sds.webUI.controller;

import ie.ucd.sds.webUI.core.Patient;
import ie.ucd.sds.webUI.service.DomainNameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;

@Controller
@RequestMapping("/results")
public class ResultsDiscoveryController {
    private static final String RESULTS_DISCOVERY_SERVICE = "results-discovery";
    private final DomainNameService dns;

    @Autowired
    public ResultsDiscoveryController(DomainNameService dns) {
        this.dns = dns;
    }

    @GetMapping("/")
    public String getResultIndex(Model model) {
        model.addAttribute("patient", new Patient());
        return "results/index";
    }

    @PostMapping("/")
    public String addResult(@ModelAttribute Patient patient, Model model, HttpServletResponse response) throws IOException {
        // todo validate the patient
        boolean isValid = true;

        if (isValid) {
            // send it to the back-end
            // todo check for a null URI
            URI resultsDiscoveryUri = dns.find(RESULTS_DISCOVERY_SERVICE);
            RestTemplate template = new RestTemplate();
            ResponseEntity<String> restResponse = template.postForEntity(resultsDiscoveryUri.resolve("results"), patient, String.class);

            if (restResponse.getStatusCode() == HttpStatus.CREATED) {
                // todo return a link to the new resource
                //  specify the link in a toast
                model.addAttribute("newResource", "new resource URI");
                response.sendRedirect("results/index");
            } else {
                // Handle the error
                return "problem";
            }
            response.sendRedirect("results/index");
        }
        // return some indication of pass/failure
        return "problem";
    }
}
