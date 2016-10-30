package org.cg.eclipse.plugins.ftc.glue;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.event.DocumentListener;
import javax.swing.table.TableModel;

import org.cg.common.check.Check;
import org.cg.common.interfaces.OnTextFieldChangedEvent;
import org.cg.common.interfaces.OnValueChanged;
import org.cg.common.interfaces.Progress;
import org.cg.common.io.PreferencesStringStorage;
import org.cg.eclipse.plugins.ftc.FtcEditor;
import org.cg.ftc.ftcClientJava.BaseClient;
import org.cg.ftc.ftcClientJava.Const;
import org.cg.ftc.ftcClientJava.FrontEnd;
import org.cg.ftc.ftcClientJava.GuiClient;
import org.cg.ftc.ftcClientJava.Observism;
import org.cg.ftc.ftcClientJava.ftcClientController;
import org.cg.ftc.ftcClientJava.ftcClientModel;
import org.cg.ftc.shared.structures.ClientSettings;
import org.eclipse.swt.widgets.Display;

import com.google.common.base.Optional;

public class FtcPluginClient extends BaseClient implements FrontEnd {

	private static FtcPluginClient _default;

	private ActionListener actionListener;

	private final ClientSettings clientSettings = ClientSettings.instance(GuiClient.class);
	private final ftcClientModel model = new ftcClientModel(clientSettings);
	private final Progress progress = createProgress();
	private final ftcClientController controller = new ftcClientController(model, logging, getConnector(),
			clientSettings,
			new PreferencesStringStorage(org.cg.ftc.shared.uglySmallThings.Const.PREF_ID_CMDHISTORY, GuiClient.class),
			progress);

	private Optional<FtcEditor> activeEditor = Optional.absent();

	public void onEditorActivated(FtcEditor e) {
		activeEditor = Optional.of(e);
	}

	public void onEditorDeactivated(FtcEditor e) {
		activeEditor = Optional.absent();
	}

	public static FtcPluginClient getDefault() {
		if (_default == null)
			_default = new FtcPluginClient();
		return _default;
	}

	private FtcPluginClient() {
		setActionListener(controller);

		model.resultData.addObserver(createResultDataObserver());
		model.resultText.addObserver(createOpResultObserver());

		model.clientId.setValue(clientSettings.clientId);
		model.clientSecret.setValue(clientSettings.clientSecret);

		addQueryTextChangedListener(model.queryText.getListener());
		controller.authenticate();
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

	public void translateCommand(String commandId) {
		Check.notNull(actionListener);
		if (activeEditor.isPresent()) {
			model.caretPositionQueryText = activeEditor.get().getCaretOffset();
			model.queryText.setValue(activeEditor.get().text());
		}
		actionListener.actionPerformed(new ActionEvent(this, 0, Const.listTables));
	}

	@Override
	public Progress getProgressMonitor() {
		return null;
	}

	@Override
	public void setActionListener(ActionListener l) {
		actionListener = l;
	}

	@Override
	public void addClientIdChangedListener(OnTextFieldChangedEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addClientSecretChangedListener(OnTextFieldChangedEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addResultTextChangedListener(DocumentListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addQueryTextChangedListener(DocumentListener listener) {
	}

	@Override
	public void addQueryCaretChangedListener(OnValueChanged<Integer> onChange) {
		// TODO Auto-generated method stub

	}

	@Override
	public Observer createClientIdObserver() {
		return unObserver;
	}

	@Override
	public Observer createClientSecretObserver() {
		return unObserver;
	}

	@Override
	public Observer createOpResultObserver() {
		return new Observer() {

			@Override
			public void update(Observable o, Object arg) {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						logging.Info(Observism.decodeTextModelObservable(o));
					}
				});

			}
		};
	}

	@Override
	public Observer createQueryObserver() {
		return unObserver;
	}

	@Override
	public Observer createResultDataObserver() {
		return new Observer() {

			@Override
			public void update(Observable o, Object arg) {
				TableModel t = Observism.decodeTableModelObservable(o);

				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						logging.Info(
								String.format("retrieved %d rows %d columns", t.getRowCount(), t.getColumnCount()));
					}
				});
			}
		};

	}

	private static Observer unObserver = new Observer() {
		@Override
		public void update(Observable o, Object arg) {
		}
	};

}
