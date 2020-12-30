package service.patientInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import service.core.Subject;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.net.URISyntaxException;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
public class PatientInfoController {

    @Autowired
    private PatientRepository patientRepo;

    private Patient patient;
    private HashMap<String, Patient> patientList = new HashMap<>();

    @RequestMapping(value = "/patientinfo", method = RequestMethod.POST)
//    @ResponseStatus(value = HttpStatus.OK)
    public Patient addPatient(@RequestBody Patient patient) {

//        patientRepo.deleteAll();
        System.out.println("CONTROLLER-ENTERED PATIENTINFO MAPPING" + patient.toString());
        System.out.println("PATIENTINFO-CONTROLLER: PATIENT RECEIVED: " + patient.getFirstName() + patient.getSurname());

        patientRepo.save(patient);

        patientList.put(patient.getId(), patient);
        String path = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString()+ "/patientinfo/"
                +patient.getFirstName()+patient.getSurname();

        HttpHeaders headers = new HttpHeaders();
        try	{
            headers.setLocation(new URI(path));
        }
        catch(URISyntaxException e){
            System.out.println("PATIENTINFO-CONTROLLER ERROR: " + e);
        }

        System.out.println("Patients found with findAll():");
        System.out.println("-------------------------------");
        for (Patient p : patientRepo.findAll()) {
            System.out.println(p);
        }
        System.out.println();

        // fetch an individual patient/patients based on query
        System.out.println("Patients found with findByFirstName('A'):");
        System.out.println("--------------------------------");
        System.out.println(patientRepo.findByFirstName("A"));

        // fetch an individual patient/patients based on query
        System.out.println("Patients found with findBySurname('Y'):");
        System.out.println("--------------------------------");
        System.out.println(patientRepo.findBySurname("Y"));

        return patient;
//        return new ResponseEntity<>(quotation, headers, HttpStatus.CREATED);
//        record.patient = patient;
//    add patient to record, store record in db
//        get patient object from body of REST message, store it in db
    }

    //return list of all patients that havent been called for contact tracing
    @RequestMapping(value = "/patientinfo/listpatients", method = RequestMethod.GET)
    public @ResponseBody ArrayList<Patient> listPatients()    {
        ArrayList<Patient> patientList = new ArrayList<>();
//        iterate through all record in db, store relevant records in list and return to web ui
        return patientList;
    }

}