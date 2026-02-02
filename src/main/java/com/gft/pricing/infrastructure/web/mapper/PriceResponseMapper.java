package com.gft.pricing.infrastructure.web.mapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import com.gft.pricing.domain.model.Price;
import com.gft.pricing.infrastructure.web.model.generated.PriceResponse;

/**
 * Mapper de MapStruct para convertir entre Price (dominio) y PriceResponse
 * (DTO).
 * Se genera automáticamente en tiempo de compilación.
 * Configurado como componente de Spring para inyección de dependencias.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PriceResponseMapper {

    /**
     * Convierte el modelo de dominio a DTO de respuesta.
     * Las fechas se formatean a String en formato ISO-8601.
     *
     * @param price modelo de dominio
     * @return DTO de respuesta
     */
    @Mapping(target = "startDate", expression = "java(formatDate(price.startDate()))")
    @Mapping(target = "endDate", expression = "java(formatDate(price.endDate()))")
    PriceResponse toResponse(Price price);

    /**
     * Formatea una fecha a String en formato ISO-8601.
     *
     * @param dateTime fecha a formatear
     * @return fecha en formato String ISO-8601
     */
    default String formatDate(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null;
    }
}
