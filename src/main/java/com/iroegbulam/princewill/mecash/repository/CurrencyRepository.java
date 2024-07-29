package com.iroegbulam.princewill.mecash.repository;

import com.iroegbulam.princewill.mecash.domain.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency,Long> {
    Optional<Currency> findByCode(String code);
}
