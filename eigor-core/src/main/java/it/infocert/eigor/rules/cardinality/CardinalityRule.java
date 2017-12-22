package it.infocert.eigor.rules.cardinality;

import it.infocert.eigor.api.utils.ReflectionsReflections;
import it.infocert.eigor.model.core.InvoiceUtils;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.model.BTBG;
import it.infocert.eigor.model.core.model.structure.BtBgName;
import it.infocert.eigor.model.core.model.structure.CenStructure;
import it.infocert.eigor.model.core.rules.Rule;
import it.infocert.eigor.model.core.rules.RuleOutcome;
import it.infocert.eigor.rules.MalformedRuleException;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class CardinalityRule extends Rule {

    private final static Logger log = LoggerFactory.getLogger(CardinalityRule.class);

    private final String name;
    private final Integer min;
    private final Integer max;
    private final InvoiceUtils invoiceUtils = new InvoiceUtils(new ReflectionsReflections("it.infocert"));

    public CardinalityRule(String name, String cardinality) {
        this.name = formatName(name);
        String[] slices = cardinality.split("\\.\\.");
        try {
            min = Integer.valueOf(slices[0]);
        } catch (NumberFormatException e) {
            throw new MalformedRuleException(String.format("ERROR in rule %s, %s is not a valid number for minimum cardinality.", name, slices[0]));
        }
        if (!"n".equals(slices[1])) {
            max = Integer.valueOf(slices[1]);
        } else {
            max = null;
        }
    }

    @Override
    public RuleOutcome isCompliant(BG0000Invoice invoice) {
        CenStructure structure = new CenStructure();
        CenStructure.BtBgNode byName = structure.findByName(name);

        List<CenStructure.BtBgNode> test = new ArrayList<>(0);
        while (byName.getNumber() != 0) {
            test.add(0, byName);
            byName = byName.getParent();
        }

        List<BTBG> childrenAsList = null;
        final List<RuleOutcome> outcomes = new ArrayList<>(1);
        for (CenStructure.BtBgNode node : test) {
            try {
                if (node.getParent().getNumber() == 0) {
                    childrenAsList = invoiceUtils.getChildrenAsList(invoice, formatName(node.getName()));
                } else {
                    if (childrenAsList.isEmpty()) {
                        return RuleOutcome.newUnapplicableOutcome("%s - Can't verify the cardinality because " +
                                "one of its parent elements is missing. Last parent checked: %s.", name, node.getParent());
                    }
                    childrenAsList = invoiceUtils.getChildrenAsList(childrenAsList.get(0), formatName(node.getName()));
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }

            if (childrenAsList.isEmpty()) {
                computeCardinality(0, outcomes);
            } else {
                for (BTBG btbg : childrenAsList) {
                    assertExistance(btbg, outcomes);
                }
            }
        }

        if (outcomes.isEmpty()) {
            return RuleOutcome.newErrorOutcome("%s - Error in Cardinality Rule.", name);
        } else {
            return outcomes.get(0);
        }
    }

    private void assertExistance(final BTBG btbg, final List<RuleOutcome> outcomes) {
            try {
                List<BTBG> childrenAsList = invoiceUtils.getChildrenAsList(btbg.getParent(), btbg.denomination());

                computeCardinality(childrenAsList.size(), outcomes);

            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
    }

    private String formatName(BtBgName name) { //TODO move this to BtBgName class
        return formatName(name.toString());
    }

    private String formatName(String name) { //TODO move this to BtBgName class
        String[] split = name.split("-");
        return split[0].toUpperCase() + String.format("%04d", Integer.parseInt(split[1]));
    }

    private void computeCardinality(final int number, final List<RuleOutcome> outcomes) {
        if (max != null) {
            if (number >= min && number <= max) {
                outcomes.add(RuleOutcome.newSuccessOutcome("%s - Cardinality %s verified.", name, String.format("%d..%d", min, max)));
            } else {
                outcomes.add(RuleOutcome.newFailedOutcome("%s - Cardinality %s not verified. Found %d times.", name, String.format("%d..%d", min, max), number));
            }
        } else {
            if (number >= min) {
                outcomes.add(RuleOutcome.newSuccessOutcome("%s - Cardinality %s..n verified.", name, min));
            } else {
                outcomes.add(RuleOutcome.newFailedOutcome("%s - Cardinality %s..n verified. Found %d times.", name, min, number));
            }
        }
    }
}
