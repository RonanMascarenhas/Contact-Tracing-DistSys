package ie.ucd.sds.webUI.controller;

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
import service.core.Names;
import service.core.Patient;
import service.core.Result;
import service.dns.DomainNameService;
import service.exception.InvalidEntityException;
import service.exception.NoSuchServiceException;
import service.messages.PatientResultCallWorkItem;
import service.messages.PatientWorkItem;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Objects;

@Controller
@RequestMapping("/results")
public class ResultsDiscoveryController {
    // test Entities
    private final DomainNameService dns;

    private static final Logger logger = LoggerFactory.getLogger(ResultsDiscoveryController.class);

    @Autowired
    public ResultsDiscoveryController(DomainNameService dns) {
        this.dns = dns;
    }

    /**
     * Simple method to ensure a patient is valid on entry to the system.
     *
     * @param patient The {@code Patient} to validate.
     * @throws InvalidEntityException if the patient has a missing field and cannot be accepted.
     */
    private static void validatePatient(Patient patient) throws InvalidEntityException {
        boolean patientInvalid =
                isNullOrBlank(patient.getFirstName())
                        || isNullOrBlank(patient.getSurname())
                        || isNullOrBlank(patient.getPhoneNumber())
                        || Objects.isNull(patient.getResult());

        if (patientInvalid) throw new InvalidEntityException(
                String.format("%s is invalid. Make sure all fields are filled in as appropriate", patient)
        );
    }

    private static void validateWorkItem(PatientResultCallWorkItem workItem) throws InvalidEntityException {
        boolean workItemInvalid =
                isNullOrBlank(workItem.getFirstName())
                        || isNullOrBlank(workItem.getSurname())
                        || isNullOrBlank(workItem.getPatientId())
                        || isNullOrBlank(workItem.getPhoneNumber())
                        || Objects.isNull(workItem.getCreated())
                        || Objects.isNull(workItem.getLastAccessed())
                        || Objects.isNull(workItem.getResult())
                        || Objects.isNull(workItem.getStatus());

        if (workItemInvalid) throw new InvalidEntityException(String.format("%s is invalid.", workItem));
    }

    private static boolean isNullOrBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    @GetMapping("/")
    public String getResultIndex(Model model) {
        model.addAttribute("patient", new Patient());
        model.addAttribute("resultsValues", Result.values());
        model.addAttribute("resourceCreated", false);
        return "results/index";
    }

    @GetMapping("/call")
    public String getResultsCall(Model model) throws NoSuchServiceException {
        URI resultsDiscoveryUri = dns.find(Names.RESULTS_DISCOVERY)
                .orElseThrow(dns.getServiceNotFoundSupplier(Names.RESULTS_DISCOVERY));
        URI workItemEndpoint = URI.create(resultsDiscoveryUri + "/result/workitem");

        RestTemplate template = new RestTemplate();
        PatientResultCallWorkItem workItem = template.getForObject(workItemEndpoint, PatientResultCallWorkItem.class);

        if (Objects.isNull(workItem)) {
            return "results/call/none";
        }

        model.addAttribute("workItem", workItem);
        model.addAttribute("statusValues", PatientWorkItem.Status.values());
        return "results/call/index";
    }

    @PostMapping("/call")
    public String editResultsCall(@ModelAttribute PatientResultCallWorkItem workItem, Model model)
            throws InvalidEntityException, NoSuchServiceException {
        validateWorkItem(workItem);

        URI resultsDiscoveryUri = dns.find(Names.RESULTS_DISCOVERY)
                .orElseThrow(dns.getServiceNotFoundSupplier(Names.RESULTS_DISCOVERY));
        URI workItemEndpoint = URI.create(resultsDiscoveryUri + "/result/workitem");

        RestTemplate template = new RestTemplate();
        ResponseEntity<?> response = template.postForEntity(workItemEndpoint, workItem, String.class);

        if (response.getStatusCode() == HttpStatus.OK) return "results/call/accepted";
        // else
        model.addAttribute("msg", "There was an error and the Work-Item was not accepted");
        return "problem";
    }

    @PostMapping("/")
    public String addResult(@ModelAttribute Patient patient, Model model, HttpServletResponse response)
            throws IOException, NoSuchServiceException, InvalidEntityException {
        validatePatient(patient);

        // send it to the back-end
        URI resultsDiscoveryUri = dns.find(Names.RESULTS_DISCOVERY)
                .orElseThrow(dns.getServiceNotFoundSupplier(Names.RESULTS_DISCOVERY));
        URI resultsEndpoint = URI.create(resultsDiscoveryUri + "/result");
        ResponseEntity<Patient> restResponse =
                new RestTemplate().postForEntity(resultsEndpoint, patient, Patient.class);
        HttpStatus status = restResponse.getStatusCode();

        if (status.is2xxSuccessful()) {
            model.addAttribute("resourceLocation", restResponse.getHeaders().getLocation());
            model.addAttribute("patient", restResponse.getBody());
            if (status == HttpStatus.CREATED) {
                model.addAttribute("resourceCreated", true);
                model.addAttribute("location", restResponse.getHeaders().getLocation());
            } else if (status == HttpStatus.OK) {
                model.addAttribute("resourceCreated", false);
            }
            return "results/accepted";
        }

        // some unknown error occurred (Not invalid Patient nor ServiceNotFound)
        String unknownErrorMessage =
                String.format("Unknown Error. Status %s returned from %s", status, Names.RESULTS_DISCOVERY);
        model.addAttribute("errorMsg", unknownErrorMessage);
        logger.warn(unknownErrorMessage);

        // return some error explanation
        return "problem";
    }
}
