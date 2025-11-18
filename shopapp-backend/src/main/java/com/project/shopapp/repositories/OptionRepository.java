package com.project.shopapp.repositories;

import com.project.shopapp.models.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OptionRepository extends JpaRepository<Option, Long> {
    Option findByName(String name);
}
