package com.gft.pricing.domain.port.out;

import com.gft.pricing.domain.model.Price;
import com.gft.pricing.domain.model.PriceQuery;

import java.util.Optional;

public interface PriceRepoPort {
    Optional<Price> findTopPriceByCriteria(PriceQuery query);
}
