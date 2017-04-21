package it.infocert.eigor.rules.constraints;

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

        private static Logger log = LoggerFactory.getLogger(CardinalityRule.class);

        private String pathToBT;
        private Integer minimum;
        private Integer maximum;
        private Reflections reflections;
        
        public CardinalityRule(String pathToBT, Integer minimum, Integer maximum, Reflections reflections) {
            this.pathToBT = pathToBT;
            this.minimum = minimum;
            this.maximum = maximum;
            this.reflections = reflections;
        }

        @Override
        public RuleOutcome isCompliant(BG0000Invoice invoice) {
            InvoiceUtils invoiceUtils = new InvoiceUtils(reflections);
            String btName = pathToBT.substring(pathToBT.lastIndexOf("/") + 1);

            BTBG child = invoiceUtils.getFirstChild(pathToBT, invoice);
            if (child == null && minimum != 0) {
                return failure(btName, 0);
            } else {

                BTBG parent = child.getParent();
                if (parent != null && !(parent instanceof BG0000Invoice)) {
                    BTBG parentParent = parent.getParent();

                    try {

                        RuleOutcome result = null;
                        List<BTBG> parentList = invoiceUtils.getChildrenAsList(parentParent, parent.denomination());
                        for (BTBG foundParent : parentList) {
                            List<BTBG> childList = invoiceUtils.getChildrenAsList(foundParent, btName);
                            result = getRuleOutcome(btName, childList);
                        }
                        return result;
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        log.error(e.getMessage(), e);
                    }
                } else {
                    try {
                        List<BTBG> childList = invoiceUtils.getChildrenAsList(parent, child.denomination());
                        return getRuleOutcome(btName, childList);
                    } catch (InvocationTargetException | IllegalAccessException e) {
                        log.error(e.getMessage(), e);
                    }
                }
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

        private RuleOutcome getRuleOutcome(String btName, List<BTBG> childList) {
            RuleOutcome result;
            if (maximum != null) {
                if (childList.size() >= minimum && childList.size() <= maximum) {
                    result = success(btName, childList.size());
                } else {
                    result = failure(btName, childList.size());
                }
            } else {
                if (childList.size() >= minimum) {
                    result = success(btName, childList.size());
                } else {
                    result = failure(btName, childList.size());
                }
            }
            return result;
        }
    }