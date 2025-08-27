package com.example.digital_money_tests.smoke;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
public class SmokeTests {
    private static final String BASE_URL = "http://localhost:8080/account-service";

    // P-7 Ver últimos 5 movimientos
    @Test
    void testUltimosMovimientos() {
        given()
                .baseUri(BASE_URL)
                .when()
                .get("/accounts/1/transactions?limit=5")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", lessThanOrEqualTo(5)); // máximo 5, puede ser menos
    }

    // P-8 Ver perfil de cuenta
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

    // P-10 Listar tarjetas asociadas
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
}