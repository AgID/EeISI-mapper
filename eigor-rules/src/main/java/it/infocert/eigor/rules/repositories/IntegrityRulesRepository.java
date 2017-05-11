package it.infocert.eigor.rules.repositories;

import it.infocert.eigor.api.RuleRepository;
import it.infocert.eigor.model.core.rules.Rule;
import it.infocert.eigor.rules.MalformedRuleException;
import it.infocert.eigor.rules.integrity.IntegrityRule;
import it.infocert.eigor.rules.integrity.IteratingIntegrityRule;

import java.util.*;

/**
 * {@link RuleRepository} for {@link IntegrityRule} objects.
 * It stores all the active integrity rules
 */
public class IntegrityRulesRepository implements RuleRepository {
    private List<Rule> ruleList;
    private final Properties properties;

    public IntegrityRulesRepository(Properties properties) {
        this.properties = properties;
    }

    /**
     * Lazily loads and returns all the rules expressed in "rules.properties".
     * @return a {@link List} of {@link Rule} containing all the {@link IntegrityRule} configured
     * @throws MalformedRuleException if the expression is not a valid rule definition
     */
    @Override
    public List<Rule> rules() {
        if (ruleList != null) {
            return ruleList;
        } else {
            Map<String, Map<String, Object>> collected = new HashMap<>();
            for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                String key = (String) entry.getKey();
                if (!key.matches("\\w+\\.\\w+")) {
                    throw new MalformedRuleException(String.format("Rule name %s is not valid", key));
                }
                String[] split = key.split("\\.");
                String superKey = split[0];
                if (collected.containsKey(superKey)) {
                    Map<String, Object> objects = collected.get(superKey);
                    objects.put(split[1], entry.getValue());
                } else {
                    Map<String, Object> values = new HashMap<>();
                    values.put(split[1], entry.getValue());
                    collected.put(superKey, values);
                }
            }
            List<Rule> rules = new ArrayList<>();
            collected.forEach((key, entry) -> {
                Rule rule;
                if (entry.containsKey("items")) {
                    rule = new IteratingIntegrityRule(((String) entry.get("items")), ((String) entry.get("body")));
                } else {
                    rule = new IntegrityRule((String) entry.get("body"));
                }
                rules.add(rule);
            });
            this.ruleList = rules;
            return ruleList;
        }
    }
}
