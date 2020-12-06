package ie.sds.resultsDiscovery.core;

public class PatientResult {
    private Patient info;
    private Covid19Result result;

    public PatientResult() {
    }

    public PatientResult(Patient patientInfo, Covid19Result result) {
        this.info = patientInfo;
        this.result = result;
    }

    public Patient getInfo() {
        return info;
    }

    public void setInfo(Patient info) {
        this.info = info;
    }

    public Covid19Result getResult() {
        return result;
    }

    public void setResult(Covid19Result result) {
        this.result = result;
    }
}
