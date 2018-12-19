package it.infocert.eigor.rules.repositories;

import it.infocert.eigor.api.RuleRepository;
import it.infocert.eigor.model.core.rules.Rule;
import it.infocert.eigor.rules.MalformedRuleException;
import it.infocert.eigor.rules.cardinality.CardinalityRule;

import java.util.*;

public class CardinalityRulesRepository implements RuleRepository {

    private List<Rule> validRules;
    private final Properties properties;
    private final Map<String, String> invalidRules = new HashMap<>();

    public CardinalityRulesRepository(Properties properties) {
        this.properties = properties;
    }

    @Override
    public List<Rule> rules() {
        if (validRules != null) {
            return validRules;
        } else {
            validRules = new ArrayList<>(0);

            for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                String key = (String) entry.getKey();
                String value = (String) entry.getValue();
                validateProperty(key, value);
            }

            if (invalidRules.isEmpty()) {
                return validRules;
            } else {
                throw new MalformedRuleException("There are invalid rules in the configuration.", invalidRules, validRules);
            }
        }
    }

    private void validateProperty(String name, String card) {
        if (!card.matches("^\\d\\.\\.[\\d+|n]$") || !name.matches("^B[T|G]-\\d+$")) {
            invalidRules.put(name, card);
        } else {
            validRules.add(new CardinalityRule(name, card));
        }
    }
}
