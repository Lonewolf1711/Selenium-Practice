# Selenium-Practice: Security-Aware Test Automation Framework

![Tests](https://github.com/Lonewolf1711/Selenium-Practice/actions/workflows/tests.yml/badge.svg)

A Java test automation framework that combines functional UI testing, API testing, and lightweight automated security checks in a single suite.

## What it covers
- **UI functional tests**: login, cart, and checkout flows on Sauce Demo and the-internet.herokuapp.com, built with the Page Object Model.
- **Security probes**: XSS and SQL injection payloads submitted through login forms, with a scanner that flags script execution, unescaped reflection, and leaked error text.
- **API tests**: Rest Assured tests for status codes, payloads, and error handling.
- **Security header audit**: checks for missing `X-Content-Type-Options`, `X-Frame-Options`, `Content-Security-Policy`, and cookies missing `HttpOnly`/`Secure`.

## Tech stack
Java 17, Selenium 4, JUnit 5, Rest Assured, Maven, GitHub Actions.

## Structure
```
src/main/java/org/example
    BasePage.java            shared explicit-wait helpers
    *Page.java               page objects (one per page)
    SecurityPayloads.java    injection payload library
    SecurityScanner.java     unsafe-behaviour detection
src/test/java/org/example
    BaseTest.java            browser setup/teardown
    *Test.java               functional, security, and API tests
```

## Design decisions
- **Page Object Model**: locators live in one place, so a UI change means one fix, not many.
- **Explicit waits** (`WebDriverWait`) instead of `Thread.sleep`: tests wait only as long as needed and fail with a clear timeout.
- **The scanner is tested itself** against a deliberately vulnerable page, so a passing security test actually means something.
- **Header audit runs against a local HTTP server** with known-good and known-bad endpoints, so results are deterministic and need no network.

## Running
```
mvn test
```
Set the `CI` environment variable to run Chrome headless.

## Limitations
- The scanner uses heuristics (page-source reflection, alert detection, error signatures). It is not a replacement for tools like OWASP ZAP or Burp Suite.
- Injection probes currently cover login forms only.
- Run security tests only against practice sites or applications you own or have permission to test.

## Possible next steps
- Extend payload probes to search boxes and registration forms.
- Add HSTS checks for HTTPS targets.
- Publish an HTML test report from CI.