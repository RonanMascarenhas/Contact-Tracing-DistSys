package ie.sds.resultsDiscovery.service;

import service.exception.NoSuchServiceException;

import java.net.URI;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * An interface for objects that find URIs of services for clients
 */
public interface DomainNameService {
    Optional<URI> find(String serviceName);

    default Supplier<NoSuchServiceException> getServiceNotFoundSupplier(String serviceName) {
        return () -> new NoSuchServiceException(serviceName);
    }
}
