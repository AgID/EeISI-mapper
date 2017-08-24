package it.infocert.eigor.api.configuration;

import java.util.*;

/**
 * A {@link Properties} that is able to replace placeholders.
 * <h2>Supported Placeholders</h2>
 * <ul>
 *     <li>{@code k1=${k2}}: the value of {@code k1} is the value of {@code k2}.</li>
 *     <li>{@code k1=Hi ${name}}: the value of {@code k1} is the value obtained concatenating {@code "Hi "} and the value of the {@code name} key.</li>
 *     <li>{@code k1=${env.PATH}}: the value of {@code k1} is the value of the environment variable {@code PATH}.</li>
 *     <li>{@code k1=${prop.java.io.tmpdir}}: the value of {@code k1} is the value of the Java system property {@code java.io.tmpdir}.</li>
 * </ul>
 */
public class PropertiesWithReplacement extends Properties {

    /**
     * @see it.infocert.eigor.api.configuration.PropertiesWithReplacement
     */
    public PropertiesWithReplacement(Properties properties) {
        super(properties);
    }

    /**
     * @see it.infocert.eigor.api.configuration.PropertiesWithReplacement
     */
    public PropertiesWithReplacement() {
        super();
    }

    @Override public String getProperty(String key) {
        return _getProperty(key, new ArrayList<String>());
    }

    private String _getProperty(String key, List<String> alreadyLookedUpKeys) {

        if (alreadyLookedUpKeys.contains(key)) {
            alreadyLookedUpKeys.add(key);
            String message = "";
            for (String alreadyLookedUpKey : alreadyLookedUpKeys) {
                if (!message.isEmpty()) {
                    message = message + " -> ";
                }
                message = message + "${" + alreadyLookedUpKey + "}";
            }
            message = "Circular reference detected on placeholders " + message + ".";
            throw new IllegalStateException(message);
        }
        alreadyLookedUpKeys.add(key);

        String originalValue = super.getProperty(key);

        LinkedHashMap<String, String> placeholderWithReplacement = new LinkedHashMap<>();


        if (originalValue != null) {
            List<int[]> tokensz = tokens(originalValue);
            for (int[] tokenz : tokensz) {

                String delimitedPlaceholder = originalValue.substring(tokenz[0], tokenz[1]);

                String value = null;

                // resolve the property value among env variable if needed
                if (delimitedPlaceholder.startsWith("${env.") && delimitedPlaceholder.length() > "${env.}".length()) {
                    String envVariableName = delimitedPlaceholder.substring("${env.".length(), delimitedPlaceholder.length() - 1);
                    String envVariableValue = System.getenv(envVariableName);
                    if(envVariableValue==null) throw new NullPointerException("Unable to resolve " + delimitedPlaceholder + ".");
                    value = envVariableValue;
                }

                // resolve the property value among sys prop if needed
                if (delimitedPlaceholder.startsWith("${prop.") && delimitedPlaceholder.length() > "${prop.}".length()) {
                    String propName = delimitedPlaceholder.substring("${prop.".length(), delimitedPlaceholder.length() - 1);
                    String propValue = System.getProperty(propName);
                    if(propValue==null) throw new NullPointerException("Unable to resolve " + delimitedPlaceholder + ".");
                    value = propValue;
                }

                // resolve the property value among other keys
                if (value == null) {
                    String newPlaceholder = delimitedPlaceholder.substring(2, delimitedPlaceholder.length() - 1);
                    value = _getProperty(newPlaceholder, alreadyLookedUpKeys);
                    if(value==null) throw new NullPointerException("Unable to resolve " + delimitedPlaceholder + ".");
                }

                placeholderWithReplacement.put(delimitedPlaceholder, value);
            }
        }

        if (!placeholderWithReplacement.isEmpty()) {
            for (Map.Entry<String, String> placeholderAndReplacement : placeholderWithReplacement.entrySet()) {
                String placeholder = placeholderAndReplacement.getKey();
                int start = originalValue.indexOf(placeholder);
                int end = start + placeholder.length();
                String preChunk = start > 0 ? originalValue.substring(0, start) : "";
                String postChunk = end <= originalValue.length() ? originalValue.substring(end) : "";

                if (!preChunk.isEmpty() || !postChunk.isEmpty()) {
                    originalValue = preChunk + placeholderAndReplacement.getValue() + postChunk;
                } else {
                    originalValue = placeholderAndReplacement.getValue();
                }
            }
        }
        return originalValue;
    }

    private List<int[]> tokens(String s) {
        ArrayList<int[]> result = new ArrayList<>();
        if (s != null) {
            int end, start = 0;
            do {
                start = s.indexOf("${", start);
                end = s.indexOf("}", start);
                if (start >= 0 && end > 0) {
                    result.add(new int[] { start, end + 1 });
                    start = end;
                }
            } while (start > 0);
        }
        return result;
    }
}
