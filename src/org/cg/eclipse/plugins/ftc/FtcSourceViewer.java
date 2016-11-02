package org.cg.eclipse.plugins.ftc;

import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;

public class FtcSourceViewer extends SourceViewer {

	public FtcSourceViewer(Composite parent, IVerticalRuler ruler, int styles) {
		super(parent, ruler, styles);
	}
	
	public FtcSourceViewer(Composite parent, IVerticalRuler ruler, IOverviewRuler overviewRuler,
			boolean overviewRulerVisible, int styles) {
		super(parent, ruler, overviewRuler, overviewRulerVisible, styles);
	}

	/**
	 *  @see org.eclipse.jface.text.createTextWidget(Composite parent, int styles)
	 */
	@Override
	protected StyledText createTextWidget(Composite parent, int styles) {
		StyledText styledText= new FtcStyledText(parent, styles);
		styledText.setLeftMargin(Math.max(styledText.getLeftMargin(), 2));
		return styledText;
	}

}
