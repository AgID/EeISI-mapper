package it.infocert.eigor.model.core.datatypes;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Identifiers (IDs) are keys that are issued by the sender or recipient of a document or by a third party.
 * <p>
 * For each identifier in the model it is stated whether an identification scheme or a scheme version ID may or shall
 * be defined and if so, from what list the identification schemes may be chosen.
 * This EN16931_ Identifier. Type is based on the Identifier. Type as defined in ISO 15000-5:2014 Annex A.
 * The Scheme identifier and the Scheme version ID identify the scheme on which the identifier is based.
 * The use of the attributes is specified for each information element in the semantic model.
 * </p>
 */
public class Identifier {

    @Nullable
    private final String identificationSchema;

    @Nullable
    private final String schemaVersion;

    @Nonnull
    private final String identifier;

    public Identifier(@Nullable String identificationSchema, @Nullable String schemaVersion, @Nonnull String identifier) {
        this.identificationSchema = identificationSchema;
        this.schemaVersion = schemaVersion;
        this.identifier = checkNotNull(identifier);
    }

    public Identifier(@Nullable String identificationSchema, @Nonnull String identifier) {
        this.identificationSchema = identificationSchema;
        this.schemaVersion = null;
        this.identifier = checkNotNull(identifier);
    }

    public Identifier(@Nonnull String identifier) {
        this.identificationSchema = null;
        this.schemaVersion = null;
        this.identifier = checkNotNull(identifier);
    }

    @Nullable
    public String getIdentificationSchema() {
        return identificationSchema;
    }

    @Nullable
    public String getSchemaVersion() {
        return schemaVersion;
    }

    @Nonnull
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String toString() {
        String identifier = this.identifier;
        if (schemaVersion != null) identifier = schemaVersion + ":" + identifier;
        if (identificationSchema != null) identifier = identificationSchema + ":" + identifier;
        return identifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Identifier)) return false;
        Identifier that = (Identifier) o;
        return Objects.equals(identificationSchema, that.identificationSchema) &&
                Objects.equals(schemaVersion, that.schemaVersion) &&
                Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identificationSchema, schemaVersion, identifier);
    }
}
