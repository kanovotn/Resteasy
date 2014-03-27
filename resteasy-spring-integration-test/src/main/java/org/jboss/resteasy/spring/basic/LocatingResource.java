package org.jboss.resteasy.spring.basic;

import javax.ws.rs.Path;

@Path("/")
public class LocatingResource {
  public static final String PATH = "locating";
  private final SimpleResource simpleResource;

  public LocatingResource(SimpleResource simpleResource) {
    this.simpleResource = simpleResource;

  }

  @Path(PATH)
  public SimpleResource getLocating() {
    System.out.println("LOCATING...");
    return simpleResource;
  }
}