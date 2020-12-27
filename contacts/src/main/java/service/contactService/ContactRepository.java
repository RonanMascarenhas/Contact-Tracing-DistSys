package service.contactService;


import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "contacts", path = "contacts")
public interface ContactRepository extends MongoRepository<Contact, String>{

    public Contact findByFirstName(@Param("name") String firstName);
    public List<Contact> findByLastName(String lastName);
}
