package app.utils;

import app.exceptions.ApiException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Utils {

    public static String getPropertyValue(String propName, String resourceName)  {
        try (InputStream is = Utils.class.getClassLoader().getResourceAsStream(resourceName)) {
            Properties prop = new Properties();
            prop.load(is);

            String value = prop.getProperty(propName);
            if (value != null) {
                return value.trim();  // Trim whitespace
            } else {
                throw new ApiException(500, String.format("Property %s not found in %s", propName, resourceName));
            }
        } catch (IOException ex) {
            throw new ApiException(500, String.format("Could not read property %s.", propName));
        }
    }


    /**
     * Reads a setting from an environment variable if it exists (used on the server),
     * otherwise from config.properties (used locally).
     * Keeps secrets out of the code and lets the same code run unchanged in both environments.
     */
    public static String setting(String key) {
        String fromEnv = System.getenv(key);
        return fromEnv != null ? fromEnv : Utils.getPropertyValue(key, "config.properties");
    }
}