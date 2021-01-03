package service.patientInfo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import service.core.ContactTraced;
import service.core.Patient;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class PatientInfoController {

    @Autowired
    private PatientRepository patientRepo;

    private Patient patient;
//    private HashMap<String, Patient> patientList = new HashMap<>();


//    CRUD - add patient to list. Returns 201 CREATED response with URI of new resource in header
//    if duplicate phone numbers being added, will create new patient object using same phone number
//    (Results Discovery service ensures no duplicate phone numbers added)
    @RequestMapping(value = "/patientinfo", method = RequestMethod.POST)
    public ResponseEntity<Patient> addPatient(@RequestBody Patient patient) {

//        patientRepo.deleteAll();
        System.out.println("\nCONTROLLER-ADD: PATIENT RECEIVED: " + patient.getFirstName() + patient.getSurname());

//        validation check - ensure none of the fields are NULL, except for id
        if ((patient.getFirstName() == null || patient.getSurname() == null || patient.getPhoneNumber() == null || patient.getCt() == null || patient.getResult() == null)) {
            System.out.println("\nCONTROLLER-ADD: invalid input, one or more of the fields are null");
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        patientRepo.save(patient);
//        patientList.put(patient.getId(), patient);

        System.out.println("\nCONTROLLER-ADD: Patients found with findAll():");
        System.out.println("-------------------------------");
        for (Patient p : patientRepo.findAll()) {
            System.out.println(p);
        }
        System.out.println();


        String path = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString()+ "/patientinfo/"
                +patient.getPhoneNumber();
        HttpHeaders headers = new HttpHeaders();

        try	{
            headers.setLocation(new URI(path));
        }
        catch(URISyntaxException e){
            System.out.println("\nCONTROLLER-ADD ERROR: " + e);
        }

        return new ResponseEntity<>(patient, headers, HttpStatus.CREATED);

        /*
        String path = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString()+ "/book/"+phone_number;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Location", path);
        return new ResponseEntity<>(headers, HttpStatus.NO_CONTENT);
         */
    }


    //    CRUD - returns list of patients
    //returns 200 OK response with list of ALL patients
    @RequestMapping(value = "/patientinfo", method = RequestMethod.GET)
    public @ResponseBody
    List<Patient> getListPatients() {
        System.out.println("\nCONTROLLER-LISTPATIENTS: Outputting repo patient list");
        List<Patient> patientList = patientRepo.findAll();

        for (Patient p : patientList) {
            System.out.println(p.toString());
        }
        return patientList;
    }

    //    CRUD-returns 200 OK with specific patient based on input phone number. 204 NO CONTENT if input phone number not in repo
    //    if duplicate phone numbers present will return first matching patient (Results Discovery service ensures no duplicate phone numbers added)
//    also used by Contacts service to find out if a patient exists
    @RequestMapping(value="/patientinfo/{phoneNumber}", method=RequestMethod.GET)
//    @ResponseStatus(value=HttpStatus.OK)
    public ResponseEntity<Patient> getPatient(@PathVariable String phoneNumber) {
        System.out.println("\nEntered CONTROLLER-GETPATIENT. Phone number: " + phoneNumber);
        List<Patient> patientList = patientRepo.findAll();
        Patient pTemp = null;

        for (Patient p: patientList) {
            if (p.getPhoneNumber().equals(phoneNumber))  {
                System.out.println("CONTROLLER-GETPATIENT: found match, returning patient " + p.toString());
                pTemp = p;
                break;
            }
//            System.out.println(p.toString());
        }

        String path = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString()+ "/patientinfo/"
                +phoneNumber;
        HttpHeaders headers = new HttpHeaders();

        try	{
            headers.setLocation(new URI(path));
        }
        catch(URISyntaxException e){
            System.out.println("\nCONTROLLER-ADD ERROR: " + e);
        }

        if (pTemp==null)    {
            System.out.println("\nCONTROLLER-GETPATIENT: no matching patient found for input phone number");
            return new ResponseEntity<>(headers, HttpStatus.NO_CONTENT);
//            throw new NoSuchPatientException();
//            return null;
        }
//        return pTemp;
        return new ResponseEntity<>(pTemp, headers, HttpStatus.OK);
    }

    //    CRUD - remove patient based on phone number. will return 204 NO CONTENT if success and 404 NOT FOUND if fail
    //    if duplicate phone numbers present will delete first matching patient (Results Discovery service ensures no duplicate phone numbers added)
    @RequestMapping(value="/patientinfo/{phoneNumber}", method=RequestMethod.DELETE)
    @ResponseStatus(value=HttpStatus.NO_CONTENT)
    public void removePatient(@PathVariable String phoneNumber) {
        System.out.println("\nEntered CONTROLLER-REMOVE");

        Patient pTemp = null;
        try {
            pTemp = patientRepo.findFirstByPhoneNumber(phoneNumber);
        }
        catch(Exception e) {
            System.out.println("CONTROLLER-REMOVE: error " + e);
            return;
        }
        if (pTemp == null) {
            throw new NoSuchPatientException();
        }

        System.out.println("\nCONTROLLER-REMOVE: Removing patient:" + pTemp);
        patientRepo.delete(pTemp);

        System.out.println("\nCONTROLLER-REMOVE: Patients found with findAll():");
        System.out.println("-------------------------------");
        for (Patient p : patientRepo.findAll()) {
            System.out.println(p);
        }
        System.out.println();

    }

//    CRUD - replace entry based on phone number.
//    Returns 204 NO CONTENT if successful and 404 NOT FOUND if phone number not in repo
    @RequestMapping(value="/patientinfo/{phoneNumber}", method=RequestMethod.PUT)
    @ResponseStatus(value=HttpStatus.NO_CONTENT)
    public ResponseEntity<Patient> replacePatient(@PathVariable String phoneNumber, @RequestBody Patient entry) {
        System.out.println("\nEntered CONTROLLER-REPLACEPATIENT. Phone number: " + phoneNumber + "\nPatient to put in: " + entry.toString());

//        validation check - ensure none of the input fields are NULL
        if ((entry.getFirstName() == null || entry.getSurname() == null || entry.getId() == null || entry.getPhoneNumber() == null || entry.getCt() == null || entry.getResult() == null) )   {
            System.out.println("\nCONTROLLER-REPLACE: invalid input, one or more of the fields are null");
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        List<Patient> patientList = patientRepo.findAll();
        Patient pTemp = null;

        for (Patient p: patientList) {
            if (p.getPhoneNumber().equals(phoneNumber))  {
                System.out.println("CONTROLLER-REPLACEPATIENT: found match, deleting this patient " + p.toString());
                pTemp = p;
                break;
            }
        }

        if (pTemp==null)    {
            System.out.println("\nCONTROLLER-REPLACEPATIENT: no matching patient found for input phone number");
            throw new NoSuchPatientException();
//            return null;
        }

        patientRepo.delete(pTemp);
        patientRepo.save(entry);

        System.out.println("\nCONTROLLER-REPLACEPATIENT: Patients found with findAll():");
        System.out.println("-------------------------------");
        for (Patient p : patientRepo.findAll()) {
            System.out.println(p);
        }
        System.out.println();

        String path = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString()+ "/patientinfo/"
                +entry.getPhoneNumber();
        HttpHeaders headers = new HttpHeaders();

        try	{
            headers.setLocation(new URI(path));
        }
        catch(URISyntaxException e){
            System.out.println("\nCONTROLLER-REPLACE ERROR: " + e);
        }

        return new ResponseEntity<>(headers, HttpStatus.NO_CONTENT);
    }

//    Returns 200 OK and list of relevant patients depending on the contactTraced parameter specified
//    E.g. http://localhost:8082/patientinfo/listpatients?ct=NO returns list of patients with ContactTraced.NO
    @RequestMapping(value = "/patientinfo/listpatients", method = RequestMethod.GET)
    public @ResponseBody List<Patient> listPatients(
        @RequestParam(name = "ct", required = false) ContactTraced ct) {
        ContactTraced localct = ct;

        System.out.println("\nEntered CONTROLLER-LISTPATIENTS. ContactTraced: " + localct);
        List<Patient> patientList = patientRepo.findAll();
        ArrayList<Patient> selectedPatientList = new ArrayList<>();

//        no ct param specified - return ALL patients in repo
        if (localct == null) {
            System.out.println("CONTROLLER-LISTPATIENTS: returning all patients since param is "+localct);
            return patientList;
        }
//        input specifies YES contact traced, return list of all these patients
        else if (localct==ContactTraced.YES) {
            System.out.println("CONTROLLER-LISTPATIENTS: returning all YES contact traced patients since param is "+localct);
            for (Patient p: patientList) {
                if (p.getCt() == ContactTraced.YES)  {
                    selectedPatientList.add(p);
                }
            }
            return selectedPatientList;
        }
//        input specifies NO contact traced, return list of all these patients
        else {
            System.out.println("CONTROLLER-LISTPATIENTS: returning all NO contact traced patients since param is "+localct);
            for (Patient p: patientList) {
                if (p.getCt() == ContactTraced.NO)  {
                    selectedPatientList.add(p);
                }
//            System.out.println(p.toString());
            }
            return selectedPatientList;
        }
    }
}

