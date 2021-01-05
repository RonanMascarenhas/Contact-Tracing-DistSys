package ie.sds.resultsDiscovery.controller;

import ie.sds.resultsDiscovery.service.PatientResultsCallQueue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;
import service.core.ContactTraced;
import service.core.Names;
import service.core.Patient;
import service.core.Result;
import service.dns.DomainNameService;
import service.exception.NoSuchServiceException;
import service.messages.PatientResultCallWorkItem;
import service.messages.PatientWorkItem;

import java.net.URI;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@WebMvcTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class ResultsDiscoveryControllerTest {
    static String patientInfoURIString = "http://localhost:8082";
    static URI patientInfoURI = URI.create(patientInfoURIString);
    static URI patientInfoURIEndpoint = URI.create(patientInfoURIString + "/patientinfo");

    static String newPatientTel = "0871112222";
    static Patient newPatient = new Patient(null, "James", "McEvoy", newPatientTel,
            Result.POSITIVE, ContactTraced.NO);
    static String existingPatientTel = "0894445555";
    static Patient existingPatient = new Patient("1234", "Sally", "Garden", existingPatientTel,
            Result.UNDETERMINED, ContactTraced.YES);
    static String healthyPatientTel = "016667777";
    static Patient healthyPatient = new Patient("1234", "Mary", "Lawlor", healthyPatientTel,
            Result.NEGATIVE, ContactTraced.NO);

    @MockBean
    DomainNameService mockDns;
    @Mock
    DomainNameService badMockDns;
    @MockBean
    PatientResultsCallQueue mockPRCallQueue;
    @Mock
    PatientResultsCallQueue emptyMockPRCallQueue;
    @MockBean
    RestTemplateBuilder mockRestTemplateBuilder;
    @Mock
    RestTemplate mockRestTemplate;
    ResultsDiscoveryController testController;

    @BeforeEach
    void setup() {
        when(mockDns.find(Names.PATIENT_INFO)).thenReturn(Optional.of(patientInfoURI));
        when(badMockDns.find(Names.PATIENT_INFO)).thenReturn(Optional.empty());
        when(badMockDns.getServiceNotFoundSupplier(Names.PATIENT_INFO))
                .thenReturn(() -> new NoSuchServiceException(Names.PATIENT_INFO));

        when(mockPRCallQueue.isEmpty()).thenReturn(false);
        when(emptyMockPRCallQueue.isEmpty()).thenReturn(true);

        // mock request for a nonexistent patient
        String newPatientEndpoint = String.format("%s/%s", patientInfoURIEndpoint, newPatientTel);
        when(mockRestTemplate.getForEntity(newPatientEndpoint, Patient.class))
                .thenReturn(new ResponseEntity<>(HttpStatus.NO_CONTENT));
        when(mockRestTemplate.postForEntity(patientInfoURIEndpoint, newPatient, Patient.class))
                .thenReturn(ResponseEntity.created(patientInfoURIEndpoint).body(newPatient));

        String existingPatientEndpoint = String.format("%s/%s", patientInfoURIEndpoint, existingPatientTel);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(patientInfoURI);
        ResponseEntity<Patient> response = new ResponseEntity<>(existingPatient, headers, HttpStatus.OK);
        when(mockRestTemplate.getForEntity(existingPatientEndpoint, Patient.class))
                .thenReturn(response);

        String healthyPatientEndpoint = String.format("%s/%s", patientInfoURIEndpoint, healthyPatientTel);
        when(mockRestTemplate.getForEntity(healthyPatientEndpoint, Patient.class))
                .thenReturn(new ResponseEntity<>(HttpStatus.NO_CONTENT));
        when(mockRestTemplate.postForEntity(patientInfoURIEndpoint, healthyPatient, Patient.class))
                .thenReturn(ResponseEntity.created(patientInfoURIEndpoint).body(healthyPatient));

        when(mockRestTemplateBuilder.build()).thenReturn(mockRestTemplate);

        testController = new ResultsDiscoveryController(mockDns, mockPRCallQueue, mockRestTemplateBuilder);
    }

    @Test
    void addResultForPatient_throwsExceptionForServiceNotFound() {
        ResultsDiscoveryController badController = new ResultsDiscoveryController(badMockDns, mockPRCallQueue, mockRestTemplateBuilder);
        assertThrows(NoSuchServiceException.class, () -> badController.addResultForPatient(newPatient));
    }

    @Test
    void addResultForPatient_addsPatientIfNotFound() throws NoSuchServiceException {
        ResponseEntity<Patient> response = testController.addResultForPatient(newPatient);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(mockRestTemplate).postForEntity(patientInfoURIEndpoint, newPatient, Patient.class);
    }

    @Test
    void addResultForPatient_doesNotAddPatientIfFound() throws NoSuchServiceException {
        ResponseEntity<Patient> response = testController.addResultForPatient(existingPatient);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(mockRestTemplate, never()).postForEntity(any(), any(), any());
    }

    @Test
    void addResultForPatient_schedulesCallsForPositiveResults() throws NoSuchServiceException {
        testController.addResultForPatient(newPatient);

        verify(mockPRCallQueue).add(newPatient);
    }

    @Test
    void addResultForPatient_doesNotScheduleForNegativeResults() throws NoSuchServiceException {
        testController.addResultForPatient(healthyPatient);

        verify(mockPRCallQueue, never()).add(healthyPatient);
    }

    @Test
    void getCallPatientWorkItem_Returns200IfWorkItemFound() {
        ResponseEntity<?> response = testController.getCallPatientWorkItem();
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getCallPatientWorkItem_Returns204IfNoWorkItemFound() {
        ResultsDiscoveryController emptyQueueController =
                new ResultsDiscoveryController(mockDns, emptyMockPRCallQueue, mockRestTemplateBuilder);
        ResponseEntity<?> response = emptyQueueController.getCallPatientWorkItem();
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void editWorkItem_resendsNotDoneWorkItems() {
        PatientResultCallWorkItem workItem = new PatientResultCallWorkItem(newPatient);
        ResponseEntity<?> response = testController.editWorkItem(workItem);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(mockPRCallQueue).add(workItem);
    }

    @Test
    void editWorkItem_ignoresDoneWorkItems() {
        PatientResultCallWorkItem workItem = new PatientResultCallWorkItem(existingPatient);
        workItem.setStatus(PatientWorkItem.Status.DONE);
        ResponseEntity<?> response = testController.editWorkItem(workItem);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(mockPRCallQueue, never()).add(workItem);
    }
}
