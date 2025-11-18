package com.project.shopapp.repositories;

import com.project.shopapp.models.OptionValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OptionValueRepository extends JpaRepository<OptionValue, Long> {
    OptionValue findByOptionIdAndValue(Long optionId, String value);
}
