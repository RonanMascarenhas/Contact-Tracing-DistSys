package service.patientInfo;

import service.core.Subject;

public class PatientInfo extends Subject{

    public PatientInfo(){};

    public PatientInfo(String firstName, String surname, int age, char sex, String phoneNumber, String address)    {
        this.setFirstName(firstName);
        this.setSurname(surname);
        this.setAge(age);
        this.setSex(sex);
        this.setPhoneNumber(phoneNumber);
        this.setAddress(address);
    };

//    public ArrayList<Patient> findBySurname(@Param("surname") String surname);

    @Override
    public String toString() {
        return String.format("PatientInfo[firstName='%s', surname='%s', age='%d', sex='%c', phoneNumber='%s', address='%s']",
                this.getFirstName(), this.getSurname(), this.getAge(),this.getSex(), this.getPhoneNumber(), this.getAddress());
    }
}

