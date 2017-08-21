package it.infocert.eigor.model.core.datatypes;

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

    private final String identificationSchema;
    private final String schemaVersion;
    private final String identifier;

    public Identifier(String identificationSchema, String schemaVersion, String identifier) {
        this.identificationSchema = checkNotNull( identificationSchema );
        this.schemaVersion = checkNotNull( schemaVersion );
        this.identifier = checkNotNull( identifier );
    }

    public Identifier(String identificationSchema, String identifier) {
        this.identificationSchema = checkNotNull(identificationSchema);
        this.schemaVersion = null;
        this.identifier = checkNotNull(identifier);
    }

    public Identifier(String identifier) {
        this.identificationSchema = null;
        this.schemaVersion = null;
        this.identifier = checkNotNull( identifier );
    }

    public String getIdentificationSchema() {
        return identificationSchema;
    }

    public String getSchemaVersion() {
        return schemaVersion;
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String toString() {
        String identifier = this.identifier;
        if(schemaVersion!=null) identifier = schemaVersion + ":" + identifier;
        if(identificationSchema!=null) identifier = identificationSchema + ":" + identifier;
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
