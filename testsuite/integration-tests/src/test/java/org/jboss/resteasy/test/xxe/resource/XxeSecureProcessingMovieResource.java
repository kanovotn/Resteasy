package org.jboss.resteasy.test.xxe.resource;

import org.jboss.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

@Path("/")
public class XxeSecureProcessingMovieResource {
    private static Logger logger = Logger.getLogger(XxeSecureProcessingMovieResource.class);

    @POST
    @Path("xmlRootElement")
    @Consumes({"application/xml"})
    public String addFavoriteMovie(XxeSecureProcessingFavoriteMovieXmlRootElement movie) {
        logger.info("XxeSecureProcessingMovieResource(xmlRootElment): title = " + movie.getTitle().substring(0, 30));
        return movie.getTitle();
    }

    @POST
    @Path("test")
    @Consumes({"application/*+xml"})
    @Produces({"application/*+xml"})
    public Source testSource(Source mySource) {
        String resultXmlStr = null;
        try {
            Transformer transformer= TransformerFactory.newInstance().newTransformer();
            StreamResult xmlOutput=new StreamResult(new StringWriter());
            transformer.transform(mySource,xmlOutput);
            logger.info(xmlOutput.getWriter().toString());
            resultXmlStr = xmlOutput.getWriter().toString();
        } catch (TransformerConfigurationException e) {
            logger.error("Failed to create transformer",e);
        } catch (TransformerException e) {
            logger.error("Failed to trasform Source to xml result", e);
        }

        InputStream stream = new ByteArrayInputStream(resultXmlStr.getBytes(StandardCharsets.UTF_8));
        return new StreamSource(stream);
    }
}
