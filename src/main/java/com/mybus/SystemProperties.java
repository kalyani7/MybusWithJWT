package com.mybus;


import com.mybus.util.PropertiesUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SystemProperties {
	private static final String HOME_DIR_PROPS_FILENAME = ".mybus.properties";
	private Properties homeDirProperties;
	
	private static final Logger logger = LoggerFactory.getLogger(SystemProperties.class);

	@Autowired
	private Environment env;
	private Properties manuallySetProperties;

	public SystemProperties(final Environment environment) {
		if (environment == null) {
			throw new IllegalArgumentException("Environment cannot be null when initializing SystemProperties");
		}
		this.env = environment;
		this.homeDirProperties = getPropertiesFromHomeDir();
		this.manuallySetProperties = new Properties();
	}

	public String getProperty(final String propertyName) {
		if (manuallySetProperties != null && manuallySetProperties.containsKey(propertyName)) {
			return manuallySetProperties.getProperty(propertyName);
		}
		if (homeDirProperties != null && homeDirProperties.containsKey(propertyName)) {
			return homeDirProperties.getProperty(propertyName);
		}
		return env.getProperty(propertyName, "");
	}
	@ManagedOperation(description = "Set a system property")
    public void setProperty(final String propertyName, final String value) {
        manuallySetProperties.setProperty(propertyName, value);
    }
    public void setProperty(final SysProps property, final String value) {
        setProperty(property.getPropertyName(), value);
    }

    @ManagedOperation(description = "Get a list of all current properties, in JSON format")
    public String getCurrentPropertyValues() {
        return getCurrentPropertyValuesJSON().toJSONString();
    }

    @SuppressWarnings("unchecked")
    public JSONObject getCurrentPropertyValuesJSON() {
        Set<String> keys = new HashSet<>(SysProps.values().length * 2);
        for (SysProps sysProps : SysProps.values()) {
            keys.add(sysProps.getPropertyName());
        }
        if (manuallySetProperties != null) {
            for (Object key : manuallySetProperties.keySet()) {
                keys.add(key.toString());
            }
        }
        if (homeDirProperties != null) {
            for (Object o : homeDirProperties.keySet()) {
                keys.add(o.toString());
            }
        }
        // there aren't any public getters for the values stored in 'env'.  Might need to use reflection.

        Map m = new TreeMap();
        for (String key : keys) {
            m.put(key, this.getProperty(key));
        }
        return new JSONObject(m);
    }
	private static Properties getPropertiesFromHomeDir() {
		return PropertiesUtils.getPropertiesFromHomeDirFile(HOME_DIR_PROPS_FILENAME);
	}

	public String getProperty(final SysProps property) {
		return getProperty(property.getPropertyName());
	}

	public String getStringProperty(String propertyName, String defaultValue) {
		String value = getProperty(propertyName);
		if(value == null) {
			return defaultValue;
		} else{
			return value;
		}
	}

	public static enum SysProps {
		AWS_S3_OFFICEEXPENSESBUCKET("aws.s3.officeExpensesBucket"),
		EXPIRATION_BUFFER("expiry.buffer"),
		SMS_GATEWAY_API_KEY("sms.api.key"),
		SMS_GATEWAY_URL("sms.api.url"),
		MAX_UPLOAD_SIZE_BYTES("max.upload.size"),
		JSON_PREFIX_TAINT_ENABLED("json.prefix.taint.enabled"),
		REST_API_DOCS_ENABLED("rest.api.docs.enabled"),
		SEND_EMAIL_ENABLED("send.email.enabled"),
		GOOGLE_API_KEY("google.api.key");

		private final String propertyName;

		SysProps(final String propertyName) {
			this.propertyName = propertyName;
		}

		public String getPropertyName() {
			return this.propertyName;
		}
	}
	
	public int getIntegerProperty(final String propertyName) {
		return Integer.valueOf(getProperty(propertyName));
	}

	public int getIntegerProperty(final String propertyName, final int defaultValue) {
		try {
			return Integer.valueOf(getProperty(propertyName));
		} catch (Exception ex) {
			return defaultValue;
		}
	}

	public int getIntegerProperty(final SysProps property) {
		return getIntegerProperty(property.getPropertyName());
	}

	public int getIntegerProperty(final SysProps property, final int defaultValue) {
		return getIntegerProperty(property.getPropertyName(), defaultValue);
	}

	public boolean getBooleanProperty(final SysProps property, final boolean defaultValue) {
		String propertyValue = getProperty(property.getPropertyName());
		return getBooleanValue(propertyValue, defaultValue);
	}

	public boolean getBooleanProperty(final SysProps property) {
		return getBooleanProperty(property, false);
	}

	private boolean getBooleanValue(final String propertyValue, final boolean defaultValue) {
		try {
			return Boolean.valueOf(propertyValue);
		} catch (Exception ex) {
			if (logger.isTraceEnabled()) {
				logger.trace(String.format("Boolean system property could not be parsed.  Its value was '%s'", propertyValue));
			}
			return defaultValue;
		}
	}
	private static String coalesce(String... strings) {
		for (String string : strings) {
			if (!StringUtils.isEmpty(string)) {
				return string;
			}
		}
		return null;
	}

	public boolean getBooleanProperty(final String propertyName, final boolean defaultValue) {
		return getBooleanValue(propertyName, defaultValue);
	}

	public boolean getBooleanProperty(final String propertyName) {
		return getBooleanValue(propertyName, false);
	}

	public String getEnvironmentProperty(final SysProps property, final String def) {
		String propKey = property.getPropertyName();
		return coalesce(System.getProperty(propKey), System.getenv(propKey), getProperty(propKey), def);
	}

}
