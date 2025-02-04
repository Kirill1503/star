package com.starbank.star.entity;

import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "rule_queries")
public class RuleQuery {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "query_type", nullable = false)
    private String queryType;

    @ElementCollection
    @CollectionTable(name = "rule_query_arguments", joinColumns = @JoinColumn(name = "rule_query_id"))
    @Column(name = "argument", nullable = false)
    private List<String> arguments;

    @Column(name = "negate", nullable = false)
    private boolean negate;

    // Конструктор по умолчанию
    public RuleQuery() {}

    // Конструктор с параметрами
    public RuleQuery(String queryType, List<String> arguments, boolean negate) {
        this.queryType = queryType;
        this.arguments = arguments;
        this.negate = negate;
    }

    // Геттеры и сеттеры
    public String getQueryType() {
        return queryType;
    }

    public void setQueryType(String queryType) {
        this.queryType = queryType;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public void setArguments(List<String> arguments) {
        this.arguments = arguments;
    }

    public boolean isNegate() {
        return negate;
    }

    public void setNegate(boolean negate) {
        this.negate = negate;
    }
}