package service.patientInfo;

public class PatientInfoController {

    private PatientInfo info;
    private PatientRecord record;

    @RequestMapping(value = "/patientInfo/{name}", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public void storePatient(@PathVariable String name) {
//    add name to case, store case in db
    }
}