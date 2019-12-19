package com.mybus.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

public final class PropertiesUtils {

    private static final Logger logger = LoggerFactory.getLogger(PropertiesUtils.class);

    private PropertiesUtils() {
        // Utility classes should not have a public or default constructor
    }

    public static Properties getPropertiesFromHomeDirFile(final String filename) {
        Properties properties = new Properties();
        if (filename == null) {
            logger.warn("null filename provided in PropertiesUtils.getPropertiesFromHomeDirFile()");
            return properties;
        }

        File propertiesFile = new File(System.getProperty("user.home"), filename);

        if (propertiesFile.exists()) {
            logger.info("Reading properties from ~/" + filename + " file.");
            InputStream ubePropertiesInputStream;
            try {
                ubePropertiesInputStream = new FileInputStream(propertiesFile);
                properties.load(ubePropertiesInputStream);
                logger.info(properties.size() + " properties were read from ~/" + filename);
            } catch (FileNotFoundException e) {
                logger.error("File not found: ~/" + filename, e);
            } catch (IOException e) {
                logger.error("Error reading ~/" + filename, e);
            }
        } else {
            if (logger.isWarnEnabled()) {
                logger.warn("You have no custom properties file provided in ~/" + filename + ".  "
                        + "If you want to override the default properties, please put your values in that file.");
            }
        }
        return properties;
    }
    
}