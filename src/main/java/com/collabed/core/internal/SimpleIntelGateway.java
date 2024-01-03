package com.collabed.core.internal;

import com.collabed.core.service.intel.criteria.Criteria;

import java.net.http.HttpClient;
import java.util.List;

public class SimpleIntelGateway implements CEGateway {
    private ConnectionConfig connectionConfig;
    private List<?> listResult;
    private Object result;
    @Override
    public boolean initialise() {
        connectionConfig = new ConnectionConfig();
        return false;
    }

    @Override
    public void authenticate() {
        try {
            System.out.println(connectionConfig.httpClient.toString());
         } catch (Exception e) {

        }
     }

    /**
     * Submit intel criteria and wait for results
     * @param criteria Intel criteria to be submitted
     * @return Boolean indicating success/failure of operation
     */
     public boolean submitCriteria(Criteria criteria) {
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
        private final HttpClient httpClient;
        ConnectionConfig() {
            this.httpClient = HttpClient.newBuilder().build();
        }
    }
}
