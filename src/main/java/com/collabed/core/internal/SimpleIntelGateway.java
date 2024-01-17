package com.collabed.core.internal;

import com.collabed.core.service.intel.criteria.Criteria;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

/**
 * A simple, request scoped implementation of CEGateway for CEIntel.
 * This class is instantiated at least once per web request, thus all state variables are encapsulated per-request basis.
 */
@Log4j2
public class SimpleIntelGateway implements CEGateway {
    private ConnectionConfig connectionConfig;
    @Value("${intel-gateway.host}")
    private String hostUri;
    private List<?> listResult;
    private Object result;

    @Override
    public boolean initialise() {
        if (connectionConfig == null)
            connectionConfig = new ConnectionConfig();
        return true;
    }

    @Override
    public void authenticate() {
        try {
            // TODO: implement authentication
            System.out.println(connectionConfig.httpClient.toString());
         } catch (Exception e) {
            // TODO: handle and log errors properly
            log.error(e);
        }
     }

     public void config(Criteria criteria) throws URISyntaxException {
        if (!connectionConfig.configured)
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
                     ObjectMapper objectMapper = new ObjectMapper();
                     T value = objectMapper.readValue(response.body(), resultType);

                     if (value instanceof List<?>) {
                         this.listResult = (List<?>) value;
                     }
                     else if (value != null) {
                         this.result = value;
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

    private static class ConnectionConfig {
        HttpClient httpClient;
        HttpRequest httpRequest;
        boolean configured = false;

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

                configured = true;
            } catch (JsonProcessingException e) {
                log.error("Connection configuration failed. Error mapping object to JSON: " + e);
            }
        }
    }
}
