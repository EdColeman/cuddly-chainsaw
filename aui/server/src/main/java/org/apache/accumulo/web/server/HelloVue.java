/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.accumulo.web.server;

import org.apache.accumulo.web.server.api.GetSample;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.PathResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HelloVue extends AbstractHandler {

  public static final Logger log = LoggerFactory.getLogger(HelloVue.class);

  @Override
  public void handle(String target,
      Request baseRequest,
      HttpServletRequest request,
      HttpServletResponse response) throws IOException, ServletException
  {
    // Declare response encoding and types
    response.setContentType("text/html; charset=utf-8");

    // Declare response status code
    response.setStatus(HttpServletResponse.SC_OK);

    // Write back response
    response.getWriter().println("<h1>Hello World</h1>");

    // Inform jetty that this request has now been handled
    baseRequest.setHandled(true);
  }

  public static void main(String... args) throws Exception {
    log.error("an error 1");
    log.debug("Starting");
    log.trace("T Starting");
    log.error("an error 2");

    int port = 8080;
    Server server = new Server(port);

    Path basePath = Paths.get("src/main/web/dist").toAbsolutePath();
    if (!Files.exists(basePath))
      throw new FileNotFoundException(basePath.toString());

    // a Jetty Handler object so it is suitable for chaining with other handlers as you will see in other examples.
    ResourceHandler resourceHandler = new ResourceHandler();

    // Configure the ResourceHandler. Setting the resource base indicates where the files should be served out of.
    // In this example it is the current directory but it can be configured to anything that the jvm has access to.
    resourceHandler.setDirectoriesListed(true);
    resourceHandler.setWelcomeFiles(new String[]{"index.html"});
    resourceHandler.setBaseResource(new PathResource(basePath));


    // rest

    ServletContextHandler restHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
    restHandler.setContextPath("/");

    // Add the ResourceHandler to the server.
    HandlerList handlers = new HandlerList();

    handlers.setHandlers(new Handler[]{resourceHandler, restHandler, new DefaultHandler()});
    server.setHandler(handlers);

    ServletHolder jerseyServlet = restHandler.addServlet(
        org.glassfish.jersey.servlet.ServletContainer.class, "/*");
    jerseyServlet.setInitOrder(0);

    // Tells the Jersey Servlet which REST service/class to load.
    jerseyServlet.setInitParameter(
        "jersey.config.server.provider.classnames",
        GetSample.class.getCanonicalName());

    // server.setHandler(new HelloVue());

    server.start();
    server.join();
  }
}
