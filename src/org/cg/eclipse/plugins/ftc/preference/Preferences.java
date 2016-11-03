package org.cg.eclipse.plugins.ftc.preference;

import org.eclipse.jface.preference.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.cg.eclipse.plugins.ftc.glue.EclipseClientSettings;
import org.cg.eclipse.plugins.ftc.glue.FtcPluginClient;

public class Preferences extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public Preferences() {
		super(GRID);
		setPreferenceStore(FtcPluginClient.getDefault().getPreferenceStore());
		setDescription("Fusion Tables Console");
	}

	public void createFieldEditors() {
		addField(new StringFieldEditor(EclipseClientSettings.KEY_CLIENT_ID, "&Client Id", getFieldEditorParent()));
		addField(new StringFieldEditor(EclipseClientSettings.KEY_CLIENT_SECRET, "&Client Secret",
				getFieldEditorParent()));
		addField(new IntegerFieldEditor(EclipseClientSettings.KEY_CLIENT_AUTHTIMEOUT, "&Authentication Timeout",
				getFieldEditorParent()));
		addField(new IntegerFieldEditor(EclipseClientSettings.KEY_CLIENT_QUERYLIMIT, "&Query Result Limit",
				getFieldEditorParent()));
		
		Button authenticate = new Button(getFieldEditorParent(), SWT.PUSH);
        authenticate.setText("Authenticate");
        authenticate.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                       FtcPluginClient.getDefault().authenticate();
                }
        });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

}