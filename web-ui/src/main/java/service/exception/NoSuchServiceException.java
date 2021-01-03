package service.exception;

// todo remove once migrated to core

/**
 * An exception to explain errors that arise when a DNS fails to find a requested service
 */
public class NoSuchServiceException extends Exception {
    public NoSuchServiceException(String serviceName) {
        super(
                String.format("No service of name %s was found", serviceName)
        );
    }
}
