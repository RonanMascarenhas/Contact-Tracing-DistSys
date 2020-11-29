package service.contactService;

import org.springframework.web.bind.annotation.RestController;

import java.sql.*;

@RestController
public class ContactsService {

    //Checks patient DB to see if phone number exists in there
    public boolean checkPatientExists(String Phone){
        return true;
    }

    //Checks contacts DB to see if phone number exists there
    public boolean checkContactExists(String Phone){
        return true;
    }

    //Add a contact to the database
    public void addToContactDB(String Name, String Phone, String Address, String CaseID ){

    }

    public static void main(String[] args){

        String url = "jdbc:mysql://localhost:3306/contactsDB?autoReconnect=true&userSSl=false";
        String user = "root";
        String password = "dissys";
        try {
            Connection myConn = DriverManager.getConnection(url, user, password);
            Statement myStmt = myConn.createStatement();
            String sql = "select * from contactDB";
            ResultSet resultSet = myStmt.executeQuery(sql);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
