package it.infocert.eigor.api.errors;

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ErrorMessage implements Serializable{

    @Nullable
    private final String message;

    @Nullable
    private ErrorCode errorCode;
    private final List<Exception> relatedExceptions = new ArrayList<>(0);

    public ErrorMessage(String message, @Nullable String location, @Nullable String type, @Nullable String code) {
        this(message, new ErrorCode(location, type, code));
    }

    public ErrorMessage(@Nullable String message) {
        this.message = message;
    }

    public ErrorMessage(@Nullable String message, @Nullable ErrorCode errorCode) {
        this.message = message;
        this.errorCode = errorCode;
    }

    public ErrorMessage(ErrorMessage errorMessage, String message) {
        this(message, errorMessage.getErrorCode());
    }

    public ErrorMessage(Exception relatedException, String message, String location, @Nullable String action, @Nullable String code) {
        this(relatedException, message, new ErrorCode(location, action, code));
    }

    public ErrorMessage(Exception relatedException, String message,@Nullable ErrorCode errorCode) {
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
    }

    @Nullable
    public String getMessage() {
        return message;
    }

    @Nullable
    public ErrorCode getErrorCode() {
        return errorCode;
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

    public ErrorCode updateErrorCode(String location, @Nullable String action, String error) {
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
        private String location;

        @Nullable
        private String action;

        @Nullable
        private String error;

        @Nullable
        private ErrorCode errorCode;

        private final List<Exception> exceptions = new ArrayList<>(0);

        private Builder() {}

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder location(String location) {
            Preconditions.checkNotNull(location, "ErrorCode location cannot be null");
            this.location = location;
            return this;
        }

        public Builder action(String action) {
            this.action = action;
            return this;
        }

        public Builder error(String error) {
            this.error = error;
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
            Preconditions.checkNotNull(location, "ErrorCode location cannot be null");
            if (errorCode == null) {
                this.errorCode = new ErrorCode(location, action, error);
            }
            return new ErrorMessage(this);
        }
    }
}
