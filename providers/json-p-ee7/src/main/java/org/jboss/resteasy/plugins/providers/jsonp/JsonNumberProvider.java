package org.jboss.resteasy.plugins.providers.jsonp;

import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;

import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonReader;
import javax.json.JsonWriter;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Consumes({"application/json", "application/*+json", "text/json"})
@Produces({"application/json", "application/*+json", "text/json"})
public class JsonNumberProvider extends AbstractJsonpProvider implements MessageBodyReader<JsonNumber>, MessageBodyWriter<JsonNumber> {

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return JsonNumber.class.isAssignableFrom(type);
    }

    @Override
    public JsonNumber readFrom(Class<JsonNumber> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
        LogMessages.LOGGER.debugf("Provider : %s,  Method : readFrom", getClass().getName());
        JsonReader reader = findReader(mediaType, entityStream);
        try
        {
            return (JsonNumber) reader.readValue();
        }
        finally
        {
            reader.close();
        }
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return JsonNumber.class.isAssignableFrom(type);
    }

    @Override
    public void writeTo(JsonNumber number, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        LogMessages.LOGGER.debugf("Provider : %s,  Method : writeTo", getClass().getName());
        JsonWriter writer = findWriter(mediaType, entityStream);
        try
        {
            writer.write(number);
        }
        finally
        {
            writer.close();
        }
    }
}
