package org.example;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

public class PostsApiTest {

    @BeforeAll
    static void configure() {
        RestAssured.baseURI = "https://jsonplaceholder.typicode.com";
    }

    @Test
    void getPostReturnsExpectedFields() {
        given().when().get("/posts/1")
                .then().statusCode(200)
                .body("id", equalTo(1))
                .body("userId", equalTo(1))
                .body("title", not(emptyOrNullString()));
    }

    @Test
    void listReturnsHundredPosts() {
        given().when().get("/posts")
                .then().statusCode(200)
                .body("size()", equalTo(100));
    }

    @Test
    void createPostReturns201() {
        given().contentType(ContentType.JSON)
                .body("{\"title\":\"qa\",\"body\":\"test\",\"userId\":1}")
                .when().post("/posts")
                .then().statusCode(201)
                .body("id", notNullValue())
                .body("title", equalTo("qa"));
    }

    @Test
    void missingPostReturns404() {
        given().when().get("/posts/99999")
                .then().statusCode(404);
    }
}