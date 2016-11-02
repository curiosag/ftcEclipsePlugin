package org.cg.eclipse.plugins.ftc;

import org.cg.common.check.Check;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;

public class FtcStyledText extends StyledText {

	public FtcStyledText(Composite parent, int style) {
		super(parent, style);
	}

	/**
	 * prevents repainting existing styles
	 */
	@Override
	public void setStyleRange(StyleRange range) {
		StyleRange[] curr = getStyleRanges(range.start, range.length, false);
		if (!similar(curr, range)) 
			super.setStyleRange(range);
	}

	private boolean similar(StyleRange[] curr, StyleRange range) {
		Check.notNull(range);
		for (int i = 0; i < curr.length; i++)
			if (!(sameColor(curr[i].foreground, range.foreground) && curr[i].underline == range.underline
					&& curr[i].underlineStyle == range.underlineStyle
					&& sameColor(curr[i].underlineColor, range.underlineColor)))
				return false;
		return true;
	}

	private boolean sameColor(org.eclipse.swt.graphics.Color c1, org.eclipse.swt.graphics.Color c2) {
		return (c1 == null && c2 == null) || ((c1 != null && c2 != null) && c1.equals(c2));
	}

	/**
	 * A hack to prevent that problemmarkers update the entire text view
	 */
	@Override
	public void replaceStyleRanges(int start, int length, StyleRange[] ranges) {
	}

}
