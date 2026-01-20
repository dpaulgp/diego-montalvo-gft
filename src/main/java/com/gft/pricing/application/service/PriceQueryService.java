package com.gft.pricing.application.service;

import com.gft.pricing.domain.exception.PriceNotFoundException;
import com.gft.pricing.domain.model.Price;
import com.gft.pricing.domain.model.PriceQuery;
import com.gft.pricing.domain.port.in.PriceQueryUseCase;
import com.gft.pricing.domain.port.out.PriceRepoPort;
import org.springframework.stereotype.Service;

/**
 * Servicio de aplicación que implementa el caso de uso de consulta de precios.
 * Orquesta la lógica de negocio y coordina entre los puertos de entrada y salida.
 * Capa de aplicación en la arquitectura hexagonal.
 */
@Service
public class PriceQueryService implements PriceQueryUseCase {
    
    private final PriceRepoPort priceRepoPort;

    public PriceQueryService(PriceRepoPort priceRepoPort) {
        this.priceRepoPort = priceRepoPort;
    }

    /**
     * Busca el precio aplicable según los criterios proporcionados.
     * Delega la búsqueda al puerto de salida y aplica la regla de negocio:
     * debe existir un precio válido o se lanza excepción de dominio.
     *
     * @param query criterios de búsqueda (fecha, productId, brandId)
     * @return el precio con mayor prioridad que cumple los criterios
     * @throws PriceNotFoundException si no se encuentra ningún precio aplicable
     */
    @Override
    public Price queryPrices(PriceQuery query) {

        return priceRepoPort.findTopPriceByCriteria(query)
                .orElseThrow(() -> new PriceNotFoundException(
                    "No price found for the given criteria: productId %d , brandId %d , applicationDate %s".formatted(
                        query.productId(),
                        query.brandId(),
                        query.applicationDate()
                )));
    }

}
