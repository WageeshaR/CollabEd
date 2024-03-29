package com.collabed.core.internal;

import com.collabed.core.service.intel.criteria.Criteria;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple, request-scoped @Bean of type CEGateway for communicating with CEIntel. [@see com.collabed.core.config.GatewayBeanConfig.java]
 * This bean is instantiated once per web request, thus all state variables are encapsulated per-request basis.
 * @property ConnectionConfig is guarded just to ensure atomicity, but we expect only a single thread is operating
 * on an instance of this class at a time, with the same connectionConfig.
 *
 * @author Wageesha Rasanjana
 * @since 1.0
 */
@Log4j2
@ThreadSafe
public class SimpleIntelGateway implements CEGateway {
    @GuardedBy("this")
    private ConnectionConfig connectionConfig;
    private final String hostUri;
    private List<Object> listResult;
    private Object result;

    public SimpleIntelGateway(String hostUri) {
        this.hostUri = hostUri;
    }

    /**
     * Initialises the gateway instance with a ConnectionConfig instance
     * @return boolean
     */
    @Override
    public boolean initialise() {
        connectionConfig = new ConnectionConfig();
        listResult = new ArrayList<>();
        return true;
    }

    @Override
    public void authenticate() {
        try {
            // TODO: implement authentication
         } catch (Exception e) {
            // TODO: handle and log errors properly
            log.error(e);
        }
     }

    /**
     * Initialise a http request per instance, with thread-safety in place
     * @param criteria intel criteria to be submitted along with request
     * @throws URISyntaxException throws if the hostUri is in incorrect format
     */
     public synchronized void config(Criteria criteria) throws URISyntaxException {
        if (connectionConfig.httpRequest == null)
            connectionConfig.configure(hostUri, criteria);
     }

    /**
     * Submit intel criteria and "synchronously" wait for results
     * @param resultType The class type of result to determine which variable to be filled
     * @return Boolean indicating success/failure of operation
     * @param <T> Generic type holder
     */
     public <T> boolean fetchSync(Class<T> resultType) {
         try {
             var response = connectionConfig.httpClient.send(connectionConfig.httpRequest,
                     HttpResponse.BodyHandlers.ofString());

             if (response.statusCode() == HttpStatus.OK.value()) {
                 try {
                     var objectMapper = new ObjectMapper();
                     T value = objectMapper.readValue(response.body(), resultType);

                     if (value instanceof List<?>) {
                         synchronized (this) {
                             this.listResult.addAll((List<?>) value);
                         }
                     }
                     else if (value != null) {
                         result = value;
                     }
                     else return false;
                 } catch (JsonProcessingException e) {
                     log.error("Failed to convert JSON response body back to given type: " + e);
                     return false;
                 }
             }
             else {
                 log.info(String.format("Error response %d from intel gateway.\nResponse body: %s", response.statusCode(), response.body()));
                 return false;
             }
         } catch (IOException | InterruptedException e) {
             log.error("");
             return false;
         }
         log.info("Successfully fetched data and stored in gateway.");
         return true;
     }

    /**
     * Submit intel criteria "asynchronously"
     * @param resultType The class type of result to determine which variable to be filled
     * @return Boolean indicating success/failure of operation
     * @param <T> Generic type holder
     */
    public <T> boolean fetchAsync(Class<T> resultType) {
        return false;
    }

    @Override
    public boolean hasResult() {
        return false;
    }

    @Override
    public List<?> returnListResult() {
        return listResult;
    }

    @Override
    public Object returnResult() {
        return result;
    }

    private static final class ConnectionConfig {
        HttpClient httpClient;
        HttpRequest httpRequest;

        ConnectionConfig() {
            this.httpClient = HttpClient.newBuilder().build();
        }
        private void configure(String uri, Criteria criteria) throws URISyntaxException {
            URI clientUri = new URI(uri);
            String requestBody;
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                requestBody = objectMapper.writeValueAsString(criteria);

                // TODO: construct request with body, headers, etc.
                this.httpRequest = HttpRequest
                        .newBuilder(clientUri)
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                        .header("custom", "header")
                        .build();
            } catch (JsonProcessingException e) {
                log.error("Connection configuration failed. Error mapping object to JSON: " + e);
            }
        }
    }
}
