package com.gft.pricing.infrastructure;

 import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
 import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
 import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

 import java.util.stream.Stream;

 import org.junit.jupiter.api.Test;
 import org.junit.jupiter.params.ParameterizedTest;
 import org.junit.jupiter.params.provider.Arguments;
 import org.junit.jupiter.params.provider.MethodSource;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
 import org.springframework.boot.test.context.SpringBootTest;
 import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class PriceControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

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
                                              String expectedEndDate) throws Exception {
        mockMvc.perform(get("/api/v1/prices")
                        .param("brandId", "1")
                        .param("productId", "35455")
                        .param("applicationDate", applicationDate))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(35455))
                .andExpect(jsonPath("$.brandId").value(1))
                .andExpect(jsonPath("$.priceList").value(expectedPriceList))
                .andExpect(jsonPath("$.startDate").value(expectedStartDate))
                .andExpect(jsonPath("$.endDate").value(expectedEndDate))
                .andExpect(jsonPath("$.amount").value(expectedAmount))
                .andExpect(jsonPath("$.currency").value("EUR"));
    }

    @Test
    void shouldReturnNotFoundWhenNoPriceExists() throws Exception {
        mockMvc.perform(get("/api/v1/prices")
                        .param("brandId", "1")
                        .param("productId", "99999")
                        .param("applicationDate", "2020-06-14T10:00:00"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Price not found"));
    }

    @Test
    void shouldReturnBadRequestWhenParametersAreInvalid() throws Exception {
        mockMvc.perform(get("/api/v1/prices")
                        .param("brandId", "0")
                        .param("productId", "35455")
                        .param("applicationDate", "2020-06-14T10:00:00"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid request"));
    }
}
