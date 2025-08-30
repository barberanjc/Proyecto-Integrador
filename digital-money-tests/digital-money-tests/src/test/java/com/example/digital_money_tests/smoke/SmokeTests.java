package com.example.digital_money_tests.smoke;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
public class SmokeTests {
    private static final String BASE_URL = "http://localhost:8080/account-service";

    @Test
    void testUltimosMovimientos() {
        given()
                .baseUri(BASE_URL)
                .when()
                .get("/accounts/1/transactions?limit=5")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", lessThanOrEqualTo(5));
    }

    @Test
    void testPerfilCuenta() {
        given()
                .baseUri(BASE_URL)
                .when()
                .get("/accounts/1/profile")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("cvu", notNullValue())
                .body("alias", notNullValue());
    }

    @Test
    void testListarTarjetas() {
        given()
                .baseUri(BASE_URL)
                .when()
                .get("/cards/accounts/1/cards")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", greaterThanOrEqualTo(0));
    }

    @Test
    void testRecargarCuenta() {
        given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .body("{ \"cardId\": 1, \"amount\": 500.0 }")
                .when()
                .post("/accounts/1/recharge")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("message", notNullValue())
                .body("newBalance", greaterThan(0.0f));
    }

    @Test
    void testListadoTransacciones() {
        given()
                .baseUri(BASE_URL)
                .when()
                .get("/accounts/1/activity")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", greaterThanOrEqualTo(0));
    }
}