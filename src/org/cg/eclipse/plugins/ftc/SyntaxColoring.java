package org.cg.eclipse.plugins.ftc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.cg.eclipse.plugins.ftc.glue.FtcPluginClient;
import org.cg.ftc.shared.interfaces.SyntaxElement;
import org.cg.ftc.shared.interfaces.SyntaxElementType;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

public class SyntaxColoring {

	private List<SyntaxElement> tokens = new ArrayList<SyntaxElement>();
	private HashMap<SyntaxElementType, Color> elementColors = new HashMap<SyntaxElementType, Color>();

	private final StyledText styledText;

	Color red;
	Color yellow;
	Color black;
	Color blue;

	public SyntaxColoring(StyledText styledText, ColorManager colorManager) {
		this.styledText = styledText;

		yellow = colorManager.getColor(new RGB(255, 214, 51));
		blue = colorManager.getColor(new RGB(0, 0, 255));
		red = colorManager.getColor(new RGB(255, 0, 0));
		black = colorManager.getColor(new RGB(0, 0, 0));

		elementColors.put(SyntaxElementType.tableName, blue);
		elementColors.put(SyntaxElementType.columnName, blue);
		elementColors.put(SyntaxElementType.viewName, blue);
		elementColors.put(SyntaxElementType.alias, blue);

		elementColors.put(SyntaxElementType.stringLiteral, blue);
		elementColors.put(SyntaxElementType.numericLiteral, red);
		elementColors.put(SyntaxElementType.identifier, red);

	}

	public Color getColor(SyntaxElementType type) {
		Color result = elementColors.get(type);
		if (result == null)
			result = black;
		return result;
	}

	/**
	 * parses the text and populates caches for style ranges and markers
	 * 
	 * @param text
	 *            the text to parse
	 */
	public void setText(String text) {
		tokens = FtcPluginClient.getDefault().getSyntaxElementSource().get(text);
	}

	/**
	 * return get styles within a range. call setText() before to populate style
	 * caches.
	 * 
	 * @param idxFrom
	 *            range from
	 * @param idxTo
	 *            range to
	 * @return
	 */
	public List<StyleRange> getStyles(int idxFrom, int idxTo) {

		ArrayList<StyleRange> result = new ArrayList<StyleRange>();

		for (SyntaxElement t : tokens)
			if (t.from >= idxFrom && t.to <= idxTo)
					result.add(getStyleRange(t, getColor(t.type)));

		return result;
	}

	private StyleRange getStyleRange(SyntaxElement t, Color color) {
		StyleRange result = new StyleRange(t.from, (t.to - t.from) + 1, color, null);
		setUnderlineStyles(t, result);

		return result;
	}

	private void setUnderlineStyles(SyntaxElement t, StyleRange result) {
		if (t.type == SyntaxElementType.error || t.hasSemanticError()) {
			result.underline = true;
			result.underlineStyle = SWT.UNDERLINE_SQUIGGLE;
			result.underlineColor = red;
		}
		if (t.type == SyntaxElementType.error)
			result.underlineColor = red;

		if (t.hasSemanticError())
			result.underlineColor = yellow;
	}

	/**
	 * Set markers. Call setText() before to reparse document
	 */

	public void setMarkers(IResource r) {
		try {
			r.deleteMarkers(PluginConst.MARKER_TYPE_SYNTAXERROR, false, 0);
			r.deleteMarkers(PluginConst.MARKER_TYPE_MODELERROR, false, 0);
		} catch (CoreException e1) {
			e1.printStackTrace();
		}

		for (SyntaxElement t : tokens) {
			if (t.type == SyntaxElementType.error)
				createMarker(PluginConst.MARKER_TYPE_SYNTAXERROR, r, t, IMarker.SEVERITY_ERROR,
						String.format("Invalid token '%s'", t.value));
			else if (t.hasSemanticError())
				createMarker(PluginConst.MARKER_TYPE_MODELERROR, r, t, IMarker.SEVERITY_WARNING,
						String.format("Invalid name '%s'", t.value));
		}

	}

	private void createMarker(String markerType, IResource r, SyntaxElement t, int severity, String message) {
		try {
			IMarker m = r.createMarker(markerType);

			m.setAttribute(IMarker.SEVERITY, severity);
			m.setAttribute(IMarker.CHAR_START, t.from);
			m.setAttribute(IMarker.CHAR_END, t.to + 1);
			m.setAttribute(IMarker.MESSAGE, message);
			m.setAttribute(IMarker.LINE_NUMBER, styledText.getContent().getLineAtOffset(t.from));

		} catch (CoreException e1) {
			e1.printStackTrace();
		}

	}

	/**
	 * Set styles. Call setText() before to reparse document
	 */

	public void setStyles() {

		try {

			int startIdx = 0;
			int endIdx = styledText.getText().length() - 1;

			for (StyleRange r : getStyles(startIdx, endIdx))
				styledText.setStyleRange(r);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
