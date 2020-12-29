package service.patientInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

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

@RestController
public class PatientInfoController {    //extends Subject class from core, uses Subject fields (master branch) {

    private Patient patient;
    private PatientRecord record;

    @RequestMapping(value = "/patientinfo/{patient}", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void addPatient(@PathVariable Patient patient) {
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