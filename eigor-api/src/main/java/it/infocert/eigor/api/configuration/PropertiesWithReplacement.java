package it.infocert.eigor.api.configuration;

import java.util.*;

public class PropertiesWithReplacement extends Properties {

    public PropertiesWithReplacement(Properties properties) {
        super(properties);
    }

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

        List<int[]> tokensz = tokens(originalValue);
        for (int[] tokenz : tokensz) {

            String delimitedPlaceholder = originalValue.substring(tokenz[0], tokenz[1]);

            String value = null;

            // resolve the property value among env variable if needed
            if (delimitedPlaceholder.startsWith("${env.") && delimitedPlaceholder.length() > "${env.}".length()) {
                String envVariableName = delimitedPlaceholder.substring("${env.".length(), delimitedPlaceholder.length() - 1);
                String envVariableValue = System.getenv(envVariableName);
                value = envVariableValue;
            }

            // resolve the property value among sys prop if needed
            if (delimitedPlaceholder.startsWith("${prop.") && delimitedPlaceholder.length() > "${prop.}".length()) {
                String propName = delimitedPlaceholder.substring("${prop.".length(), delimitedPlaceholder.length() - 1);
                String propValue = System.getProperty(propName);
                value = propValue;
            }

            // resolve the property value among other keys
            if (value == null) {
                value = _getProperty(delimitedPlaceholder.substring(2, delimitedPlaceholder.length() - 1), alreadyLookedUpKeys);
            }

            placeholderWithReplacement.put(delimitedPlaceholder, value);
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
