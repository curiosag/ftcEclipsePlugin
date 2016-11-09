package org.cg.eclipse.plugins.ftc.glue;

import java.util.HashMap;
import java.util.Map;

import org.cg.eclipse.plugins.ftc.preference.StyleAspect;
import org.cg.ftc.shared.structures.ClientSettings;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.jface.util.IPropertyChangeListener;

/**
 * 
 * a hybrid monster
 *
 */
public class FtcPreferenceStore implements IPreferenceStore {

	public final static String KEY_CLIENT_ID = "org.cg.eclipse.plugins.ftc.client_id";
	public final static String KEY_CLIENT_SECRET = "org.cg.eclipse.plugins.ftc.client_secret";
	public final static String KEY_CLIENT_QUERYLIMIT = "org.cg.eclipse.plugins.ftc.querylimit";
	public final static String KEY_CLIENT_AUTHTIMEOUT = "org.cg.eclipse.plugins.ftc.authtimeout";

	private final ClientSettings clientSettings;
	
	private final Map<String, String> defaults = new HashMap<String, String>();

	private static final String prefixStylePreference = "styleKey";
	
	public static boolean isStyleKey(String preferenceKey) {
		return preferenceKey != null && preferenceKey.startsWith(prefixStylePreference);
	}

	public static String getStyleKey(StyleAspect aspect, String tokenName) {
		return prefixStylePreference + ";" + aspect.name() + ";" + tokenName;
	}
	
	public FtcPreferenceStore(ClientSettings clientSettings) {
		this.clientSettings = clientSettings;
	}

	@Override
	public void addPropertyChangeListener(IPropertyChangeListener listener) {
	}

	@Override
	public boolean contains(String name) {
		return true;
	}

	@Override
	public void firePropertyChangeEvent(String name, Object oldValue, Object newValue) {

	}

	@Override
	public boolean getBoolean(String name) {
		return clientSettings.getPreferences().getBoolean(name, getDefaultBoolean(name));
	}

	@Override
	public boolean getDefaultBoolean(String name) {
		String value = defaults.get(name);
		if (value == null)
			return false;
		else
			return StringConverter.asBoolean(value);
	}

	@Override
	public double getDefaultDouble(String name) {
		return 0;
	}

	@Override
	public float getDefaultFloat(String name) {
		return 0;
	}

	@Override
	public int getDefaultInt(String name) {
		String value = defaults.get(name);
		if (value == null)
			return 0;
		else
			return StringConverter.asInt(value);
	}

	@Override
	public long getDefaultLong(String name) {
		return 0;
	}

	@Override
	public String getDefaultString(String name) {
		String value = defaults.get(name);
		if (value == null)
			return "";
		else
			return value;
	}

	@Override
	public double getDouble(String name) {
		throw new NotImplementedException();
	}

	@Override
	public float getFloat(String name) {
		throw new NotImplementedException();
	}

	@Override
	public int getInt(String name) {
		switch (name) {
		case KEY_CLIENT_AUTHTIMEOUT:
			return clientSettings.authTimeout;

		case KEY_CLIENT_QUERYLIMIT:
			return clientSettings.defaultQueryLimit;

		default: {
			return clientSettings.getPreferences().getInt(name, getDefaultInt(name));
		}

		}
	}

	@Override
	public long getLong(String name) {
		throw new NotImplementedException();
	}

	@Override
	public String getString(String name) {
		switch (name) {
		case KEY_CLIENT_ID:
			return clientSettings.clientId;

		case KEY_CLIENT_SECRET:
			return clientSettings.clientSecret;

		default: {
			return clientSettings.getPreferences().get(name, getDefaultString(name));
		}
		}
	}

	@Override
	public boolean isDefault(String name) {
		return false;
	}

	@Override
	public boolean needsSaving() {
		return true;
	}

	@Override
	public void putValue(String name, String value) {
		clientSettings.getPreferences().put(name, value);
	}

	@Override
	public void removePropertyChangeListener(IPropertyChangeListener listener) {
	}

	@Override
	public void setDefault(String name, double value) {
		throw new NotImplementedException();
	}

	@Override
	public void setDefault(String name, float value) {
		throw new NotImplementedException();
	}

	@Override
	public void setDefault(String name, int value) {
		defaults.put(name, StringConverter.asString(value));
	}

	@Override
	public void setDefault(String name, long value) {
		throw new NotImplementedException();
	}

	@Override
	public void setDefault(String name, String defaultObject) {
		defaults.put(name, defaultObject);
	}

	@Override
	public void setDefault(String name, boolean value) {
		defaults.put(name, StringConverter.asString(value));
	}

	@Override
	public void setToDefault(String name) {
		throw new NotImplementedException();
	}

	@Override
	public void setValue(String name, double value) {
		throw new NotImplementedException();
	}

	@Override
	public void setValue(String name, float value) {
		throw new NotImplementedException();
	}

	@Override
	public void setValue(String name, int value) {
		switch (name) {
		case KEY_CLIENT_AUTHTIMEOUT:
			clientSettings.authTimeout = value;
			break;

		case KEY_CLIENT_QUERYLIMIT:
			clientSettings.defaultQueryLimit = value;
			break;

		default:
			clientSettings.getPreferences().putInt(name, value);
		}
		clientSettings.write();
	}

	@Override
	public void setValue(String name, long value) {
		throw new NotImplementedException();
	}

	@Override
	public void setValue(String name, String value) {
		switch (name) {
		case KEY_CLIENT_ID:
			clientSettings.clientId = value;
			break;

		case KEY_CLIENT_SECRET:
			clientSettings.clientSecret = value;
			break;

		default:
			clientSettings.getPreferences().put(name, value);
		}
		clientSettings.write();
	}

	@Override
	public void setValue(String name, boolean value) {
		setValue(name, StringConverter.asString(value));
	}

}
