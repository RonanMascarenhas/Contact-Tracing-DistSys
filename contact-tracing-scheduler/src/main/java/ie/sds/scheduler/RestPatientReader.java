package ie.sds.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.web.client.RestTemplate;
import service.core.Patient;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class RestPatientReader implements ItemReader<Patient> {
    private final String apiUrl;
    private final RestTemplate restTemplate;
    private final Logger logger = LoggerFactory.getLogger(RestPatientReader.class);

    private int nextPatientIndex;
    private List<Patient> patientList;

    public RestPatientReader(String apiUrl, RestTemplate restTemplate) {
        this.apiUrl = apiUrl;
        this.restTemplate = restTemplate;
        nextPatientIndex = 0;
    }

    @Override
    public Patient read() {
        if (patientDataNotInitialized()) {
            patientList = fetchPatientDataFromApi();
        }

        Patient patient = null;
        if (nextPatientIndex < patientList.size()) {
            patient = patientList.get(nextPatientIndex);
            nextPatientIndex++;
        } else {
            nextPatientIndex = 0;
        }
        return patient;
    }

    private boolean patientDataNotInitialized() {
        return patientList == null;
    }

    private List<Patient> fetchPatientDataFromApi() {
        Patient[] patients = restTemplate.getForObject(apiUrl, Patient[].class);

        if (patients == null) {
            logger.warn("api returned null patient data");
            return new LinkedList<>();
        }
        return Arrays.asList(patients);
    }
}
