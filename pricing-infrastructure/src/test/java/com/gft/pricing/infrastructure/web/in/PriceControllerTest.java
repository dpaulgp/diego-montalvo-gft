package com.gft.pricing.infrastructure.web.in;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.gft.pricing.domain.exception.PriceNotFoundException;
import com.gft.pricing.domain.model.Price;
import com.gft.pricing.domain.model.PriceQuery;
import com.gft.pricing.domain.port.in.PriceQueryUseCase;
import com.gft.pricing.infrastructure.web.mapper.PriceResponseMapper;
import com.gft.pricing.infrastructure.web.model.generated.PriceResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Test unitario del PriceController.
 * Valida el comportamiento del controlador de forma aislada usando mocks.
 */
@WebMvcTest(PriceController.class)
class PriceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PriceQueryUseCase priceQueryUseCase;

    @MockBean
    private PriceResponseMapper mapper;

    @Test
    void shouldReturnPriceWhenValidRequest() throws Exception {
        // Given
        LocalDateTime startDate = LocalDateTime.of(2020, 6, 14, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2020, 12, 31, 23, 59, 59);
        Price price = new Price(1L, 1, 35455, 1, startDate, endDate, 0,
                BigDecimal.valueOf(35.50), "EUR");

        when(priceQueryUseCase.queryPrices(any(PriceQuery.class))).thenReturn(price);
        when(mapper.toResponse(price)).thenAnswer(invocation -> {
            Price p = invocation.getArgument(0);
            PriceResponse response = new PriceResponse();
            response.setProductId(p.productId());
            response.setBrandId(p.brandId());
            response.setPriceList(p.priceList());
            response.setStartDate("2020-06-14T00:00:00");
            response.setEndDate("2020-12-31T23:59:59");
            response.setAmount(p.amount());
            response.setCurrency(p.currency());
            return response;
        });

        // When & Then
        mockMvc.perform(get("/api/v1/prices")
                .param("brandId", "1")
                .param("productId", "35455")
                .param("applicationDate", "2020-06-14T10:00:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(35455))
                .andExpect(jsonPath("$.brandId").value(1))
                .andExpect(jsonPath("$.priceList").value(1))
                .andExpect(jsonPath("$.startDate").value("2020-06-14T00:00:00"))
                .andExpect(jsonPath("$.endDate").value("2020-12-31T23:59:59"))
                .andExpect(jsonPath("$.amount").value(35.50))
                .andExpect(jsonPath("$.currency").value("EUR"));

        verify(priceQueryUseCase).queryPrices(any(PriceQuery.class));
        verify(mapper).toResponse(price);
    }

    @Test
    void shouldReturnNotFoundWhenPriceDoesNotExist() throws Exception {
        // Given
        when(priceQueryUseCase.queryPrices(any(PriceQuery.class)))
                .thenThrow(new PriceNotFoundException(
                        "No price found for the given criteria: productId 35455 , brandId 1 , applicationDate 2020-06-14T10:00"));

        // When & Then
        mockMvc.perform(get("/api/v1/prices")
                .param("brandId", "1")
                .param("productId", "35455")
                .param("applicationDate", "2020-06-14T10:00:00"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Price not found"));

        verify(priceQueryUseCase).queryPrices(any(PriceQuery.class));
    }

    @Test
    void shouldReturnBadRequestWhenDateFormatIsInvalid() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/prices")
                .param("brandId", "1")
                .param("productId", "35455")
                .param("applicationDate", "invalid-date-format"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid request"));
    }

    @Test
    void shouldReturnBadRequestWhenDateIsNotISOFormat() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/prices")
                .param("brandId", "1")
                .param("productId", "35455")
                .param("applicationDate", "2020/06/14 10:00:00"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid request"));
    }

    @Test
    void shouldReturnBadRequestWhenMissingRequiredParameter() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/prices")
                .param("brandId", "1")
                .param("productId", "35455"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid request"));
    }
}
