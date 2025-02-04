package com.starbank.star.rules.rulesRepository;

import com.starbank.star.entity.Rules;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RulesRepository extends JpaRepository<Rules, UUID> {
}