package service.contactService;

import org.springframework.data.mongodb.repository.MongoRepository;
import service.messages.Contact;


import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "contacts", path = "contacts")
public interface ContactRepository extends MongoRepository<Contact, String>{
    Contact findByPhoneNumber(@Param("phoneNumber") String phoneNumber);
}
