package it.infocert.eigor.api.errors;

import it.infocert.eigor.api.utils.Pair;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ErrorMessage implements Serializable {

    public static final String SOURCEMSG_PARAM = "sourceMsg";
    public static final String OFFENDINGITEM_PARAM = "offendingItem";

    private final String message;

    private ErrorCode errorCode;

    private final Map<String, String> parameters = new HashMap<>(0);

    private final List<Exception> relatedExceptions = new ArrayList<>(0);

    @SafeVarargs
    public ErrorMessage(String message, ErrorCode.Location location, ErrorCode.Action action, ErrorCode.Error code, Pair<String, String>... parameters) {
        this(message, new ErrorCode(location, action, code));
        for (Pair<String, String> p : parameters) {
            this.parameters.put(p.getLeft(), p.getRight());
        }
    }

    public ErrorMessage(String message) {
        this.message = message;
    }

    public ErrorMessage(String message, ErrorCode errorCode) {
        this.message = message;
        this.errorCode = errorCode;
    }

    public ErrorMessage(ErrorMessage errorMessage, String message) {
        this(message, errorMessage.getErrorCode());
    }

    public ErrorMessage(Exception relatedException, String message, ErrorCode.Location location, ErrorCode.Action action, ErrorCode.Error code) {
        this(relatedException, message, new ErrorCode(location, action, code));
    }

    public ErrorMessage(Exception relatedException, String message, ErrorCode errorCode) {
        this(message, errorCode);
        this.relatedExceptions.add(relatedException);
    }

    public ErrorMessage(ErrorMessage errorMessage, Exception relatedException, String message) {
        this(relatedException, message, errorMessage.getErrorCode());
        this.relatedExceptions.addAll(errorMessage.relatedExceptions);
    }

    private ErrorMessage(Builder builder) {
        this.message = builder.message;
        this.errorCode = builder.errorCode;
        this.relatedExceptions.addAll(builder.exceptions);
        for (Pair<String, String> p : builder.parameters) {
            this.parameters.put(p.getLeft(), p.getRight());
        }
    }

    @Nullable
    public String getMessage() {
        return message;
    }

    @Nullable
    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public List<Exception> getRelatedExceptions() {
        return relatedExceptions;
    }

    public Exception getRelatedException(int index) {
        return relatedExceptions.get(index);
    }

    public boolean hasRelatedExceptions() {
        return !relatedExceptions.isEmpty();
    }

    public ErrorCode updateErrorCode(ErrorCode.Location location, ErrorCode.Action action, ErrorCode.Error error) {
        if (this.errorCode != null) {
            this.errorCode = new ErrorCode(
                    location != null ? location : this.errorCode.getLocation(),
                    action != null ? action : this.errorCode.getAction(),
                    error != null ? error : this.errorCode.getError()
            );
        } else {
            this.errorCode = new ErrorCode(location, action, error);
        }
        return errorCode;
    }

    @Override
    public String toString() {
        if (errorCode == null) {
            if (message == null) {
                return "";
            } else {
                return message;
            }
        } else {
            return String.format("%s - %s", errorCode, message);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        @Nullable
        private String message;

        @Nullable
        private ErrorCode.Location location;

        @Nullable
        private ErrorCode.Action action;

        @Nullable
        private ErrorCode.Error error;

        @Nullable
        private ErrorCode errorCode;

        private List<Pair<String, String>> parameters = new ArrayList<>(0);

        private final List<Exception> exceptions = new ArrayList<>(0);

        private Builder() {
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder location(ErrorCode.Location location) {
            this.location = location;
            return this;
        }

        public Builder action(ErrorCode.Action action) {
            this.action = action;
            return this;
        }

        public Builder error(ErrorCode.Error error) {
            this.error = error;
            return this;
        }

        public Builder addParam(String key, String value) {
            parameters.add(Pair.of(key, value));
            return this;
        }

        public Builder errorCode(ErrorCode errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public Builder addException(Exception exception) {
            exceptions.add(exception);
            return this;
        }

        public ErrorMessage build() {
            if (errorCode == null) {
                this.errorCode = new ErrorCode(location, action, error);
            }
            return new ErrorMessage(this);
        }
    }
}
