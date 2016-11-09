package org.cg.eclipse.plugins.ftc;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.editors.text.TextEditor;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.cg.common.check.Check;
import org.cg.eclipse.plugins.ftc.glue.FtcPluginClient;
import org.eclipse.swt.custom.CaretEvent;
import org.eclipse.swt.custom.CaretListener;
import org.eclipse.swt.custom.ST;
import org.eclipse.swt.custom.StyledText;

public class FtcEditor extends TextEditor {

	private final FtcSourceViewerConfiguration sourceViewerConfiguration;
	private final MessageConsoleLogger logging = MessageConsoleLogger.getDefault();
	
	@Override
	public void doSetInput(IEditorInput input) throws CoreException {
		super.doSetInput(input);
	}

	/*
	 * custom implementation to create an FTCSourceViewer
	 * 
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

		Object input = getEditorInput();
		Assert.isTrue(input instanceof IFileEditorInput);

		ISourceViewer viewer = new FtcSourceViewer(((IFileEditorInput) input).getFile(), parent, ruler,
				getOverviewRuler(), isOverviewRulerVisible(), styles);

		getSourceViewerDecorationSupport(viewer);
		
		return viewer;
	}

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
		sourceViewerConfiguration = new FtcSourceViewerConfiguration();
		setSourceViewerConfiguration(sourceViewerConfiguration);
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
		addEditorListeners();
		addCaretListener();
		
		logging.reveal();
		Check.isTrue(getSourceViewer() instanceof FtcSourceViewer);
	}

	private void addCaretListener() {
		getStyledText().addCaretListener(new CaretListener(){

			@Override
			public void caretMoved(CaretEvent event) {
				setStatusLineMessage(String.valueOf(event.caretOffset));
			}});
		
	}

	private void addEditorListeners() {
		FtcEditor thisEditor = this;
		getSite().getPage().addPartListener(new IPartListener() {

			@Override
			public void partActivated(IWorkbenchPart part) {
				if (part == thisEditor)
					FtcPluginClient.getDefault().onEditorActivated(thisEditor);
			}

			@Override
			public void partBroughtToTop(IWorkbenchPart part) {
			}

			@Override
			public void partDeactivated(IWorkbenchPart part) {
			}

			@Override
			public void partOpened(IWorkbenchPart part) {
			}

			@Override
			public void partClosed(IWorkbenchPart part) {
				if (part == thisEditor)
					FtcPluginClient.getDefault().onEditorClosed(thisEditor);
			}
		});

	}

	public void invalidateTextRepresentation() {
		getFtcSourceViewer().resetSyntaxColoring();
		getFtcSourceViewer().invalidateTextPresentation();
	}

	private FtcSourceViewer getFtcSourceViewer() {
		Check.isTrue(getSourceViewer() instanceof FtcSourceViewer);
		return (FtcSourceViewer)getSourceViewer();
	}
	

}
