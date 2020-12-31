package service.patientInfo;


import java.util.*;

import service.core.Patient;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.net.URISyntaxException;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
public class PatientInfoController {

    @Autowired
    private PatientRepository patientRepo;

    private Patient patient;
//    private HashMap<String, Patient> patientList = new HashMap<>();

//    CRUD - add patient to list
    @RequestMapping(value = "/patientinfo", method = RequestMethod.POST)
//    @ResponseStatus(value = HttpStatus.OK)
    public Patient addPatient(@RequestBody Patient patient) {

//        patientRepo.deleteAll();
//        System.out.println("CONTROLLER-ENTERED PATIENTINFO MAPPING" + patient.toString());
        System.out.println("\nCONTROLLER-ADD: PATIENT RECEIVED: " + patient.getFirstName() + patient.getSurname());

        patientRepo.save(patient);

//        patientList.put(patient.getId(), patient);
        String path = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString()+ "/patientinfo/"
                +patient.getFirstName()+patient.getSurname();

        HttpHeaders headers = new HttpHeaders();
        try	{
            headers.setLocation(new URI(path));
        }
        catch(URISyntaxException e){
            System.out.println("\nCONTROLLER-ADD ERROR: " + e);
        }

        System.out.println("\nCONTROLLER-ADD: Patients found with findAll():");
        System.out.println("-------------------------------");
        for (Patient p : patientRepo.findAll()) {
            System.out.println(p);
        }
        System.out.println();

        /*
        // fetch an individual patient/patients based on query
        System.out.println("\nCONTROLLER-ADD: Patients found with findByFirstName('A'):");
        System.out.println("--------------------------------");
        System.out.println(patientRepo.findByFirstName("A"));

        // fetch an individual patient/patients based on query
        System.out.println("\nCONTROLLER-ADD: Patients found with findBySurname('Y'):");
        System.out.println("--------------------------------");
        System.out.println(patientRepo.findBySurname("Y"));
        */

        return patient;
//        return new ResponseEntity<>(quotation, headers, HttpStatus.CREATED);
//        record.patient = patient;
//    add patient to record, store record in db
//        get patient object from body of REST message, store it in db
    }

    //    CRUD - returns list of patients
    //returns ALL patients (no filtered list yet)
    @RequestMapping(value = "/patientinfo", method = RequestMethod.GET)                                //return all applications/all applications wiht given name
    public @ResponseBody List<Patient> getListPatients(@RequestParam(defaultValue = "") String phoneNumber)    {
        System.out.println("\nCONTROLLER-LISTPATIENTS: Outputting repo patient list");
        List<Patient> patientList = patientRepo.findAll();
        Iterator<Patient> patientIterator = patientList.iterator();
        while (patientIterator.hasNext())   {
            Patient pTemp = patientIterator.next();
            System.out.println(pTemp.toString());
        }
        return patientList;

        /*
        Iterator<Patient> patientIterator = patientList.iterator();
        while (patientIterator.hasNext() == true)   {
            Patient pTemp = patientIterator.next();

        }

        for (ClientApplication ca : applications.values()) {
            //name not given - add all entries to list
            if (name.isEmpty()) {
                list.add(ca);
            }
            else    {
                //if name given AND it DOES match name from this application, add it to the list
                if (ca.getInfo().getName().equals(name))   {
                    list.add(ca);
                }
                //if name given AND it DOESNT match name from this application, DONT add it to the list
                else    {
                    continue;
                }
            }
        }
        return list;

         */
    }

    /*
//    CRUD - replace entry based on phone number
    @RequestMapping(value="/patientinfo/{phoneNumber}", method=RequestMethod.PUT)
    public ResponseEntity<Patient> replacePatient(@PathVariable String phoneNumber, @RequestBody Patient entry) {
        System.out.println("\nEntered CONTROLLER-REPLACEPATIENT. Phone number: " + phoneNumber + "\nPatient: " + entry);
        List<Patient> patientList = patientRepo.findAll();
        Iterator<Patient> patientIterator = patientList.iterator();
        Patient pTemp = null;

        while (patientIterator.hasNext())   {
            pTemp = patientIterator.next();
//            System.out.println(pTemp.toString());
//            System.out.println(pTemp.getPhoneNumber() + " " + phoneNumber);
            if (pTemp.getPhoneNumber().equals(phoneNumber))  {
                System.out.println("CONTROLLER-REPLACEPATIENT: found match, replacing patient " + pTemp.toString());
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

        String path = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString()+ "/patientinfo/"+phoneNumber;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Location", path);
        return new ResponseEntity<>(headers, HttpStatus.NO_CONTENT);
    }

     */


//CRUD-get specific patient based on input phone number.
// returns error if more than 1 match for a phone number
    @RequestMapping(value="/patientinfo/{phoneNumber}", method=RequestMethod.GET)
    @ResponseStatus(value=HttpStatus.OK)
    public Patient getPatient(@PathVariable String phoneNumber) {
        System.out.println("\nEntered CONTROLLER-GETPATIENT. Phone number: " + phoneNumber);
        List<Patient> patientList = patientRepo.findAll();
        Iterator<Patient> patientIterator = patientList.iterator();
        Patient pTemp = null;

        while (patientIterator.hasNext())   {
            pTemp = patientIterator.next();
//            System.out.println(patientIterator.next());
//            System.out.println(pTemp.toString());
//            System.out.println(pTemp.getPhoneNumber() + " " + phoneNumber);
            if (pTemp.getPhoneNumber().equals(phoneNumber))  {
                System.out.println("CONTROLLER-GETPATIENT: found match, returning patient " + pTemp.toString());
                break;
            }

        }

        if (pTemp==null)    {
            System.out.println("\nCONTROLLER-GETPATIENT: no matching patient found for input phone number");
            throw new NoSuchPatientException();
//            return null;
        }

        return pTemp;
    }

    //    CRUD - remove patient based on phone number
    //returns error if more than 1 match for a phone number
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



    //Callback scheduler - return list of all patients that havent been called for contact tracing
    @RequestMapping(value = "/patientinfo/listpatients", method = RequestMethod.GET)
    public @ResponseBody ArrayList<Patient> listPatients()    {
        ArrayList<Patient> patientList = new ArrayList<>();
//        iterate through all record in db, store relevant records in list and return to web ui
        return patientList;
    }

}