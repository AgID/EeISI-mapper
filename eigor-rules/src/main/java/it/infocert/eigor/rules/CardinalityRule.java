package it.infocert.eigor.rules;

import it.infocert.eigor.converter.fattpa2cen.mapping.GenericOneToOneTransformation;
import it.infocert.eigor.model.core.InvoiceUtils;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BTBG;
import it.infocert.eigor.model.core.rules.Rule;
import it.infocert.eigor.model.core.rules.RuleOutcome;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;

public class CardinalityRule implements Rule {
    
    private static Logger LOGGER = LoggerFactory.getLogger(CardinalityRule.class);

    private String pathToBT;
    private Integer minimum;
    private Integer maximum;

    public CardinalityRule(String pathToBT, Integer minimum, Integer maximum) {
        this.pathToBT = pathToBT;
        this.minimum = minimum;
        this.maximum = maximum;
    }

    @Override
    public RuleOutcome isCompliant(BG0000Invoice coreInvoice) {
        InvoiceUtils invoiceUtils = new InvoiceUtils(new Reflections("it.infocert"));
        String bgPath = pathToBT.substring(0, pathToBT.lastIndexOf("/"));

        if ("".equals(bgPath)) {
            bgPath = "BG0000";
        }

        BTBG parent = invoiceUtils.getChild(bgPath, coreInvoice);
        String btName = pathToBT.substring(pathToBT.lastIndexOf("/") + 1);
        try {
            List<BTBG> childrenAsList = invoiceUtils.getChildrenAsList(parent, btName);
            if (maximum != null) {
                if (childrenAsList.size() >= minimum && childrenAsList.size() <= maximum) {
                    return success(btName, childrenAsList.size());
                } else {
                    return failure(btName, childrenAsList.size());
                }
            } else {
                if (childrenAsList.size() >= minimum) {
                    return success(btName, childrenAsList.size());
                } else {
                    return failure(btName, childrenAsList.size());
                }
            }

        } catch (IllegalAccessException | InvocationTargetException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    private RuleOutcome success(String expected, int result) {
        if (maximum != null) {
            if (Objects.equals(maximum, minimum)) {
                return RuleOutcome.newSuccessOutcome("An invoice shall have exactly %d %s, it has: %s.",
                        maximum,
                        expected,
                        result);
            } else {
                return RuleOutcome.newSuccessOutcome("An invoice shall have between %d and %d %s, it has: %s.",
                        minimum,
                        maximum,
                        expected,
                        result);
            }
        } else {
            return RuleOutcome.newSuccessOutcome("An invoice shall have at least %d %s, it has: %s.",
                    minimum,
                    expected,
                    result);
        }
    }

    private RuleOutcome failure(String expected, int result) {
        if (maximum != null) {
            if (Objects.equals(maximum, minimum)) {
                return RuleOutcome.newFailedOutcome("An invoice shall have exactly %d %s, it has: %s.",
                        maximum,
                        expected,
                        result);
            } else {
                return RuleOutcome.newFailedOutcome("An invoice shall have between %d and %d %s, it has: %s.",
                        minimum,
                        maximum,
                        expected,
                        result);
            }
        } else {
            return RuleOutcome.newFailedOutcome("An invoice shall have at least %d %s, it has: %s.",
                    minimum,
                    expected,
                    result);
        }
    }
}
