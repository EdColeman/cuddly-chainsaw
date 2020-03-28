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

import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.util.ssl.SslContextFactory;
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

public class HelloSLLWorld extends AbstractHandler {

  public static final Logger log = LoggerFactory.getLogger(HelloSLLWorld.class);

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

    int port = 8080;
    int sslPort = 8433;

    log.error("an error 1");
    log.debug("Starting");
    log.trace("T Starting");
    log.error("an error 2");

    Path keystorePath = Paths.get("src/main/resources/etc/keystore").toAbsolutePath();
    if (!Files.exists(keystorePath))
      throw new FileNotFoundException(keystorePath.toString());

  Server server = new Server();

    HttpConfiguration httpConfig = new HttpConfiguration();
    httpConfig.setSecureScheme("https");
    httpConfig.setSecurePort(sslPort);
    httpConfig.setOutputBufferSize(32768);

    ServerConnector http = new ServerConnector(server,
        new HttpConnectionFactory(httpConfig));
    http.setPort(port);
    http.setIdleTimeout(30000);

    SslContextFactory sslContextFactory = new SslContextFactory.Server();
    sslContextFactory.setKeyStorePath(keystorePath.toString());
    sslContextFactory.setKeyStorePassword("123456");
    sslContextFactory.setKeyManagerPassword("123456");

    HttpConfiguration httpsConfig = new HttpConfiguration(httpConfig);
    SecureRequestCustomizer src = new SecureRequestCustomizer();
    src.setStsMaxAge(2000);
    src.setStsIncludeSubDomains(true);
    httpsConfig.addCustomizer(src);

    ServerConnector https = new ServerConnector(server,
        new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString()),
        new HttpConnectionFactory(httpsConfig));
    https.setPort(sslPort);

    server.setConnectors(new Connector[]{http, https});

    https.setIdleTimeout(500000);
    server.setHandler(new HelloSLLWorld());

    server.start();
    server.dumpStdErr();
    server.join();
  }
}
