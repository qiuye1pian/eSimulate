package org.esimulate.core.repository;

import lombok.NonNull;
import org.esimulate.core.model.environment.gas.GasScheme;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GasSchemeRepository extends JpaRepository<GasScheme, Long> {

    Page<GasScheme> findBySchemeNameContaining(String schemeName, Pageable pageable);

    Optional<GasScheme> findBySchemeName(@NonNull String schemeName);

}
