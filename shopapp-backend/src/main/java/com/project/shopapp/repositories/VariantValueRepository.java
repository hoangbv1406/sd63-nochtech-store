package com.project.shopapp.repositories;

import com.project.shopapp.models.VariantValue;
import com.project.shopapp.models.VariantValueId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VariantValueRepository extends JpaRepository<VariantValue, VariantValueId> {
}
