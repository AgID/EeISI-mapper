package it.infocert.eigor.rules.integrity;

import de.odysseus.el.ExpressionFactoryImpl;
import de.odysseus.el.util.SimpleContext;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BTBG;
import it.infocert.eigor.model.core.rules.Rule;
import it.infocert.eigor.model.core.rules.RuleOutcome;
import it.infocert.eigor.rules.MalformedRuleException;

import javax.el.ELException;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import java.util.Iterator;
import java.util.Objects;
import java.util.Properties;

public class IteratingIntegrityRule implements Rule{

    private final String iterating;
    private final ExpressionFactory expressionFactory;
    private final SimpleContext juelContext;
    private final String expression;
    private final String ruleName;

    private static Properties initProps() {
        Properties properties = new Properties();
        properties.setProperty("javax.el.nullProperties", "true");
        return properties;
    }

    public IteratingIntegrityRule(String iterating, String expression, String ruleName) throws MalformedRuleException{
        this.expression = expression;
        this.iterating = iterating;
        this.expressionFactory = new ExpressionFactoryImpl(initProps());
        this.juelContext = new SimpleContext();
        this.ruleName = ruleName;
    }

    @Override
    public RuleOutcome isCompliant(BG0000Invoice invoice) {
        juelContext.setVariable("invoice", expressionFactory.createValueExpression(invoice, BG0000Invoice.class));
        Object object = expressionFactory.createValueExpression(juelContext, iterating, Object.class).getValue(juelContext);
        if (object != null && object instanceof Iterator) {
            Iterator iterator = (Iterator) object;
            RuleOutcome outcome = null;
            while (iterator.hasNext()) {
                juelContext.setVariable("item", expressionFactory.createValueExpression(iterator.next(), BTBG.class));
                ValueExpression valueExpression = expressionFactory.createValueExpression(juelContext, expression, Object.class);
                Boolean condition;
                try {
                    Object tmp = valueExpression.getValue(juelContext);
                    if (Objects.isNull(tmp)) {
                        outcome = RuleOutcome.newUnapplicableOutcome("Rule %s is unapplicable", ruleName);
                        continue;
                    } else {
                        condition = (Boolean) tmp;
                    }
                } catch (IllegalArgumentException | ClassCastException | ELException e) {
                    outcome = RuleOutcome.newErrorOutcome("Error in the rule %s: %s", ruleName, e.getMessage());
                    continue;
                }

                if (condition) {
                    outcome = RuleOutcome.newSuccessOutcome("Rule %s successfully validated", ruleName);
                } else {
                    return RuleOutcome.newFailedOutcome("Rule %s has failed", ruleName);
                }
            }
            if (outcome != null) {
                return outcome;
            } else {
                return RuleOutcome.newUnapplicableOutcome("Rule %s is unapplicable, the Iterator is empty", ruleName);
            }
        } else {
            return RuleOutcome.newErrorOutcome("Error in the items expression for rule %s. The expression %s does not return a valid Iterator", ruleName, iterating);
        }
    }

    private void validateExpression(String expr) {
        if (!expr.matches("^(\\$)\\{((?!\\{|}).)*\\}$")) {
            throw new MalformedRuleException(String.format("Rule %s is malformed: %s. Rule expression should follow the pattern ${ expression } without any surrounding quotes,", ruleName, expr));
        }
    }
}
