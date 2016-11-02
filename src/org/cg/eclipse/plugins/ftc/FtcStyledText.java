package org.cg.eclipse.plugins.ftc;

import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;

public class FtcStyledText extends StyledText {
	
	public FtcStyledText(Composite parent, int style) {
		super(parent, style);
	}

	/**
	 * A gruesome hack to prevent that problemmarkers update the text view
	 */
	@Override
	public void replaceStyleRanges(int start, int length, StyleRange[] ranges) {
	}

}
