package com.starbank.star.rules;

import com.starbank.star.entity.RuleStats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RuleStatsRepository extends JpaRepository<RuleStats, String> {

    RuleStats findByRuleId(UUID ruleId);
}
