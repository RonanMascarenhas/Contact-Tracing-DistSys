package service.contactService;

import com.mongodb.*;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ContactsService {

    //Checks patient DB to see if phone number exists in there
    public boolean checkPatientExists(String Phone){
       /* BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put("phonenumber", Phone);
        DBCursor cursor = patientRepo.find(searchQuery);

       */
        return true;
    }

    //Checks contacts DB to see if phone number exists there
    public boolean checkContactExists(String Phone){
        return true;
    }

}
