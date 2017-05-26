package it.infocert.eigor.rules.repositories;

import it.infocert.eigor.api.RuleRepository;
import it.infocert.eigor.model.core.rules.Rule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CompositeRuleRepository implements RuleRepository {

    private final List<RuleRepository> components = new ArrayList<>(0);
    private List<Rule> rules;

    public CompositeRuleRepository(RuleRepository... repositories) {
        components.addAll(Arrays.asList(repositories));
    }

    @Override
    public List<Rule> rules() {
        if (rules != null) {
            return rules;
        } else {
            rules = new ArrayList<>(1);
            for (RuleRepository repo : components) {
                rules.addAll(repo.rules());
            }
            return rules;
        }
    }
}
