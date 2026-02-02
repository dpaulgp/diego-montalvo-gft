package com.gft.pricing.infrastructure.persistence.adapter;

import java.util.Optional;

import com.gft.pricing.domain.model.Price;
import com.gft.pricing.domain.model.PriceQuery;
import com.gft.pricing.domain.port.out.PriceRepoPort;
import com.gft.pricing.infrastructure.persistence.mapper.PriceEntityMapper;
import com.gft.pricing.infrastructure.persistence.repo.PriceRepo;
import org.springframework.stereotype.Component;

/**
 * Adaptador de persistencia que implementa el puerto de salida para acceso a
 * datos.
 * Utiliza MapStruct para convertir entre el modelo de dominio (Price) y las
 * entidades JPA (PriceEntity).
 * Capa de infraestructura en la arquitectura hexagonal.
 */
@Component
public class PriceAdapter implements PriceRepoPort {

    private final PriceRepo priceRepo;
    private final PriceEntityMapper mapper;

    public PriceAdapter(PriceRepo priceRepo, PriceEntityMapper mapper) {
        this.priceRepo = priceRepo;
        this.mapper = mapper;
    }

    /**
     * Busca el precio de mayor prioridad que cumple los criterios de la consulta.
     * Realiza la consulta a base de datos y mapea el resultado al modelo de dominio
     * usando MapStruct.
     *
     * @param query criterios de búsqueda (fecha de aplicación, productId, brandId)
     * @return Optional con el precio encontrado, o vacío si no existe
     */
    @Override
    public Optional<Price> findTopPriceByCriteria(PriceQuery query) {
        return priceRepo
                .findApplicablePrice(
                        query.brandId(),
                        query.productId(),
                        query.applicationDate())
                .map(mapper::toDomain);
    }

}
