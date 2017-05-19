package it.infocert.eigor.rules.repositories;

import it.infocert.eigor.api.RuleRepository;
import it.infocert.eigor.model.core.rules.Rule;
import it.infocert.eigor.rules.MalformedRuleException;
import it.infocert.eigor.rules.integrity.IntegrityRule;
import it.infocert.eigor.rules.integrity.IteratingIntegrityRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * {@link RuleRepository} for {@link IntegrityRule} objects.
 * It stores all the active integrity rules
 */
public class IntegrityRulesRepository implements RuleRepository {
    private static final Logger log = LoggerFactory.getLogger(IntegrityRulesRepository.class);
    private List<Rule> ruleList;
    private final Properties properties;
    private final List<Rule> validRules = new ArrayList<>(0);
    private final Map<String, String> invalidRules = new HashMap<>();

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
                    rule = new IteratingIntegrityRule(((String) entry.get("items")), ((String) entry.get("body")), key);
                } else {
                    rule = new IntegrityRule((String) entry.get("body"), key);
                }
                Map<String, Object> validation;
                for (Map.Entry<String, Object>ruleDef : entry.entrySet()) {
                    validation = validateExpression(((String) ruleDef.getValue()));
                    if (((boolean) validation.get("result"))) {
                        validRules.add(rule);
                    } else {
                        invalidRules.put(String.format("%s.%s", key, ruleDef.getKey()), (String) validation.get("expression"));
                    }
                }

                rules.add(rule);
            });
            if (!invalidRules.isEmpty()) {
                throw new MalformedRuleException("There are invalid rules in the configuration.", Collections.unmodifiableMap(invalidRules), validRules);
            }
            this.ruleList = rules;
            return ruleList;
        }
    }

    private Map<String, Object> validateExpression(String expr) {
            HashMap<String, Object> result = new HashMap<>();
        if (expr.matches("^(\\$)\\{((?!\\{|}).)*}$")) {
            result.put("result", true);
            return result;
        } else {
            result.put("result", false);
            result.put("expression", expr);
            return result;
        }
    }

    public List<Rule> getValidRules() {
        return validRules;
    }

    public Map<String, String> getInvalidRules() {
        return Collections.unmodifiableMap(invalidRules);
    }
}
