package com.gft.pricing.infrastructure;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import io.restassured.RestAssured;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PriceControllerE2ETest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    private static Stream<Arguments> priceRequests() {
        return Stream.of(
                // Test 1: petición a las 10:00 del día 14 del producto 35455 para la brand 1 (ZARA)
                Arguments.of("2020-06-14T10:00:00", 1, 35.50,
                        "2020-06-14T00:00:00", "2020-12-31T23:59:59"),
                // Test 2: petición a las 16:00 del día 14 del producto 35455 para la brand 1 (ZARA)
                Arguments.of("2020-06-14T16:00:00", 2, 25.45,
                        "2020-06-14T15:00:00", "2020-06-14T18:30:00"),
                // Test 3: petición a las 21:00 del día 14 del producto 35455 para la brand 1 (ZARA)
                Arguments.of("2020-06-14T21:00:00", 1, 35.50,
                        "2020-06-14T00:00:00", "2020-12-31T23:59:59"),
                // Test 4: petición a las 10:00 del día 15 del producto 35455 para la brand 1 (ZARA)
                Arguments.of("2020-06-15T10:00:00", 3, 30.50,
                        "2020-06-15T00:00:00", "2020-06-15T11:00:00"),
                // Test 5: petición a las 21:00 del día 16 del producto 35455 para la brand 1 (ZARA)
                Arguments.of("2020-06-16T21:00:00", 4, 38.95,
                        "2020-06-15T16:00:00", "2020-12-31T23:59:59")
        );
    }

    @ParameterizedTest
    @MethodSource("priceRequests")
    void shouldReturnExpectedPriceForScenario(String applicationDate,
                                              int expectedPriceList,
                                              double expectedAmount,
                                              String expectedStartDate,
                                              String expectedEndDate) {
        given()
                .queryParam("brandId", 1)
                .queryParam("productId", 35455)
                .queryParam("applicationDate", applicationDate)
        .when()
                .get("/api/v1/prices")
        .then()
                .statusCode(200)
                .body("productId", equalTo(35455))
                .body("brandId", equalTo(1))
                .body("priceList", equalTo(expectedPriceList))
                .body("startDate", equalTo(expectedStartDate))
                .body("endDate", equalTo(expectedEndDate))
                .body("amount", equalTo((float) expectedAmount))
                .body("currency", equalTo("EUR"));
    }

    @Test
    void shouldReturnNotFoundForUnknownProduct() {
        given()
                .queryParam("brandId", 1)
                .queryParam("productId", 99999)
                .queryParam("applicationDate", "2020-06-14T10:00:00")
        .when()
                .get("/api/v1/prices")
        .then()
                .statusCode(404)
                .body("error", equalTo("Price not found"));
    }
}
