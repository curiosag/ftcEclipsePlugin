package org.cg.eclipse.plugins.ftc.glue;

import org.cg.ftc.shared.structures.ClientSettings;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;

public class FtcPreferenceStore implements IPreferenceStore {

	public final static String KEY_CLIENT_ID = "org.cg.eclipse.plugins.ftc.client_id";
	public final static String KEY_CLIENT_SECRET = "org.cg.eclipse.plugins.ftc.client_secret";
	public final static String KEY_CLIENT_QUERYLIMIT = "org.cg.eclipse.plugins.ftc.querylimit";
	public final static String KEY_CLIENT_AUTHTIMEOUT = "org.cg.eclipse.plugins.ftc.authtimeout";

	private final ClientSettings clientSettings;

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
		return false;
	}

	@Override
	public boolean getDefaultBoolean(String name) {
		return false;
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
		return 0;
	}

	@Override
	public long getDefaultLong(String name) {
		return 0;
	}

	@Override
	public String getDefaultString(String name) {
		return "";
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

		default:
			throw new InvalidPreferenceKeyException(name);
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

		default:
			throw new InvalidPreferenceKeyException(name);
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
		throw new NotImplementedException();
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
		setValue(name, 0);
	}

	@Override
	public void setDefault(String name, long value) {
		throw new NotImplementedException();
	}

	@Override
	public void setDefault(String name, String defaultObject) {
		throw new NotImplementedException();
	}

	@Override
	public void setDefault(String name, boolean value) {
		throw new NotImplementedException();
	}

	@Override
	public void setToDefault(String name) {
		setValue(name, "");
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
			throw new InvalidPreferenceKeyException(name);
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
			throw new InvalidPreferenceKeyException(name);
		}
		clientSettings.write();
	}

	@Override
	public void setValue(String name, boolean value) {
		throw new NotImplementedException();
	}

}
