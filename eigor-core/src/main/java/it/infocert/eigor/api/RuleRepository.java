package it.infocert.eigor.api;

import it.infocert.eigor.model.core.rules.Rule;

import java.util.List;

/**
 * A {@link RuleRepository} gives access to all the rules that can be used to validate a CEN invoice.
 */
public interface RuleRepository {
    List<Rule> rules();
}
