package org.cg.eclipse.plugins.ftc;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.editors.text.FileDocumentProvider;
import org.eclipse.ui.editors.text.TextEditor;
import com.google.common.base.Stopwatch;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ExtendedModifyEvent;
import org.eclipse.swt.custom.ExtendedModifyListener;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;

import java.util.Observable;
import java.util.Observer;

import org.cg.common.check.Check;
import org.cg.eclipse.plugins.ftc.glue.FtcPluginClient;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;

public class FtcEditor extends TextEditor {

	private final static ColorManager colorManager = ColorManager.getDefault();
	private SyntaxColoring syntaxColoring;

	private final FtcSourceViewerConfiguration sourceViewerConfiguration;
	private final MessageConsoleLogger logging = MessageConsoleLogger.getDefault();
	Stopwatch markerStopwatch = Stopwatch.createStarted();

	private IResource resource;

	/*
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#createSourceViewer(
	 * Composite, IVerticalRuler, int)
	 * 
	 * @see
	 * org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#createSourceViewer(
	 * Composite, IVerticalRuler, int)
	 */

	@Override
	protected ISourceViewer createSourceViewer(Composite parent, IVerticalRuler ruler, int styles) {

		fAnnotationAccess = getAnnotationAccess();
		fOverviewRuler = createOverviewRuler(getSharedColors());

		ISourceViewer viewer = new FtcSourceViewer(parent, ruler, getOverviewRuler(), isOverviewRulerVisible(), styles);
		// ensure decoration support has been created and configured.
		getSourceViewerDecorationSupport(viewer);

		return viewer;
	}

	private Observer onConnectObserver = new Observer() {
		@Override
		public void update(Observable o, Object arg) {
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					// make the token provider think, the text has changed, otherwise it won't re-evaluate
					getStyledText().append(" ");
					// append doesen't trigger documentchanged, though, so setStyles() call is necessary
					setStyles();
				}
			});
		}
	};

	public int getCaretOffset() {
		return getSourceViewer().getTextWidget().getCaretOffset();
	}

	private FtcStyledText getStyledText() {
		StyledText text = getSourceViewer().getTextWidget();
		Check.isTrue(text instanceof FtcStyledText);
		return (FtcStyledText) text;
	}

	public String getText() {
		return getStyledText().getText();
	}

	public FtcEditor() {
		super();

		sourceViewerConfiguration = new FtcSourceViewerConfiguration(colorManager);
		setSourceViewerConfiguration(sourceViewerConfiguration);
		FileDocumentProvider p = new FileDocumentProvider();
		setDocumentProvider(p);

	}

	protected void initializeEditor() {
		super.initializeEditor();
	}

	public void dispose() {
		super.dispose();
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		addExtendedModifyListener();
		addEditorListeners();
		addKeyListener();

		logging.reveal();

		Object input = getEditorInput();
		Assert.isTrue(input instanceof IFileEditorInput);
		resource = ((IFileEditorInput) input).getFile();
		syntaxColoring = new SyntaxColoring(getStyledText(), colorManager);
		setStyles();
	}

	private void addKeyListener() {
		getStyledText().addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// resetting the document to a previous state by ctrl+z
				// doesen't always trigger ExtendedModifyListener in cases
				// when line styles should be reset
				if (((e.stateMask & SWT.CTRL) == SWT.CTRL) && (e.keyCode == 'z'))
					setStyles();
			}
		});

	}

	private void addExtendedModifyListener() {
		getSourceViewer().getTextWidget().addExtendedModifyListener(new ExtendedModifyListener() {

			@Override
			public void modifyText(ExtendedModifyEvent e) {
				synchronized (syntaxColoring) {
					setStyles();
				}
			}

		});

	}

	private void addEditorListeners() {
		FtcEditor thisEditor = this;
		getSite().getPage().addPartListener(new IPartListener() {

			@Override
			public void partActivated(IWorkbenchPart part) {
				if (part == thisEditor) {
					FtcPluginClient.getDefault().onEditorActivated(thisEditor);
					FtcPluginClient.getDefault().addOnConnectListener(onConnectObserver);
				}
			}

			@Override
			public void partBroughtToTop(IWorkbenchPart part) {
			}

			@Override
			public void partDeactivated(IWorkbenchPart part) {
				if (part == thisEditor) {
					FtcPluginClient.getDefault().onEditorActivated(thisEditor);
					FtcPluginClient.getDefault().removeOnConnectListener(onConnectObserver);
				}
			}

			@Override
			public void partOpened(IWorkbenchPart part) {
			}

			@Override
			public void partClosed(IWorkbenchPart part) {
			}
		});

	}

	private void setStyles() {
		syntaxColoring.setText(getText());
		// styles need to be set before markers, because marker styles
		// will be added to existing styles rather than replacing them
		syntaxColoring.setStyles();
		syntaxColoring.setMarkers(resource);
	}

	/**
	 * That's a working implementation, it causes the markers to flicker, though
	 * 
	 * @return
	 */
	@SuppressWarnings("unused")
	private Thread createMarkerThread() {
		return new Thread(new Runnable() {

			@Override
			public void run() {
				while (!Thread.interrupted()) {

					WorkbenchUtil.runJob(new IWorkspaceRunnable() {

						@Override
						public void run(IProgressMonitor monitor) throws CoreException {

							Display.getDefault().asyncExec(new Runnable() {
								@Override
								public void run() {
									synchronized (syntaxColoring) {

										try {
											syntaxColoring.setText(getText());

											syntaxColoring.setMarkers(resource);
											Thread.sleep(100);
											setStyles();
										} catch (Exception e) {
											logging.Info("marking: " + e.getMessage());
										}

									}

								}
							});

						}
					}, resource);

					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						logging.Info("interruptd" + e.getMessage());
					}
				}

			}
		});

	}

}
