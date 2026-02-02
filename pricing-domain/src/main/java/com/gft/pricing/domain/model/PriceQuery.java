package com.gft.pricing.domain.model;

import java.time.LocalDateTime;

public record PriceQuery(
    LocalDateTime applicationDate,
    long productId,
    long brandId
) {}