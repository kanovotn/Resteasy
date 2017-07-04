package org.jboss.resteasy.test.interceptor.resource;

import javax.ws.rs.WebApplicationException;

public class ResponseFilterCustomExceptionCustomException extends WebApplicationException {

    public ResponseFilterCustomExceptionCustomException(String message) {
        super(message);
    }
}
