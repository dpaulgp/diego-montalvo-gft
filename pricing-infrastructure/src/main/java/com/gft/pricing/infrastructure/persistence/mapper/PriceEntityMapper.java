package com.gft.pricing.infrastructure.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import com.gft.pricing.domain.model.Price;
import com.gft.pricing.infrastructure.persistence.entity.PriceEntity;

/**
 * Mapper de MapStruct para convertir entre PriceEntity (JPA) y Price (dominio).
 * Se genera automáticamente en tiempo de compilación.
 * Configurado como componente de Spring para inyección de dependencias.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PriceEntityMapper {

    /**
     * Convierte una entidad JPA a modelo de dominio.
     * Mapea 'price' de la entidad a 'amount' en el dominio.
     *
     * @param entity entidad JPA
     * @return modelo de dominio
     */
    @Mapping(source = "price", target = "amount")
    Price toDomain(PriceEntity entity);

    /**
     * Convierte un modelo de dominio a entidad JPA.
     * Mapea 'amount' del dominio a 'price' en la entidad.
     *
     * @param price modelo de dominio
     * @return entidad JPA
     */
    @Mapping(source = "amount", target = "price")
    PriceEntity toEntity(Price price);
}
