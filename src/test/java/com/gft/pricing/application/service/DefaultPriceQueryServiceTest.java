package com.gft.pricing.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import com.gft.pricing.domain.exception.PriceNotFoundException;
import com.gft.pricing.domain.model.Price;
import com.gft.pricing.domain.model.PriceQuery;
import com.gft.pricing.domain.port.out.PriceRepoPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class DefaultPriceQueryServiceTest {

    @Mock
    private PriceRepoPort priceRepoPort;

    private PriceQueryService service;

    @BeforeEach
    void setUp() {
        service = new PriceQueryService(priceRepoPort);
    }

    @Test
    void shouldReturnPriceWhenExists() {
        LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 14, 10, 0);
        LocalDateTime startDate = applicationDate.minusHours(1);
        LocalDateTime endDate = applicationDate.plusHours(4);
        PriceQuery query = new PriceQuery(applicationDate, 35455, 1);
        Price expectedPrice = new Price(2L, 1, 35455, 2, startDate, endDate, 1,
                BigDecimal.valueOf(25.45), "EUR");
        when(priceRepoPort.findTopPriceByCriteria(query)).thenReturn(Optional.of(expectedPrice));

        Price result = service.queryPrices(query);

        assertThat(result).isEqualTo(expectedPrice);
        verify(priceRepoPort).findTopPriceByCriteria(query);
    }

    @Test
    void shouldFailWhenNoPriceExists() {
        LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 14, 10, 0);
        PriceQuery query = new PriceQuery(applicationDate, 35455, 1);
        when(priceRepoPort.findTopPriceByCriteria(query)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.queryPrices(query))
                .isInstanceOf(PriceNotFoundException.class)
                .hasMessage("No price found for the given criteria: productId 35455 , brandId 1 , applicationDate 2020-06-14T10:00");
        verify(priceRepoPort).findTopPriceByCriteria(query);
    }
}
