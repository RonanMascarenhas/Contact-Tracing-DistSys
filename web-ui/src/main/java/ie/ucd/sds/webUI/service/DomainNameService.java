package ie.ucd.sds.webUI.service;

import java.net.URI;

/**
 * An interface for objects that find URIs of services for clients
 */
public interface DomainNameService {
    URI find(String serviceName);
}
