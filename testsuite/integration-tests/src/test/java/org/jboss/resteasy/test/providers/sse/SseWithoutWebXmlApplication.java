package org.jboss.resteasy.test.providers.sse;

import javax.ws.rs.core.Application;
import javax.ws.rs.ext.Provider;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by kanovotn on 2/7/18.
 */
@Provider
public class SseWithoutWebXmlApplication extends Application
{
    private Set<Object> singletons = new HashSet<Object>();

    public Set<Object> getSingletons()
    {
        if (singletons.isEmpty())
        {
            SseWithoutWebXmlResource sseResource = new SseWithoutWebXmlResource();
            singletons.add(sseResource);
        }
        return singletons;
    }

}
