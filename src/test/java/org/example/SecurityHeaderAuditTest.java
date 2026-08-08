package org.example;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SecurityHeaderAuditTest {

    private static final List<String> REQUIRED_HEADERS = List.of(
            "X-Content-Type-Options", "X-Frame-Options", "Content-Security-Policy");

    private static HttpServer server;
    private static String baseUrl;

    static List<String> audit(Response response) {
        List<String> findings = new ArrayList<>();
        for (String header : REQUIRED_HEADERS) {
            if (!response.getHeaders().hasHeaderWithName(header)) {
                findings.add("Missing header: " + header);
            }
        }
        for (String cookie : response.getHeaders().getValues("Set-Cookie")) {
            String name = cookie.split("=", 2)[0];
            String attributes = cookie.contains(";") ? cookie.substring(cookie.indexOf(';')).toLowerCase() : "";
            if (!attributes.contains("httponly")) {
                findings.add("Cookie '" + name + "' missing HttpOnly");
            }
            if (!attributes.contains("secure")) {
                findings.add("Cookie '" + name + "' missing Secure");
            }
        }
        return findings;
    }

    private static void respond(HttpExchange exchange) throws IOException {
        byte[] body = "ok".getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, body.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(body);
        }
    }

    @BeforeAll
    static void startServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        server.createContext("/secure", exchange -> {
            exchange.getResponseHeaders().add("X-Content-Type-Options", "nosniff");
            exchange.getResponseHeaders().add("X-Frame-Options", "DENY");
            exchange.getResponseHeaders().add("Content-Security-Policy", "default-src 'self'");
            exchange.getResponseHeaders().add("Set-Cookie", "session=abc123; Secure; HttpOnly");
            respond(exchange);
        });
        server.createContext("/insecure", exchange -> {
            exchange.getResponseHeaders().add("Set-Cookie", "session=abc123");
            respond(exchange);
        });
        server.start();
        baseUrl = "http://localhost:" + server.getAddress().getPort();
    }

    @AfterAll
    static void stopServer() {
        server.stop(0);
    }

    @Test
    void secureEndpointHasNoFindings() {
        Response response = given().get(baseUrl + "/secure");
        List<String> findings = audit(response);
        assertTrue(findings.isEmpty(), "Unexpected findings: " + findings);
    }

    @Test
    void insecureEndpointIsFlagged() {
        Response response = given().get(baseUrl + "/insecure");
        List<String> findings = audit(response);
        assertTrue(findings.contains("Missing header: X-Frame-Options"));
        assertTrue(findings.contains("Cookie 'session' missing HttpOnly"));
        assertEquals(5, findings.size());
    }
}