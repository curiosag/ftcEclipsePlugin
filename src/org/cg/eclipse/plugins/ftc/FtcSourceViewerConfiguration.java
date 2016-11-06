package org.cg.eclipse.plugins.ftc;

import org.cg.common.check.Check;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.graphics.RGB;

public class FtcSourceViewerConfiguration extends SourceViewerConfiguration {
	private ColorManager colorManager;

	static class SingleTokenScanner extends BufferedRuleBasedScanner {
		
		@Override
		public IToken nextToken() {
			return super.nextToken();
		}
		
		public SingleTokenScanner(TextAttribute attribute) {
			setDefaultReturnToken(new Token(attribute));
		}
	}
	
	public FtcSourceViewerConfiguration() {
		this.colorManager = ColorManager.getDefault();
	}

	@Override
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] { IDocument.DEFAULT_CONTENT_TYPE, SqlCommentPartitionScanner.SQL_COMMENT };
	}
	
	@Override
	public String getConfiguredDocumentPartitioning(ISourceViewer sourceViewer) {
		return FtcPlugin.SQL_PARTITIONING;
	}

	@Override
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();
		reconciler.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
		
		Check.isTrue(sourceViewer instanceof FtcSourceViewer);
		SyntaxColoring coloring = new SyntaxColoring((FtcSourceViewer)sourceViewer);
		
		addDamagerRepairer(reconciler, new FtcDamagerRepairer(coloring, new ParsedSqlTokensScanner(coloring)), IDocument.DEFAULT_CONTENT_TYPE);
		
		DefaultDamagerRepairer commentDr = new DefaultDamagerRepairer(new SingleTokenScanner(new TextAttribute(ColorManager.getDefault().getColor(IColorConstants.COMMENT))));	
		addDamagerRepairer(reconciler, commentDr, SqlCommentPartitionScanner.SQL_COMMENT);
		
		return reconciler;
	}

	private void addDamagerRepairer(PresentationReconciler reconciler, DefaultDamagerRepairer dr, String contentType) {
		reconciler.setDamager(dr, contentType);
		reconciler.setRepairer(dr, contentType);
	}

	@Override
	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {

		ContentAssistant assistant = new ContentAssistant();
		assistant.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
		assistant.setContentAssistProcessor(new FtcCompletionProcessor(sourceViewer), IDocument.DEFAULT_CONTENT_TYPE);

		assistant.enableAutoActivation(false);
		// assistant.setAutoActivationDelay(500);
		assistant.setProposalPopupOrientation(IContentAssistant.PROPOSAL_OVERLAY);
		assistant.setContextInformationPopupOrientation(IContentAssistant.CONTEXT_INFO_ABOVE);
		assistant.setContextInformationPopupBackground(colorManager.getColor(new RGB(150, 150, 0)));

		return assistant;
	}

}