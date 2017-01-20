package org.jboss.resteasy.test.cdi.injection;

import org.jboss.logging.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.cdi.injection.resource.*;
import org.jboss.resteasy.test.cdi.util.Constants;
import org.jboss.resteasy.test.cdi.util.Counter;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Iterator;

@RunWith(Arquillian.class)
@RunAsClient
public class InjectionSessionScopedTest {

    // Uncomment if creating client with ClientBuilder
    static Client client;

    // Uncomment if creating client with ResteasyClientBuilder
    //static ResteasyClient client;

    private static int invocationCounter;
    protected static final Logger log = Logger.getLogger(InjectionSessionScopedTest.class.getName());

    static ParameterizedType BookCollectionType = new ParameterizedType() {
        @Override
        public Type[] getActualTypeArguments() {
            return new Type[]{CDIInjectionBook.class};
        }

        @Override
        public Type getRawType() {
            return Collection.class;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }
    };

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(InjectionSessionScopedTest.class.getSimpleName());
            war.addClasses(CDIInjectionBook.class, CDIInjectionBookBagLocal.class, CDIInjectionBookBag.class, InjectionSessionScopedResource.class)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        return TestUtil.finishContainerPrepare(war, null, (Class<?>[]) null);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, InjectionSessionScopedTest.class.getSimpleName());
    }

    @BeforeClass
    public static void init() {
        // Uncomment one of the two lines below to reproduce the issue
        client = ClientBuilder.newClient();
        //client = new ResteasyClientBuilder().build();

        // Uncomment to fix the issue
        //client = new ResteasyClientBuilder().buildOld();
    }

    @AfterClass
    public static void close() {
        client.close();
    }

    @Test
    public void testSessionScope() throws Exception {
        log.info("starting testSessionScope()");

        // Need to supply each ClientRequest with a single ClientExecutor to maintain a single
        // cookie cache, which keeps the session alive.
        //ClientExecutor executor = new ApacheHttpClient4Executor();

        // Create a book, which gets stored in the session scoped BookBag.
        WebTarget base = client.target(generateURL("/session/add/"));
        CDIInjectionBook book1 = new CDIInjectionBook(13, "Dead Man Napping");
        Response response = base.request().post(Entity.entity(book1, Constants.MEDIA_TYPE_TEST_XML));
        invocationCounter++;
        log.info("status: " + response.getStatus());
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        response.close();

        // Create another book, which should get stored in the same BookBag.
        base = client.target(generateURL("/session/add/"));
        CDIInjectionBook book2 = new CDIInjectionBook(Counter.INITIAL_VALUE, "Dead Man Dozing");
        response = base.request().post(Entity.entity(book2, Constants.MEDIA_TYPE_TEST_XML));
        invocationCounter++;
        log.info("status: " + response.getStatus());
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        response.close();

        // Get the current contents of the BookBag, and verify that it holds both of the books sent in the
        // previous two invocations.  When this method is called, the session is terminated.
        base = client.target(generateURL("/session/get/"));
        response = base.request().get();
        invocationCounter++;
        log.info("status: " + response.getStatus());
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        @SuppressWarnings("unchecked")
        Collection<CDIInjectionBook> books = response.readEntity(new GenericType<>(BookCollectionType));
        log.info("Collection from response: " + books);
        Assert.assertEquals(2, books.size());
        Iterator<CDIInjectionBook> it = books.iterator();
        CDIInjectionBook b1 = it.next();
        CDIInjectionBook b2 = it.next();
        log.info("First book in list: " + b1);
        log.info("Second book in list: " + b2);
        Assert.assertTrue(book1.equals(b1) && book2.equals(b2) || book1.equals(b2) && book2.equals(b1));
        response.close();

        // Verify that the BookBag has been replaced by a new, empty one for the new session.
        base = client.target(generateURL("/session/test/"));
        response = base.request().post(Entity.text(new String()));
        invocationCounter++;
        log.info("status: " + response.getStatus());
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        response.close();
    }
}
