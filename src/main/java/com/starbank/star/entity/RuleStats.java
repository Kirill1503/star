package com.starbank.star.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.UUID;

@Entity
public class RuleStats {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID ruleId;
    private int count;

    public RuleStats(UUID ruleId, int count) {
        this.ruleId = ruleId;
        this.count = count;
    }

    public RuleStats() {

    }

    public UUID getRuleId() {
        return ruleId;
    }

    public void setRuleId(UUID ruleId) {
        this.ruleId = ruleId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void increment() {
        this.count++;
    }
}
