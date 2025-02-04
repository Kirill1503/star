package com.starbank.star.service;

import com.starbank.star.DTO.RecommendationDTO;
import com.starbank.star.entity.RuleQuery;
import com.starbank.star.entity.RuleStats;
import com.starbank.star.entity.Rules;
import com.starbank.star.repository.RecommendationRepository;
import com.starbank.star.rules.RecommendationRuleSet;
import com.starbank.star.rules.RuleStatsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RecommendationService {
    private static final Logger logger = LoggerFactory.getLogger(RecommendationService.class);

    private final List<RecommendationRuleSet> ruleSets;
    private final RulesService rulesService;
    private final RecommendationRepository repository;
    private final RuleStatsRepository ruleStatsRepository;

    public RecommendationService(List<RecommendationRuleSet> ruleSets,
                                 RulesService rulesService,
                                 RecommendationRepository repository,
                                 RuleStatsRepository ruleStatsRepository) {
        this.ruleSets = ruleSets;
        this.rulesService = rulesService;
        this.repository = repository;
        this.ruleStatsRepository = ruleStatsRepository;
    }

    public List<RecommendationDTO> getRecommendations(UUID userId) {
        logger.info("Fetching recommendations for user: {}", userId);

        List<RecommendationDTO> recommendations = ruleSets.stream()
                .map(rule -> rule.apply(userId))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        logger.debug("Found {} static recommendations for user: {}", recommendations.size(), userId);

        // Добавляем динамические правила
        List<Rules> dynamicRules = rulesService.getAllRules();
        for (Rules rule : dynamicRules) {
            if (checkDynamicRule(userId, rule)) {
                recommendations.add(new RecommendationDTO(
                        rule.getProductName(),
                        rule.getProductId(),
                        rule.getProductText()
                ));
                updateRuleStats(rule.getId());
                logger.debug("Added dynamic recommendation: {} for user: {}", rule.getProductName(), userId);
            }
        }

        logger.info("Total recommendations found for user {}: {}", userId, recommendations.size());
        return recommendations;
    }

    private void updateRuleStats(UUID ruleId) {
        RuleStats stats = ruleStatsRepository.findByRuleId(ruleId);
        stats.increment();
        ruleStatsRepository.save(stats);
    }

    private boolean checkDynamicRule(UUID userId, Rules rule) {
        logger.debug("Checking dynamic rule for user: {}", userId);
        for (RuleQuery query : rule.getRuleQueries()) {
            boolean result = evaluateQuery(userId, query);
            if (query.isNegate()) {
                result = !result;
            }
            if (!result) {
                logger.debug("Rule query failed: {}", query.getQueryType());
                return false;
            }
        }
        return true;
    }

    private boolean evaluateQuery(UUID userId, RuleQuery query) {
        switch (query.getQueryType()) {
            case "USER_OF":
                return checkUserOf(userId, query.getArguments().get(0));
            case "ACTIVE_USER_OF":
                return checkActiveUserOf(userId, query.getArguments().get(0));
            case "TRANSACTION_SUM_COMPARE":
                return checkTransactionSumCompare(userId, query.getArguments().get(0), query.getArguments().get(1),
                        query.getArguments().get(2), Integer.parseInt(query.getArguments().get(3)));
            case "TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW":
                return checkTransactionSumCompareDepositWithdraw(userId, query.getArguments().get(0), query.getArguments().get(1));
            default:
                logger.warn("Unknown query type: {}", query.getQueryType());
                return false;
        }
    }

    private boolean checkUserOf(UUID userId, String productType) {
        return repository.getTotalDepositAmountByType(userId.toString(), productType) > 0;
    }

    private boolean checkActiveUserOf(UUID userId, String productType) {
        return repository.getTransactionCountByType(userId.toString(), productType) >= 5;
    }

    private boolean checkTransactionSumCompare(UUID userId, String productType, String transactionType, String operator, int value) {
        double sum = transactionType.equals("DEPOSIT")
                ? repository.getTotalDepositAmountByType(userId.toString(), productType)
                : repository.getTotalSpendingAmountByType(userId.toString(), productType);

        switch (operator) {
            case ">": return sum > value;
            case "<": return sum < value;
            case "=": return sum == value;
            case ">=": return sum >= value;
            case "<=": return sum <= value;
            default: return false;
        }
    }

    private boolean checkTransactionSumCompareDepositWithdraw(UUID userId, String productType, String operator) {
        double depositSum = repository.getTotalDepositAmountByType(userId.toString(), productType);
        double withdrawSum = repository.getTotalSpendingAmountByType(userId.toString(), productType);

        switch (operator) {
            case ">": return depositSum > withdrawSum;
            case "<": return depositSum < withdrawSum;
            case "=": return depositSum == withdrawSum;
            case ">=": return depositSum >= withdrawSum;
            case "<=": return depositSum <= withdrawSum;
            default: return false;
        }
    }
}