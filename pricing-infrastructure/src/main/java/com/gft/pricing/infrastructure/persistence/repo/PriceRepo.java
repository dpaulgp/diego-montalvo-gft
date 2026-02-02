package com.gft.pricing.infrastructure.persistence.repo;

import com.gft.pricing.infrastructure.persistence.entity.PriceEntity;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PriceRepo extends JpaRepository<PriceEntity, Long> {

    @Query("SELECT p FROM Price p WHERE p.brandId = :brandId AND p.productId = :productId " +
           "AND :date BETWEEN p.startDate AND p.endDate " +
           "ORDER BY p.priority DESC, p.startDate DESC LIMIT 1")
    Optional<PriceEntity> findApplicablePrice(@Param("brandId") Long brandId,
                                              @Param("productId") Long productId,
                                              @Param("date") LocalDateTime date);
}
