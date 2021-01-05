package service.patientInfo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@WebMvcTest
class PatientInfoControllerTest {
    @MockBean
    PatientRepository testRepo;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void addPatient() {
    }

    @Test
    void getListPatients() {
    }

    @Test
    void getPatient() {
    }

    @Test
    void removePatient() {
    }

    @Test
    void replacePatient() {
    }

    @Test
    void listPatients() {
    }
}
