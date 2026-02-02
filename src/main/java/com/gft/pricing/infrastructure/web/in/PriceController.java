package com.gft.pricing.infrastructure.web.in;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.gft.pricing.domain.model.Price;
import com.gft.pricing.domain.model.PriceQuery;
import com.gft.pricing.domain.port.in.PriceQueryUseCase;
import com.gft.pricing.infrastructure.web.api.DefaultApi;
import com.gft.pricing.infrastructure.web.mapper.PriceResponseMapper;
import com.gft.pricing.infrastructure.web.model.generated.PriceResponse;

/**
 * Controlador REST para consultas de precios.
 * Implementa la interfaz generada desde el OpenAPI (API First).
 * Expone el endpoint principal para obtener el precio aplicable de un producto
 * en una fecha determinada.
 * Utiliza MapStruct para el mapeo de DTOs.
 * Capa de entrada (adaptador) en la arquitectura hexagonal.
 */
@RestController
public class PriceController implements DefaultApi {

    private final PriceQueryUseCase priceQueryUseCase;
    private final PriceResponseMapper mapper;

    public PriceController(PriceQueryUseCase priceQueryUseCase, PriceResponseMapper mapper) {
        this.priceQueryUseCase = priceQueryUseCase;
        this.mapper = mapper;
    }

    /**
     * Implementación del endpoint definido en OpenAPI.
     * Obtiene el precio aplicable para un producto en una fecha específica.
     * Aplica la regla de prioridad: si múltiples precios coinciden, se devuelve el
     * de mayor prioridad.
     *
     * @param brandId         identificador de la cadena (ej: 1 = ZARA)
     * @param productId       identificador del producto
     * @param applicationDate fecha y hora en formato ISO-8601 (ej:
     *                        2020-06-14T10:00:00)
     * @return precio aplicable con sus fechas de validez
     */
    @Override
    public ResponseEntity<PriceResponse> findPrice(Long brandId, Long productId, String applicationDate) {
        LocalDateTime dateTime = LocalDateTime.parse(applicationDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        Price price = priceQueryUseCase.queryPrices(
                new PriceQuery(dateTime, productId, brandId));

        return ResponseEntity.ok(mapper.toResponse(price));
    }

}
