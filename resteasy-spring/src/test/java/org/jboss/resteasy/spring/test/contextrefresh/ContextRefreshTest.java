package org.jboss.resteasy.spring.test.contextrefresh;

import static org.junit.Assert.*;

import java.util.Enumeration;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;
import org.springframework.web.context.WebApplicationContext;

/**
 * RESTEASY-632.
 * Test suggested by Holger Morch.
 *
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 * Created Feb 12, 2012
 */
public class ContextRefreshTest
{

   private WebAppContext context;

   @Before
   public void before() throws Exception
   {
      Server server = new Server(9092);
      context = new WebAppContext();
      context.setDescriptor("WEB-INF/web.xml");
      context.setResourceBase("src/test/resources");
      context.setContextPath("/");
      context.setParentLoaderPriority(true);
      server.setHandler(context);
      server.start();
   }

   @After
   public void after() throws Exception {
     context.stop();
   }


   @Test
   public void testContextRefresh() throws Exception
   {
      assertTrue(TriggerRefresh.isOK());
      Enumeration<?> en = TriggerRefresh.getApplicationContext().getServletContext().getAttributeNames();
      while (en.hasMoreElements())
      {
         System.out.println(en.nextElement());
      }
      Object o = TriggerRefresh.getApplicationContext().getServletContext().getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
      System.out.println(o);
      assertFalse(o instanceof Exception);
   }
}
