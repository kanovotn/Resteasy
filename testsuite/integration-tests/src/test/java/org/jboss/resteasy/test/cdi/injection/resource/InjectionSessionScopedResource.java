package org.jboss.resteasy.test.cdi.injection.resource;

import org.jboss.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;

@Path("/")
@RequestScoped
public class InjectionSessionScopedResource {

    @Inject
    private CDIInjectionBookBagLocal bookBag;       // session scoped SFSB

    private static Logger log = Logger.getLogger(InjectionSessionScopedResource.class);

    @POST
    @Path("session/add")
    public Response sessionAdd(@Context HttpServletRequest request, CDIInjectionBook book) {
        log.info("entering sessionAdd()");
        log.info("new session: " + request.getSession().isNew());
        bookBag.addBook(book);
        return Response.ok().build();
    }

    @GET
    @Path("session/get")
    @Produces(MediaType.APPLICATION_XML)
    public Collection<CDIInjectionBook> sessionGetBag(@Context HttpServletRequest request) {
        log.info("entering sessionGetBag()");
        log.info("new session: " + request.getSession().isNew());
        Collection<CDIInjectionBook> books = bookBag.getContents();
        log.info("sessionGetBag(): " + books);
        request.getSession().invalidate();
        return books;
    }

    @POST
    @Path("session/test")
    public Response sessionTest(@Context HttpServletRequest request) {
        log.info("entering sessionTest()");
        log.info("new session: " + request.getSession().isNew());
        Collection<CDIInjectionBook> contents = bookBag.getContents();
        log.info("bookBag: " + contents);
        if (request.getSession().isNew() && contents.isEmpty()) {
            return Response.ok().build();
        } else {
            return Response.serverError().build();
        }
    }
}
