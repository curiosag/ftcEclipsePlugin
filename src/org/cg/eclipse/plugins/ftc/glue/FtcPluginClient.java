package org.cg.eclipse.plugins.ftc.glue;

import java.awt.event.ActionEvent;
import java.util.Observable;
import java.util.Observer;

import org.cg.common.check.Check;
import org.cg.common.io.PreferencesStringStorage;
import org.cg.common.util.Op;
import org.cg.eclipse.plugins.ftc.FtcEditor;
import org.cg.eclipse.plugins.ftc.MessageConsoleLogger;
import org.cg.eclipse.plugins.ftc.PluginConst;
import org.cg.eclipse.plugins.ftc.WorkbenchUtil;
import org.cg.eclipse.plugins.ftc.view.ResultView;
import org.cg.ftc.ftcClientJava.BaseClient;
import org.cg.ftc.ftcClientJava.Const;
import org.cg.ftc.ftcClientJava.GuiClient;
import org.cg.ftc.ftcClientJava.Observism;
import org.cg.ftc.ftcClientJava.ftcClientController;
import org.cg.ftc.ftcClientJava.ftcClientModel;
import org.cg.ftc.shared.interfaces.SyntaxElementSource;
import org.cg.ftc.shared.structures.ClientSettings;
import org.cg.ftc.shared.structures.Completions;
import org.cg.ftc.shared.structures.RunState;
import org.cg.ftc.shared.uglySmallThings.Events;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.IJobFunction;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;

import com.google.common.eventbus.Subscribe;

import com.google.common.base.Optional;

public class FtcPluginClient extends BaseClient {

	private static FtcPluginClient _default;

	private final ClientSettings clientSettings = ClientSettings.instance(GuiClient.class);
	private final ftcClientModel model = new ftcClientModel(clientSettings);
	private final CancellableProgress progress = createProgress();
	private final ftcClientController controller = new ftcClientController(model, logging, getConnector(),
			clientSettings,
			new PreferencesStringStorage(org.cg.ftc.shared.uglySmallThings.Const.PREF_ID_CMDHISTORY, GuiClient.class),
			progress);

	private Optional<FtcEditor> activeEditor = Optional.absent();
	private final IPreferenceStore preferenceStore = new FtcPreferenceStore(clientSettings);
	private boolean busy;

	public IPreferenceStore getPreferenceStore() {
		return preferenceStore;
	};

	public void authenticate() {
		controller.authenticate();
	}

	public SyntaxElementSource getSyntaxElementSource() {
		return controller;
	}

	public EclipseStyleCompletions getCompletions(String text, int cursorPos) {
		model.caretPositionQueryText = cursorPos;

		Completions completions = controller.get(text, cursorPos);
		return new EclipseStyleCompletions(completions);
	}

	public void onEditorActivated(IWorkbenchPart e) {
		activeEditor = Optional.of((FtcEditor) e);
	}

	public void onEditorClosed(IWorkbenchPart e) {
		activeEditor = Optional.absent();
	}

	public static FtcPluginClient getDefault() {
		if (_default == null)
			_default = new FtcPluginClient();
		return _default;
	}

	private FtcPluginClient() {
		model.resultData.addObserver(createResultDataObserver());
		model.resultText.addObserver(createOpResultObserver());

		model.clientId.setValue(clientSettings.clientId);
		model.clientSecret.setValue(clientSettings.clientSecret);

		logging.setDelegate(MessageConsoleLogger.getDefault());

		registerForLongOperationEvent();

		logging.Info("connecting to fusion tables service");
		controller.authenticate();

	}

	private CancellableProgress createProgress() {
		FtcPluginClient client = this;

		return new CancellableProgress() {

			int curr;
			SubMonitor m;
			private boolean cancelled;

			@Override
			public void init(int max) {
				cancelled = false;
				curr = 0;
				Job.create("ftc composite query ", new IJobFunction() {

					@Override
					public IStatus run(IProgressMonitor monitor) {

						m = SubMonitor.convert(monitor, max);
						while (!cancelled && curr < max) {
							if (monitor.isCanceled())
								hdlInternallyCancelled(client);

							try {
								Thread.sleep(500);
							} catch (InterruptedException e) {
							}
						}
						m.done();
						return Status.OK_STATUS;
					}

					private void hdlInternallyCancelled(FtcPluginClient client) {
						Display.getDefault().asyncExec(new Runnable() {
							public void run() {
								client.runCommand(Const.cancelExecution);
							}
						});
						cancelled = true;
					}
				}).schedule();

			}

			@Override
			public void announce(int progress) {
				if (!cancelled)
					m.worked(progress - curr);
				curr = progress;
			}

			@Override
			public void cancel() {
				cancelled = true;
			}
		};
	}

	public void runCommand(String commandId) {
		if (activeEditor.isPresent()) {
			model.caretPositionQueryText = activeEditor.get().getCaretOffset();
			model.queryText.setValue(activeEditor.get().getText());

			MessageConsoleLogger.getDefault().Info(commandId);
			
			if (commandId.equals(PluginConst.FOCUS_DATA_VIEW)) {
				IViewPart view = WorkbenchUtil.showView(PluginConst.RESULT_VIEW_ID);
				view.setFocus();
			} else {
				if (busy && commandId.equals(Const.cancelExecution))
					progress.cancel();
				else {
					if (busy)
						logging.Info("Operation in progress...");
					else
						controller.actionPerformed(new ActionEvent(this, 0, commandId));
				}
			}
		}

	}

	private Observer createOpResultObserver() {
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

	private Observer createResultDataObserver() {
		return new Observer() {

			@Override
			public void update(Observable o, Object arg) {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						IViewPart view = WorkbenchUtil.showView(PluginConst.RESULT_VIEW_ID);
						Check.isTrue(view instanceof ResultView);
						ResultView resultView = (ResultView) view;
						resultView.displayTable(Observism.decodeTableModelObservable(o));
					}
				});
			}
		};

	}

	private void registerForLongOperationEvent() {
		Events.ui.register(this);
	}

	@Subscribe
	public void eventBusOnLongOperation(RunState state) {
		busy = Op.in(state, RunState.AUTHENTICATION_STARTED, RunState.QUERYEXEC_STARTED);
	}
}
