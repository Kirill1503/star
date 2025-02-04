package com.starbank.star;

import com.starbank.star.entity.Rules;
import com.starbank.star.rules.rulesRepository.RulesRepository;
import com.starbank.star.service.RulesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RulesServiceTest {

    @Mock
    private RulesRepository rulesRepository;

    @InjectMocks
    private RulesService rulesService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddRule() {
        Rules rule = new Rules();
        rule.setProductName("Test Rule");
        when(rulesRepository.save(rule)).thenReturn(rule);

        Rules savedRule = rulesService.addRule(rule);

        assertNotNull(savedRule);
        assertEquals("Test Rule", savedRule.getProductName());
    }

    @Test
    void testGetAllRules() {
        Rules rule = new Rules();
        rule.setProductName("Test Rule");
        when(rulesRepository.findAll()).thenReturn(List.of(rule));

        List<Rules> rules = rulesService.getAllRules();

        assertEquals(1, rules.size());
        assertEquals("Test Rule", rules.get(0).getProductName());
    }

    @Test
    void testDeleteRule() {
        UUID ruleId = UUID.randomUUID();
        doNothing().when(rulesRepository).deleteById(ruleId);

        rulesService.deleteRule(ruleId);

        verify(rulesRepository, times(1)).deleteById(ruleId);
    }
}