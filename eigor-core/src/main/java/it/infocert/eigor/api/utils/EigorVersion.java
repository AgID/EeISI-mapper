package it.infocert.eigor.api.utils;

import com.google.common.io.Resources;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class EigorVersion {

    private static Properties properties = null;

    public static Map<String, String> getAsMap() {
        if (properties == null) {
            loadFromGitProperties();
        }
        return Collections.unmodifiableMap(new HashMap<String,String>((Map)properties));
    }

    public static String getAsString() {
        if (properties == null) {
            loadFromGitProperties();
        }

        StringBuilder sb = new StringBuilder("Eigor ");

        sb.append(properties.getProperty("git.build.version")).append(" ");
        sb.append(properties.getProperty("git.branch"));

        boolean isDirty = Boolean.parseBoolean(properties.getProperty("git.dirty"));
        if (isDirty) {
            sb.append("-dirty!");
        }
        sb.append(" ").append(properties.getProperty("git.commit.id"));
        sb.append(" ").append(properties.getProperty("git.commit.time"));

        return sb.toString();
    }

    public static String getAsDetailedString() {
        if (properties == null) {
            loadFromGitProperties();
        }

        StringBuilder sb = new StringBuilder("Eigor");

        sb.append("\nmaven-version: ").append(properties.getProperty("git.build.version"));
        sb.append("\ngit-branch: ").append(properties.getProperty("git.branch"));

        boolean isDirty = Boolean.parseBoolean(properties.getProperty("git.dirty"));
        if (isDirty) {
            sb.append("-dirty!");
        }
        sb.append("\ngit-revision: ").append(properties.getProperty("git.commit.id"));
        sb.append("\ngit-timestamp: ").append(properties.getProperty("git.commit.time"));

        return sb.toString();
    }

    private static void loadFromGitProperties() {
        properties = new Properties();
        URL resource = Resources.getResource("git.properties");
        try {
            properties.load(resource.openStream());
        } catch (IOException e) {
            throw new RuntimeException("Unable to load version information from git.properties", e);
        }
    }
}
