package org.esimulate.core.repository;

import lombok.NonNull;
import org.esimulate.core.model.environment.sunlight.SunlightIrradianceScheme;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SunlightIrradianceSchemeRepository extends JpaRepository<SunlightIrradianceScheme, Long> {

    Page<SunlightIrradianceScheme> findBySchemeNameContaining(String schemeName, Pageable pageable);


    Optional<SunlightIrradianceScheme> findBySchemeName(@NonNull String schemeName);

}
