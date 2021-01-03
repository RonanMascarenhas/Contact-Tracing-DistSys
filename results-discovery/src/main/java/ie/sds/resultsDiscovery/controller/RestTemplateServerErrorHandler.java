package ie.sds.resultsDiscovery.controller;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

/**
 * Throws an exception only if there was a server error.
 */
@Component
public class RestTemplateServerErrorHandler implements ResponseErrorHandler {
    @Override
    public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {
        return clientHttpResponse.getStatusCode().is5xxServerError();
    }

    @Override
    public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {
        if (hasError(clientHttpResponse)) {
            throw new HttpClientErrorException(clientHttpResponse.getStatusCode());
        }
    }
}
