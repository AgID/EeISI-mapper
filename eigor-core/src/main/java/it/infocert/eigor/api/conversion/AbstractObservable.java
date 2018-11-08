package it.infocert.eigor.api.conversion;

import it.infocert.eigor.api.RuleRepository;
import it.infocert.eigor.api.impl.InMemoryRuleReport;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import it.infocert.eigor.model.core.rules.Rule;
import it.infocert.eigor.model.core.rules.RuleOutcome;
import it.infocert.eigor.rules.MalformedRuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public abstract class AbstractObservable {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    protected final List<ConversionCallback> listeners;
    protected final RuleRepository ruleRepository;

    protected AbstractObservable(List<ConversionCallback> listeners, RuleRepository ruleRepository) {
        this.listeners = listeners;
        this.ruleRepository = ruleRepository;
    }

    protected void fireOnStartingConverionEvent(ConversionContext ctx) {
        for (ConversionCallback listener : listeners) {
            try {
                listener.onStartingConversion(ctx);
            } catch (Exception e) {
                log.warn("An error occurred while notifying listener '" + listener + "'. The error is logged but ignored.", e);
            }
        }
    }

    protected void fireOnStartingToCenTranformationEvent(ConversionContext ctx) {
        for (ConversionCallback listener : listeners) {
            try {
                listener.onStartingToCenTranformation(ctx);
            } catch (Exception e) {
                log.warn("An error occurred while notifying listener '" + listener + "'. The error is logged but ignored.", e);
            }
        }
    }

    protected void fireOnSuccessfullToCenTranformationEvent(ConversionContext ctx) {
        for (ConversionCallback listener : listeners) {
            try {
                listener.onSuccessfullToCenTranformation(ctx);
            } catch (Exception e) {
                log.warn("An error occurred while notifying listener '" + listener + "'. The error is logged but ignored.", e);
            }
        }
    }

    protected void fireOnFailedToCenConversion(ConversionContext ctx) {
        for (ConversionCallback listener : listeners) {
            try {
                listener.onFailedToCenConversion(ctx);
            } catch (Exception e) {
                log.warn("An error occurred while notifying listener '" + listener + "'. The error is logged but ignored.", e);
            }
        }
    }

    protected void fireOnStartingVerifyingCenRules(ConversionContext ctx) {
        for (ConversionCallback listener : listeners) {
            try {
                listener.onStartingVerifyingCenRules(ctx);
            } catch (Exception e) {
                log.warn("An error occurred while notifying listener '" + listener + "'. The error is logged but ignored.", e);
            }
        }
    }

    protected void fireOnSuccessfullyVerifiedCenRules(ConversionContext ctx) {
        for (ConversionCallback listener : listeners) {
            try {
                listener.onSuccessfullyVerifiedCenRules(ctx);
            } catch (Exception e) {
                log.warn("An error occurred while notifying listener '" + listener + "'. The error is logged but ignored.", e);
            }
        }
    }

    protected void fireOnFailedVerifyingCenRules(ConversionContext ctx) {
        for (ConversionCallback listener : listeners) {
            try {
                listener.onFailedVerifingCenRules(ctx);
            } catch (Exception e) {
                log.warn("An error occurred while notifying listener '" + listener + "'. The error is logged but ignored.", e);
            }
        }
    }

    protected void fireOnStartingFromCenTransformation(ConversionContext ctx) {
        for (ConversionCallback listener : listeners) {
            try {
                listener.onStartingFromCenTransformation(ctx);
            } catch (Exception e) {
                log.warn("An error occurred while notifying listener '" + listener + "'. The error is logged but ignored.", e);
            }
        }
    }

    protected void fireOnSuccessfullFromCenTransformation(ConversionContext ctx) {
        for (ConversionCallback listener : listeners) {
            try {
                listener.onSuccessfullFromCenTransformation(ctx);
            } catch (Exception e) {
                log.warn("An error occurred while notifying listener '" + listener + "'. The error is logged but ignored.", e);
            }
        }
    }

    protected void fireOnFailedFromCenTransformation(ConversionContext ctx) {
        for (ConversionCallback listener : listeners) {
            try {
                listener.onFailedFromCenTransformation(ctx);
            } catch (Exception e) {
                log.warn("An error occurred while notifying listener '" + listener + "'. The error is logged but ignored.", e);
            }
        }
    }

    protected void fireOnUnexpectedException(Exception theE, ConversionContext ctx) {
        for (ConversionCallback listener : listeners) {
            try {
                listener.onUnexpectedException(theE, ctx);
            } catch (Exception e) {
                log.warn("An error occurred while notifying listener '" + listener + "'. The error is logged but ignored.", e);
            }
        }
    }

    protected void fireOnTerminatedConversion(ConversionContext ctx) {
        for (ConversionCallback listener : listeners) {
            try {
                listener.onTerminatedConversion(ctx);
            } catch (Exception e) {
                log.warn("An error occurred while notifying listener '" + listener + "'. The error is logged but ignored.", e);
            }
        }
    }

    protected void applyRulesToCenObject(BG0000Invoice cenInvoice, InMemoryRuleReport ruleReport) {
        List<Rule> rules;
        try {
            rules = ruleRepository.rules();
        } catch (MalformedRuleException e) {
            Map<String, String> invalidRules = e.getInvalidRules();

            for (Map.Entry<String, String> entry : invalidRules.entrySet()) {
                log.error(
                        String.format("Rule %s is malformed: %s. Rule expression should follow the pattern ${ expression } without any surrounding quotes,", entry.getKey(), entry.getValue())
                );
            }

            rules = e.getValidRules();
        }
        if (rules != null) {
            for (Rule rule : rules) {
                RuleOutcome ruleOutcome = rule.isCompliant(cenInvoice);
                ruleReport.store(ruleOutcome, rule);
            }

        }
    }

}
