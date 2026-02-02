package com.gft.pricing.infrastructure.persistence.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gft.pricing.domain.model.Price;
import com.gft.pricing.domain.model.PriceQuery;
import com.gft.pricing.infrastructure.persistence.entity.PriceEntity;
import com.gft.pricing.infrastructure.persistence.mapper.PriceEntityMapper;
import com.gft.pricing.infrastructure.persistence.repo.PriceRepo;

/**
 * Test unitario del PriceAdapter.
 * Valida el mapeo correcto entre entidades JPA y el modelo de dominio usando
 * MapStruct.
 */
@ExtendWith(MockitoExtension.class)
class PriceAdapterTest {

    @Mock
    private PriceRepo priceRepo;

    @Mock
    private PriceEntityMapper mapper;

    private PriceAdapter priceAdapter;

    @BeforeEach
    void setUp() {
        priceAdapter = new PriceAdapter(priceRepo, mapper);
    }

    @Test
    void shouldMapEntityToDomainWhenPriceIsFound() {
        // Given
        LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 14, 10, 0);
        LocalDateTime startDate = LocalDateTime.of(2020, 6, 14, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2020, 12, 31, 23, 59, 59);
        PriceQuery query = new PriceQuery(applicationDate, 35455L, 1L);

        PriceEntity entity = new PriceEntity(
                1L,
                1L,
                35455L,
                1,
                startDate,
                endDate,
                0,
                BigDecimal.valueOf(35.50),
                "EUR");

        Price expectedPrice = new Price(
                1L,
                1L,
                35455L,
                1,
                startDate,
                endDate,
                0,
                BigDecimal.valueOf(35.50),
                "EUR");

        when(priceRepo.findApplicablePrice(1L, 35455L, applicationDate))
                .thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(expectedPrice);

        // When
        Optional<Price> result = priceAdapter.findTopPriceByCriteria(query);

        // Then
        assertThat(result).isPresent();
        Price price = result.get();
        assertThat(price.id()).isEqualTo(1L);
        assertThat(price.brandId()).isEqualTo(1L);
        assertThat(price.productId()).isEqualTo(35455L);
        assertThat(price.priceList()).isEqualTo(1);
        assertThat(price.startDate()).isEqualTo(startDate);
        assertThat(price.endDate()).isEqualTo(endDate);
        assertThat(price.priority()).isZero();
        assertThat(price.amount()).isEqualByComparingTo(BigDecimal.valueOf(35.50));
        assertThat(price.currency()).isEqualTo("EUR");

        verify(priceRepo).findApplicablePrice(1L, 35455L, applicationDate);
        verify(mapper).toDomain(entity);
    }

    @Test
    void shouldReturnEmptyWhenNoPriceIsFound() {
        // Given
        LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 14, 10, 0);
        PriceQuery query = new PriceQuery(applicationDate, 99999L, 1L);

        when(priceRepo.findApplicablePrice(1L, 99999L, applicationDate))
                .thenReturn(Optional.empty());

        // When
        Optional<Price> result = priceAdapter.findTopPriceByCriteria(query);

        // Then
        assertThat(result).isEmpty();
        verify(priceRepo).findApplicablePrice(1L, 99999L, applicationDate);
    }

    @Test
    void shouldMapEntityWithHighPriorityCorrectly() {
        // Given
        LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 14, 16, 0);
        LocalDateTime startDate = LocalDateTime.of(2020, 6, 14, 15, 0);
        LocalDateTime endDate = LocalDateTime.of(2020, 6, 14, 18, 30);
        PriceQuery query = new PriceQuery(applicationDate, 35455L, 1L);

        PriceEntity entity = new PriceEntity(
                2L,
                1L,
                35455L,
                2,
                startDate,
                endDate,
                1,
                BigDecimal.valueOf(25.45),
                "EUR");

        Price expectedPrice = new Price(
                2L,
                1L,
                35455L,
                2,
                startDate,
                endDate,
                1,
                BigDecimal.valueOf(25.45),
                "EUR");

        when(priceRepo.findApplicablePrice(1L, 35455L, applicationDate))
                .thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(expectedPrice);

        // When
        Optional<Price> result = priceAdapter.findTopPriceByCriteria(query);

        // Then
        assertThat(result).isPresent();
        Price price = result.get();
        assertThat(price.id()).isEqualTo(2L);
        assertThat(price.priceList()).isEqualTo(2);
        assertThat(price.priority()).isEqualTo(1);
        assertThat(price.amount()).isEqualByComparingTo(BigDecimal.valueOf(25.45));

        verify(priceRepo).findApplicablePrice(1L, 35455L, applicationDate);
        verify(mapper).toDomain(entity);
    }
}
