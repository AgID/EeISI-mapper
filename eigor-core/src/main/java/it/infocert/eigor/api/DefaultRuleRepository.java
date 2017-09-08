package it.infocert.eigor.api;

import it.infocert.eigor.rules.repositories.CardinalityRulesRepository;
import it.infocert.eigor.rules.repositories.CompositeRuleRepository;
import it.infocert.eigor.rules.repositories.IntegrityRulesRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * This is the {@link RuleRepository repository of CEN rules} that should be used by default,
 * it loads its config from the default location.
 * If, for any reason, a customized rule repository is needed, there are plenty of implementations to choose from.
 */
public class DefaultRuleRepository extends CompositeRuleRepository {

    private DefaultRuleRepository(RuleRepository... repositories) {
        super(repositories);
    }

    public static final DefaultRuleRepository newInstance() {
        try {
            return new DefaultRuleRepository(
                    new CardinalityRulesRepository(loadPropertyFromResourcePath("/cardinality.properties")),
                    new IntegrityRulesRepository(loadPropertyFromResourcePath("/rules.properties"))
            );
        } catch (IOException e) {
            throw new IllegalStateException("It was not possible to load the default rules.", e);
        }
    }

    private static Properties loadPropertyFromResourcePath(String resourcePath) throws IOException {
        Properties cardinalityRules = new Properties();
        InputStream resourceAsStream = DefaultRuleRepository.class.getResourceAsStream(resourcePath);
        cardinalityRules.load(resourceAsStream);
        return cardinalityRules;
    }

}
