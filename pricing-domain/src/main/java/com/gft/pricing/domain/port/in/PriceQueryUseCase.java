package com.gft.pricing.domain.port.in;

import com.gft.pricing.domain.model.Price;
import com.gft.pricing.domain.model.PriceQuery;

public interface PriceQueryUseCase {

    Price queryPrices(PriceQuery query);
    
}
