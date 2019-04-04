package it.infocert.eigor.api;

import it.infocert.eigor.api.configuration.ConfigurationException;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.errors.ErrorCode;
import it.infocert.eigor.api.xml.PlainXSDValidator;
import it.infocert.eigor.api.xml.XSDValidator;
import it.infocert.eigor.org.springframework.core.io.DefaultResourceLoader;
import it.infocert.eigor.org.springframework.core.io.Resource;

import static com.google.common.base.Preconditions.checkArgument;

public class ConverterUtils {

    public static class NamingRules {

        private final String converterName;

        public NamingRules(String converterName) {

            checkArgument(converterName!=null && !converterName.trim().isEmpty(), "The converter name cannot be empty.");
            checkArgument(converterName.matches(".*[^\\.]"), "Invalid converter name '%s'.", converterName);

            this.converterName = converterName;
        }


        String namingRule(String key) {
            return
                    "eigor.converter." + converterName + "." + key;
        }

        public String namingRuleConfigProperty(String configKey) {
            return "eigor.converter." + converterName + "." + configKey;
        }

        public XSDValidator getXsdValidatorFromConfigOrFail(String configKey, ErrorCode.Location xmlcenIn, EigorConfiguration configuration, DefaultResourceLoader drl) throws ConfigurationException {
            String mandatoryString = configuration.getMandatoryString(namingRuleConfigProperty(configKey));
            try {
                Resource xsdFile = drl.getResource(mandatoryString);
                return new PlainXSDValidator(xsdFile.getFile(), xmlcenIn);
            } catch (Exception e) {
                throw new ConfigurationException("An error occurred while loading XSD for '" + converterName + "' from '" + mandatoryString + "'.", e);
            }
        }

        public SchematronValidator getSchematronFromConfigOrFail(final String configKey, ErrorCode.Location xmlcenIn, EigorConfiguration configuration, DefaultResourceLoader drl) throws ConfigurationException {
            String fullConfigKey = namingRuleConfigProperty(configKey);
            String schFile = configuration.getMandatoryString(fullConfigKey);
            try {
                Resource schemaFile = drl.getResource(schFile);
                boolean schematronAutoUpdate = "true".equals(configuration.getMandatoryString(fullConfigKey + ".auto-update-xslt"));
                SchematronValidator validator = new SchematronValidator(schemaFile, true, schematronAutoUpdate, xmlcenIn);
                return validator;
            } catch (Exception e) {
                throw new ConfigurationException("An error occurred while getting '" + converterName + "' schematron file from resource '" + schFile + "'.", e);
            }
        }
    }

}
