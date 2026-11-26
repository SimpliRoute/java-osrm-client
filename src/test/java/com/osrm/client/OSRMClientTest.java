package com.osrm.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.osrm.client.exception.OSRMClientException;
import com.osrm.client.request.CostMatricesRequest;
import com.osrm.client.request.GeoLocation;
import com.sun.net.httpserver.HttpServer;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Test;

public class OSRMClientTest {

  @Test
  public void callsReuseTheSameHttpClientAndConnection() throws Exception {
    HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
    server.createContext("/", exchange -> {
      byte[] body = "{}".getBytes(StandardCharsets.UTF_8);
      exchange.sendResponseHeaders(200, body.length);
      try (OutputStream os = exchange.getResponseBody()) {
        os.write(body);
      }
    });
    server.start();
    try {
      String uri = "http://127.0.0.1:" + server.getAddress().getPort();
      CostMatricesRequest request = CostMatricesRequest.builder()
          .locations(Arrays.asList(new GeoLocation(-33.4, -70.6), new GeoLocation(-33.5, -70.7)))
          .speedRate(1.0)
          .country("CL")
          .token("token")
          .profile("car")
          .customParameters(Collections.emptyMap())
          .build();
      OSRMClient.httpClient().connectionPool().evictAll();

      new OSRMClient(uri).getCostMatrices(request);
      new OSRMClient(uri).getCostMatrices(request);

      assertEquals(1, OSRMClient.httpClient().connectionPool().connectionCount());
    } finally {
      server.stop(0);
    }
  }

  @Test
  public void handlesNullAndEncodedCustomParametersWithoutNpe() throws Exception {
    AtomicReference<String> receivedBody = new AtomicReference<>();
    HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
    server.createContext("/", exchange -> {
      try (InputStream is = exchange.getRequestBody()) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] chunk = new byte[256];
        int read;
        while ((read = is.read(chunk)) != -1) {
          buffer.write(chunk, 0, read);
        }
        receivedBody.set(new String(buffer.toByteArray(), StandardCharsets.UTF_8));
      }
      byte[] body = "{}".getBytes(StandardCharsets.UTF_8);
      exchange.sendResponseHeaders(200, body.length);
      try (OutputStream os = exchange.getResponseBody()) {
        os.write(body);
      }
    });
    server.start();
    try {
      String uri = "http://127.0.0.1:" + server.getAddress().getPort();
      CostMatricesRequest requestWithoutCustomParams = CostMatricesRequest.builder()
          .locations(Arrays.asList(new GeoLocation(-33.4, -70.6), new GeoLocation(-33.5, -70.7)))
          .speedRate(1.0)
          .country("CL")
          .token("token")
          .profile("car")
          .build();

      new OSRMClient(uri).getCostMatrices(requestWithoutCustomParams);
      assertTrue(receivedBody.get().contains("country=CL"));

      Map<String, Object> customParams = new HashMap<>();
      customParams.put("json_options", "{\"avoid\":\"tolls&ferries\"}");
      customParams.put("ignored_null", null);
      CostMatricesRequest requestWithCustomParams = CostMatricesRequest.builder()
          .locations(Arrays.asList(new GeoLocation(-33.4, -70.6), new GeoLocation(-33.5, -70.7)))
          .speedRate(1.0)
          .country("CL")
          .token("token")
          .profile("car")
          .customParameters(customParams)
          .build();

      new OSRMClient(uri).getCostMatrices(requestWithCustomParams);
      assertTrue(receivedBody.get().contains("json_options=%7B%22avoid%22%3A%22tolls%26ferries%22%7D"));
      assertFalse(receivedBody.get().contains("ignored_null"));
    } finally {
      server.stop(0);
    }
  }

  @Test
  public void throwsOSRMClientExceptionOnHttpErrorStatus() throws Exception {
    HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
    server.createContext("/", exchange -> {
      byte[] body = "{}".getBytes(StandardCharsets.UTF_8);
      exchange.sendResponseHeaders(500, body.length);
      try (OutputStream os = exchange.getResponseBody()) {
        os.write(body);
      }
    });
    server.start();
    try {
      String uri = "http://127.0.0.1:" + server.getAddress().getPort();
      CostMatricesRequest request = CostMatricesRequest.builder()
          .locations(Arrays.asList(new GeoLocation(-33.4, -70.6), new GeoLocation(-33.5, -70.7)))
          .speedRate(1.0)
          .country("CL")
          .token("token")
          .profile("car")
          .build();
      try {
        new OSRMClient(uri).getCostMatrices(request);
        fail("Expected OSRMClientException on HTTP 500");
      } catch (OSRMClientException expected) {
        assertTrue(expected.getMessage().contains("500"));
      }
    } finally {
      server.stop(0);
    }
  }

  @Test
  public void supportsNonSquareCostMatrixAndSafeGeoLocationEquals() {
    CostMatrix matrix = new CostMatrix();
    matrix.setMatrix(new int[][] { { 10, 20, 30 }, { 40, 50, 60 } });
    List<List<Float>> rows = matrix.asList();
    assertEquals(2, rows.size());
    assertEquals(3, rows.get(0).size());
    assertEquals(Float.valueOf(60.0f), rows.get(1).get(2));

    GeoLocation loc = new GeoLocation(-33.4, -70.6);
    assertFalse(loc.equals(null));
    assertFalse(loc.equals("not-a-location"));
    assertTrue(loc.equals(new GeoLocation(-33.4, -70.6)));
    assertFalse(loc.equals(new GeoLocation(-33.4, -70.7)));
  }
}
