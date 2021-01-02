package service.patientInfo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import service.core.ContactTraced;
import service.core.Patient;
import service.core.Result;


@SpringBootTest
public class PatientInfoTests {

    /*
    private RestTemplate restTemplate = new RestTemplate();
    private Patient testPatient0 = new Patient("12345", "A", "Z", "0123456789", Result.POSITIVE, ContactTraced.YES);

    @Test
    void postPatientWorksTest() {
        ResponseEntity<Patient> result = restTemplate.postForEntity("http://localhost:8082/patientinfo", testPatient0, Patient.class);
        assertNotNull(result);
    }

     */
}

/*
@Test
public void givenUserDoesNotExists_whenUserInfoIsRetrieved_then404IsReceived()
  throws ClientProtocolException, IOException {

    // Given
    String name = RandomStringUtils.randomAlphabetic( 8 );
    HttpUriRequest request = new HttpGet( "https://api.github.com/users/" + name );

    // When
    HttpResponse httpResponse = HttpClientBuilder.create().build().execute( request );

    // Then
    assertThat(
      httpResponse.getStatusLine().getStatusCode(),
      equalTo(HttpStatus.SC_NOT_FOUND));
}
 */
