package org.cg.eclipse.plugins.ftc.glue;

import org.cg.ftc.shared.structures.ClientSettings;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

public class EclipseClientSettings extends ClientSettings {

	private static ClientSettings instance = null;
	public final static String KEY_CLIENT_ID = "org.cg.eclipse.plugins.ftc.client_id";
	public final static String KEY_CLIENT_SECRET = "org.cg.eclipse.plugins.ftc.client_secret";
	public final static String KEY_CLIENT_QUERYLIMIT = "org.cg.eclipse.plugins.ftc.querylimit";
	public final static String KEY_CLIENT_AUTHTIMEOUT = "org.cg.eclipse.plugins.ftc.authtimeout";
	
	public EclipseClientSettings(IPreferenceStore store) {
		
		clientId = store.getString(KEY_CLIENT_ID);
		clientSecret  = store.getString(KEY_CLIENT_SECRET);
		defaultQueryLimit  = store.getInt(KEY_CLIENT_QUERYLIMIT);
		authTimeout = store.getInt(KEY_CLIENT_AUTHTIMEOUT);
		
		addListeners(store);
	}

	private void addListeners(IPreferenceStore store) {
		store.addPropertyChangeListener(new IPropertyChangeListener(){

			@Override
			public void propertyChange(PropertyChangeEvent e) {
				e.getProperty().equals(KEY_CLIENT_ID);
				
			}});
		
	}

	public static ClientSettings instance(IPreferenceStore preferenceStore) {
		if (instance == null)
			instance = new EclipseClientSettings(preferenceStore);
		return instance;
	}
	
}
