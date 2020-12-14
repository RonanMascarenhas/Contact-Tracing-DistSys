package ie.sds.scheduler;

import ie.sds.core.Patient;
import org.springframework.batch.item.ItemReader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

public class RestPatientReader implements ItemReader<Patient> {
    private final String apiUrl;
    private final RestTemplate restTemplate;

    private int nextPatientIndex;
    private List<Patient> patientList;

    public RestPatientReader(String apiUrl, RestTemplate restTemplate) {
        this.apiUrl = apiUrl;
        this.restTemplate = restTemplate;
        nextPatientIndex = 0;
    }

    @Override
    public Patient read() throws Exception {
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
        // todo error happens here: incorrect URL, fix this next
        ResponseEntity<Patient[]> response = restTemplate.getForEntity(apiUrl, Patient[].class);
        Patient[] patients = response.getBody();
        return Arrays.asList(patients);
    }
}
