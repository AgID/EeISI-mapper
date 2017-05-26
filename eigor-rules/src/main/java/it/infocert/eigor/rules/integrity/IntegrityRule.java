package it.infocert.eigor.rules.integrity;

import de.odysseus.el.ExpressionFactoryImpl;
import de.odysseus.el.util.SimpleContext;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.rules.Rule;
import it.infocert.eigor.model.core.rules.RuleOutcome;

import javax.el.ELException;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import java.util.Objects;
import java.util.Properties;

/**
 * A {@link Rule} that checks the integrity of the CEN-format converted invoice.
 * The actual body of the rule is stored as a JUEL expression in the "rules.properties" file.
 */
public class IntegrityRule implements Rule {

    private final String expression;
    private final ExpressionFactory expressionFactory;
    private final SimpleContext juelContext;
    private final String ruleName;

    private static Properties initProps() {
        Properties properties = new Properties();
        properties.setProperty("javax.el.nullProperties", "true");
        return properties;
    }

    public IntegrityRule(String expression, String ruleName) {
        this.expressionFactory = new ExpressionFactoryImpl(initProps());
        this.expression = expression;
        this.juelContext = new SimpleContext();
        this.ruleName = ruleName;
    }

    /**
     * Validate a {@link BG0000Invoice} under the conditions defined in the {@link IntegrityRule#expression}
     * @param invoice The CEN-Core invoice to validate
     * @return A {@link RuleOutcome} that describes the result of the validation
     */
    @Override
    public RuleOutcome isCompliant(BG0000Invoice invoice) {
        juelContext.setVariable("invoice", expressionFactory.createValueExpression(invoice, BG0000Invoice.class));
        ValueExpression valueExpression = expressionFactory.createValueExpression(juelContext, expression, Object.class);
        Boolean condition;
        try {
            Object tmp =  valueExpression.getValue(juelContext);
            if (Objects.isNull(tmp)) {
                return RuleOutcome.newUnapplicableOutcome("Rule %s is unapplicable", ruleName);
            } else {
                condition = (Boolean) tmp;
            }
        } catch (IllegalArgumentException | ClassCastException | ELException e) {
            return RuleOutcome.newErrorOutcome("Error in the rule %s: %s", ruleName, e.getMessage());
        }

        if (condition) {
            return RuleOutcome.newSuccessOutcome("Rule %s successfully validated", ruleName);
        } else {
            return RuleOutcome.newFailedOutcome("Rule %s has failed", ruleName);
        }
    }
}
