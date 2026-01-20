package com.gft.pricing.domain.model;

import java.time.LocalDateTime;
import java.math.BigDecimal;


public record Price (
        Long id,
        long brandId,
        long productId,
        int priceList,
        LocalDateTime startDate,
        LocalDateTime endDate,
        int priority,
        BigDecimal amount,
        String currency
){}
