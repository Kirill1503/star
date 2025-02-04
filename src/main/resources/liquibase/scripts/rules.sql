-- liquibase formatted sql

-- changeset kkatyshev:1

CREATE TABLE rules
(
    rule_id      UUID         NOT NULL PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    product_id   UUID         NOT NULL,
    product_text TEXT
);

CREATE INDEX rules_product_id_index ON rules (product_id);

CREATE TABLE rule_queries
(
    rule_id    UUID         NOT NULL PRIMARY KEY,
    query_type VARCHAR(255) NOT NULL,
    arguments  JSON         NOT NULL,
    negate     BOOLEAN      NOT NULL,
    FOREIGN KEY (rule_id) REFERENCES rules (id) ON DELETE CASCADE
);

CREATE INDEX rule_queries_query_type_index ON rule_queries (query_type);

-- changeset kkatyshev:2

CREATE TABLE rule_stats
(
    rule_id UUID PRIMARY KEY,
    count   INT NOT NULL DEFAULT 0,
    FOREIGN KEY (rule_id) REFERENCES rules (id) ON DELETE CASCADE
);

CREATE INDEX rule_stats_rule_id_index ON rule_stats (rule_id);