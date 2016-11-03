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
		Check.notNull(range);
		StyleRange curr = getStyleRangeAtOffset(range.start);
		if (!similar(curr, range))
			super.setStyleRange(range);
	}

	private boolean similar(StyleRange curr, StyleRange range) {
		return curr != null && curr.length == range.length && sameColor(curr.foreground, range.foreground)
				&& curr.underline == range.underline && curr.underlineStyle == range.underlineStyle
				&& sameColor(curr.underlineColor, range.underlineColor);
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
