package org.cg.eclipse.ftcplugin.glue;

import org.cg.common.interfaces.Progress;
import org.cg.common.io.PreferencesStringStorage;
import org.cg.ftc.ftcClientJava.BaseClient;
import org.cg.ftc.ftcClientJava.GuiClient;
import org.cg.ftc.ftcClientJava.ftcClientController;
import org.cg.ftc.ftcClientJava.ftcClientModel;
import org.cg.ftc.shared.structures.ClientSettings;

public class FtcPluginClient extends BaseClient {

	private static FtcPluginClient _default;
	
	private final ClientSettings clientSettings = ClientSettings.instance(GuiClient.class);
	private final ftcClientModel model = new ftcClientModel(clientSettings);
	private final Progress progress = createProgress();
	private final ftcClientController controller = new ftcClientController(model, logging, getConnector(), clientSettings,
			new PreferencesStringStorage(org.cg.ftc.shared.uglySmallThings.Const.PREF_ID_CMDHISTORY,
					GuiClient.class),
			progress);
	
	public final FtcPluginFrontEnd frontEnd = new FtcPluginFrontEnd(logging);
	
	public static FtcPluginClient getDefault()
	{
		if (_default == null)
			_default = new FtcPluginClient();
		return _default;
	}
	
	private FtcPluginClient()
	{
		frontEnd.setActionListener(controller);
		model.resultData.addObserver(frontEnd.createResultDataObserver());
		model.resultText.addObserver(frontEnd.createOpResultObserver());

		model.clientId.setValue(clientSettings.clientId);
		model.clientSecret.setValue(clientSettings.clientSecret);
	}
	
	private static Progress createProgress() {
		
		return new Progress() {

			int max;

			@Override
			public void init(int max) {
				this.max = max;
			}

			@Override
			public void announce(int progress) {
				System.out.println(String.format("%d/%d", progress, max));
			}
		};
	}
	
}
