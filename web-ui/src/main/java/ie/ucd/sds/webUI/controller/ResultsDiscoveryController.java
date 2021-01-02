package ie.ucd.sds.webUI.controller;

import ie.ucd.sds.webUI.core.CallPatientWorkItem;
import ie.ucd.sds.webUI.core.ContactTraced;
import ie.ucd.sds.webUI.core.Patient;
import ie.ucd.sds.webUI.core.Result;
import ie.ucd.sds.webUI.service.DomainNameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import service.exception.InvalidEntityException;
import service.exception.NoSuchServiceException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;

@Controller
@RequestMapping("/results")
public class ResultsDiscoveryController {
    private static final String RESULTS_DISCOVERY_SERVICE = "results-discovery";
    private static final Patient testPatient = new Patient("22", "Harry", "Styles", "089233445", Result.POSITIVE, ContactTraced.YES);
    private final DomainNameService dns;

    private static final Logger logger = LoggerFactory.getLogger(ResultsDiscoveryController.class.getSimpleName());

    @Autowired
    public ResultsDiscoveryController(DomainNameService dns) {
        this.dns = dns;
    }

    @GetMapping("/")
    public String getResultIndex(Model model) {
        model.addAttribute("patient", new Patient());
        return "results/index";
    }

    private static void validatePatient(Patient patient) throws InvalidEntityException {
        // todo think some more about this
        //  see if we can get a Validator into the core to share between the services
    }

    @GetMapping("/call")
    public String getResultsCall(Model model) {
        //        URI resultsDiscoveryUri = dns.find(RESULTS_DISCOVERY_SERVICE);
//        RestTemplate template = new RestTemplate();
//        CallPatientWorkItem workItem  = template.getForObject(resultsDiscoveryUri.resolve("workitem"), CallPatientWorkItem.class);
        CallPatientWorkItem workItem = new CallPatientWorkItem(testPatient);
        model.addAttribute("workItem", workItem);
        model.addAttribute("statuses", CallPatientWorkItem.Status.values());

        return "/results/call/index";
    }

    @PostMapping("/call")
    public String editResultsCall(@ModelAttribute CallPatientWorkItem workItem, Model model) {

        return "/results/call/accepted";
    }

    @PostMapping("/")
    public String addResult(@ModelAttribute Patient patient, Model model, HttpServletResponse response) throws IOException, NoSuchServiceException, InvalidEntityException {
        // todo validate the patient
        //  maybe handle via an exception
        validatePatient(patient);

        // send it to the back-end
        // todo check for a null URI
        URI resultsDiscoveryUri = dns.find(RESULTS_DISCOVERY_SERVICE)
                .orElseThrow(dns.getServiceNotFoundSupplier(RESULTS_DISCOVERY_SERVICE));

        RestTemplate template = new RestTemplate();
        ResponseEntity<String> restResponse = template.postForEntity(resultsDiscoveryUri.resolve("/results"), patient, String.class);
        int statusCode = restResponse.getStatusCodeValue();
        HttpStatus status = restResponse.getStatusCode();

        if (status.is2xxSuccessful()) {
            model.addAttribute("resourceLocation", restResponse.getHeaders().getLocation());
            if (status == HttpStatus.CREATED) {
                model.addAttribute("resourceCreated", true);
            } else if (status == HttpStatus.OK) {
                model.addAttribute("resourceCreated", false);
            }
            response.sendRedirect("/results/index");
        }

        // some unknown error occurred (Not invalid Patient nor ServiceNotFound)
        String unknownErrorMessage =
                String.format("Unknown Error. Status code %d returned from %s", statusCode, RESULTS_DISCOVERY_SERVICE);
        model.addAttribute("errorMsg", unknownErrorMessage);
        logger.warn(unknownErrorMessage);

        // return some error explanation
        return "problem";
    }
}
