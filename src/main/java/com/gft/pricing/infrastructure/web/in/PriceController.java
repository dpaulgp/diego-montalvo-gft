package com.gft.pricing.infrastructure.web.in;

import com.gft.pricing.domain.model.Price;
import com.gft.pricing.domain.model.PriceQuery;
import com.gft.pricing.domain.port.in.PriceQueryUseCase;
import com.gft.pricing.infrastructure.web.api.DefaultApi;
import com.gft.pricing.infrastructure.web.model.generated.PriceResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para consultas de precios.
 * Implementa la interfaz generada desde el OpenAPI (API First).
 * Expone el endpoint principal para obtener el precio aplicable de un producto en una fecha determinada.
 * Capa de entrada (adaptador) en la arquitectura hexagonal.
 */
@RestController
public class PriceController implements DefaultApi {

    private final PriceQueryUseCase priceQueryUseCase;

    public PriceController(PriceQueryUseCase priceQueryUseCase) {
        this.priceQueryUseCase = priceQueryUseCase;
    }

    /**
     * Implementación del endpoint definido en OpenAPI.
     * Obtiene el precio aplicable para un producto en una fecha específica.
     * Aplica la regla de prioridad: si múltiples precios coinciden, se devuelve el de mayor prioridad.
     *
     * @param brandId identificador de la cadena (ej: 1 = ZARA)
     * @param productId identificador del producto
     * @param applicationDate fecha y hora en formato ISO-8601 (ej: 2020-06-14T10:00:00)
     * @return precio aplicable con sus fechas de validez
     */
    @Override
    public ResponseEntity<PriceResponse> findPrice(Long brandId, Long productId, String applicationDate) {
        LocalDateTime dateTime = LocalDateTime.parse(applicationDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        Price price = priceQueryUseCase.queryPrices(
                new PriceQuery(dateTime, productId, brandId));

        return ResponseEntity.ok(toResponse(price));
    }

    /**
     * Convierte el modelo de dominio Price a la respuesta generada desde OpenAPI.
     */
    private PriceResponse toResponse(Price price) {
        PriceResponse response = new PriceResponse();
        response.setProductId(price.productId());
        response.setBrandId(price.brandId());
        response.setPriceList(price.priceList());
        response.setStartDate(price.startDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        response.setEndDate(price.endDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        response.setAmount(price.amount());
        response.setCurrency(price.currency());
        return response;
    }

}
