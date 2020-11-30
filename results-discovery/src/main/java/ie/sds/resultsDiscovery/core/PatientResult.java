package ie.sds.resultsDiscovery.core;

public class PatientResult {
    private PatientInfo info;
    private Covid19Result result;

    public PatientResult() {
    }

    public PatientResult(PatientInfo patientInfo, Covid19Result result) {
        this.info = patientInfo;
        this.result = result;
    }

    public PatientInfo getInfo() {
        return info;
    }

    public void setInfo(PatientInfo info) {
        this.info = info;
    }

    public Covid19Result getResult() {
        return result;
    }

    public void setResult(Covid19Result result) {
        this.result = result;
    }
}
