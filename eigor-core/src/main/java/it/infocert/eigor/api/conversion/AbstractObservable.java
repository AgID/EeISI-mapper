package it.infocert.eigor.api.conversion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class AbstractObservable {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    protected final List<ConversionCallback> listeners;

    protected AbstractObservable(List<ConversionCallback> listeners) {
        this.listeners = listeners;
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



}
