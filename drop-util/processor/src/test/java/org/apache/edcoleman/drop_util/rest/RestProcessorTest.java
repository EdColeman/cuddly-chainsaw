package org.apache.edcoleman.drop_util.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.http.HttpMethod;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RestProcessorTest {

    private static Logger log = LogManager.getLogger(RestProcessorTest.class);

    private static RestProcessor rest;
    private static HttpClient httpclient;

    @BeforeAll
    public static void init() {
        try {

            rest = new RestProcessor();


            httpclient = new HttpClient();
            httpclient.start();


        } catch (Exception ex) {
            log.debug("HttpClient start exception", ex);
            throw new IllegalStateException(ex);
        }

    }

    @AfterAll
    public static void teardown() {
        try {
            httpclient.stop();

            rest.shutdown();
        } catch (Exception ex) {
            log.debug("HttpClient shutdown exception", ex);
        }
    }

    @Test
    public void get() throws Exception {


        // ContentResponse response = httpclient.GET("http://localhost:8081/api/hello");

        ContentResponse response = httpclient.newRequest("http://localhost:8081/api/config/all")
                .method(HttpMethod.GET)
                //.param()
                .send();

        log.debug("RESP: {} --> {}", response, response.getContentAsString());

    }


    @Test
    public void query() throws Exception {

        // ContentResponse response = httpclient.GET("http://localhost:8081/api/hello");

        ContentResponse response = httpclient.newRequest("http://localhost:8081/api/config/one")
                .method(HttpMethod.GET)
                .param("p1", "aprop")
                .send();

        log.debug("RESP: {} --> {}", response, response.getContentAsString());


//        HttpGet httpget = new HttpGet("http://localhost/");
//        HttpResponse response = httpclient.execute(httpget);
//        HttpEntity entity = response.getEntity();
//        if (entity != null) {
//            InputStream instream = entity.getContent();
//            try {
//                // do something useful
//            } finally {
//                instream.close();
//            }
//        }

        httpclient.stop();

    }


    @Test
    public void put() throws Exception {

        // ContentResponse response = httpclient.GET("http://localhost:8081/api/hello");

        ContentResponse response = httpclient.newRequest("http://localhost:8081/api/config/set")
                .method(HttpMethod.PUT)
                .param("v1", "avalue")
                .send();

        log.debug("RESP: {} --> {}", response, response.getContentAsString());


//        HttpGet httpget = new HttpGet("http://localhost/");
//        HttpResponse response = httpclient.execute(httpget);
//        HttpEntity entity = response.getEntity();
//        if (entity != null) {
//            InputStream instream = entity.getContent();
//            try {
//                // do something useful
//            } finally {
//                instream.close();
//            }
//        }

        httpclient.stop();

    }
}
