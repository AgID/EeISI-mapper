package it.infocert.eigor.api;

import it.infocert.eigor.model.core.rules.Rule;

import java.util.List;

/**
 * A {@link CenRuleRepository} gives access to all the validation rules.
 */
public interface CenRuleRepository {
    List<Rule> rules();
}
