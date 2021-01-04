package ie.sds.resultsDiscovery.controller;

import ie.sds.resultsDiscovery.service.PatientResultsCallQueue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;
import service.core.ContactTraced;
import service.core.Names;
import service.core.Patient;
import service.core.Result;
import service.dns.DomainNameService;
import service.exception.NoSuchServiceException;

import java.net.URI;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class ResultsDiscoveryControllerTest {
    static String patientInfoURIString = "http://localhost:8082";
    static URI patientInfoURI = URI.create(patientInfoURIString);
    static URI patientInfoURIEndpoint = URI.create(patientInfoURIString + "/patientinfo");
    static String telephoneNumber = "0871112222";
    //    MockMvc mockMvc;
    static Patient testPatient = new Patient(null, "James", "McEvoy", telephoneNumber,
            Result.POSITIVE, ContactTraced.NO);
    @MockBean
    DomainNameService mockDns;
    @MockBean
    PatientResultsCallQueue mockPRCallQueue;
    @MockBean
    RestTemplateBuilder mockRestTemplateBuilder;
    @Mock
    RestTemplate mockRestTemplate;
    ResultsDiscoveryController testController;

    @Autowired
    public ResultsDiscoveryControllerTest(MockMvc mockMvc) {
//        this.mockMvc = mockMvc;
    }

    @BeforeEach
    void setup() {
        when(mockDns.find(Names.PATIENT_INFO)).thenReturn(Optional.of(patientInfoURI));

        // mock request for a nonexistent patient
        when(mockRestTemplate.getForEntity(String.format("%s/%s", patientInfoURIEndpoint, telephoneNumber), Patient.class))
                .thenReturn(new ResponseEntity<>(HttpStatus.NO_CONTENT));
        when(mockRestTemplateBuilder.build()).thenReturn(mockRestTemplate);

        testController = new ResultsDiscoveryController(mockDns, mockPRCallQueue, mockRestTemplateBuilder);
    }

    @Test
    void addResultForPatient_addsPatientIfNotFound() throws NoSuchServiceException {
        ResponseEntity<Patient> response = testController.addResultForPatient(testPatient);
        Assertions.assertEquals();

        verify(mockRestTemplate).postForEntity(patientInfoURIEndpoint, testPatient, Patient.class);
    }
}
