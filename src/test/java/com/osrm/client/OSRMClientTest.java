package com.osrm.client;

import static org.junit.Assert.assertEquals;

import com.sun.net.httpserver.HttpServer;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;

public class OSRMClientTest {

  @Test
  public void callsReuseTheSameHttpClientAndConnection() throws Exception {
    HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
    server.createContext("/", exchange -> {
      byte[] body = "{\"matrix\":[[0,1],[1,0]]}".getBytes(StandardCharsets.UTF_8);
      exchange.sendResponseHeaders(200, body.length);
      try (OutputStream os = exchange.getResponseBody()) {
        os.write(body);
      }
    });
    server.start();
    try {
      String uri = "http://127.0.0.1:" + server.getAddress().getPort();
      List<GeoLocation> locations = Arrays.asList(new GeoLocation(-33.4, -70.6), new GeoLocation(-33.5, -70.7));
      OSRMClient.HTTP_CLIENT.connectionPool().evictAll();

      new OSRMClient(uri).getDistanceMatrix(locations, 1.0, "CL", "token", "car", "{}");
      new OSRMClient(uri).getDistanceMatrix(locations, 1.0, "CL", "token", "car", "{}");

      assertEquals(1, OSRMClient.HTTP_CLIENT.connectionPool().connectionCount());
    } finally {
      server.stop(0);
    }
  }
}
