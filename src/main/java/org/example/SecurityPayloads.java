package org.example;

import java.util.ArrayList;
import java.util.List;

public final class SecurityPayloads {

    public static final List<String> XSS = List.of(
            "<script>alert(1)</script>",
            "\"><img src=x onerror=alert(1)>",
            "<svg onload=alert(1)>");

    public static final List<String> SQL_INJECTION = List.of(
            "' OR '1'='1",
            "' OR 1=1 --",
            "admin'--");

    private SecurityPayloads() {}

    public static List<String> all() {
        List<String> all = new ArrayList<>(XSS);
        all.addAll(SQL_INJECTION);
        return all;
    }
}