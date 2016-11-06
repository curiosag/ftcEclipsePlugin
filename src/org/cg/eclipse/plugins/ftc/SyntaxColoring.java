package org.cg.eclipse.plugins.ftc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.cg.common.check.Check;
import org.cg.eclipse.plugins.ftc.glue.FtcPluginClient;
import org.cg.ftc.shared.interfaces.SyntaxElement;
import org.cg.ftc.shared.interfaces.SyntaxElementType;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.texteditor.AbstractMarkerAnnotationModel;

public class SyntaxColoring {

	private List<SyntaxElement> tokens = new ArrayList<SyntaxElement>();
	private HashMap<SyntaxElementType, Color> elementColors = new HashMap<SyntaxElementType, Color>();

	private final String TOKEN_TEXT = "token_text";

	Color red;
	Color yellow;
	Color black;
	Color blue;
	Color green;

	private FtcSourceViewer sourceViewer;
	private String text = "";

	public SyntaxColoring(FtcSourceViewer sourceViewer) {
		this.sourceViewer = sourceViewer;
		ColorManager colorManager = ColorManager.getDefault();
		yellow = colorManager.getColor(new RGB(255, 214, 51));
		blue = colorManager.getColor(new RGB(0, 0, 255));
		green = colorManager.getColor(new RGB(0, 155, 0));
		red = colorManager.getColor(new RGB(200, 0, 0));
		black = colorManager.getColor(new RGB(0, 0, 0));

		elementColors.put(SyntaxElementType.tableName, blue);
		elementColors.put(SyntaxElementType.columnName, blue);
		elementColors.put(SyntaxElementType.viewName, blue);
		elementColors.put(SyntaxElementType.alias, blue);

		elementColors.put(SyntaxElementType.stringLiteral, green);
		elementColors.put(SyntaxElementType.numericLiteral, red);

	}

	private IDocument getDocument() {
		IDocument result = sourceViewer.getDocument();
		Check.notNull(result);
		return result;
	};

	private IResource getResource() {
		IResource result = sourceViewer.getResource();
		Check.notNull(result);
		return result;
	};

	private AbstractMarkerAnnotationModel getAnnotationModel() {
		IAnnotationModel m = sourceViewer.getAnnotationModel();
		Check.isTrue(m instanceof AbstractMarkerAnnotationModel);
		return (AbstractMarkerAnnotationModel) m;
	};

	public Color getColor(SyntaxElementType type) {
		Color result = elementColors.get(type);
		if (result == null && !type.equals(SyntaxElementType.whitespace))
			result = black;

		return result;
	}

	/**
	 * parses the text and populates caches for style ranges and markers
	 * 
	 * @param text
	 *            the text to parse
	 */
	public synchronized void setText(String text) {
		this.text  = text;
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
	
	public synchronized List<StyleRange> getStyles(int idxFrom, int idxTo) {

		ArrayList<StyleRange> result = new ArrayList<StyleRange>();

		for (SyntaxElement t : tokens)
			if (t.from >= idxFrom && t.to <= idxTo) {
				Color c = getColor(t.type);
				if (c != null)
					result.add(getStyleRange(t, c));
			}

		addStructuralTokens(result, idxFrom, idxTo);
		
		Collections.sort(result, new Comparator<StyleRange>(){

			@Override
			public int compare(StyleRange o1, StyleRange o2) {
				return o1.start - o2.start;
			}});
		
		return result;
	}
	
	private static String structuralChars = "(),.;";
	private static char eqChar = '=';
	
	private void addStructuralTokens(ArrayList<StyleRange> result, int idxFrom, int idxTo) {
		for (int i = idxFrom; i < Math.min(idxTo, text.length()); i++) {
			String current = String.valueOf(text.charAt(i));
			if (structuralChars.indexOf(text.charAt(i)) >= 0 || specialCaseEqInJoinStatement(result, i))
				result.add(getStyleRange(SyntaxElement.create(current, i, i, 0, SyntaxElementType.unknown), black));
		}
	}

	private boolean specialCaseEqInJoinStatement(ArrayList<StyleRange> result, int i) {
		// "=" as operator is provided as token by the parser, but not "=" in join statements
		return text.charAt(i) == eqChar && ! contains(result, i);
	}

	private boolean contains(ArrayList<StyleRange> result, int i) {
		for (StyleRange r : result) 
			if (r.start == i)
				return true;
		return false;
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

	private void createMarker(IResource r, SyntaxElement t) {
		if (t.type == SyntaxElementType.error)
			createMarker(PluginConst.MARKER_TYPE_SYNTAXERROR, r, t, IMarker.SEVERITY_ERROR,
					String.format("Invalid token '%s'", t.value));
		else if (t.hasSemanticError())
			createMarker(PluginConst.MARKER_TYPE_MODELERROR, r, t, IMarker.SEVERITY_WARNING,
					String.format("Invalid name '%s'", t.value));
	}

	private void updateMarkers() {
		try {
			getAnnotationModel().commit(getDocument());
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}
	}

	public synchronized void resetMarkers() {
		setText(getDocument().get());
		updateMarkers();

		IResource r = getResource();

		IMarker[] markers;
		try {
			markers = r.findMarkers("org.eclipse.core.resources.problemmarker", true, 0);
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}

		hdlDeleted(tokens, markers, r);
		hdlAdded(tokens, markers, r);

	}

	private void hdlDeleted(List<SyntaxElement> tokens, IMarker[] markers, IResource r) {
		HashMap<Integer, SyntaxElement> tokenMap = new HashMap<Integer, SyntaxElement>();
		for (SyntaxElement e : tokens)
			tokenMap.put(e.from, e);

		for (int i = 0; i < markers.length; i++) {
			SyntaxElement token = tokenMap.get(markers[i].getAttribute(IMarker.CHAR_START, -1));
			if (token == null || markerChanged(token, markers[i]))
				try {
					markers[i].delete();
				} catch (CoreException e) {
				}
		}

	}

	private boolean markerChanged(SyntaxElement token, IMarker marker) {
		boolean result = true;
		try {
			result = !token.value.equals(marker.getAttribute(TOKEN_TEXT, ""))
					|| (!getMarkerType(token).equals(marker.getType()));
		} catch (CoreException e) {

		}

		return result;
	}

	private String getMarkerType(SyntaxElement token) {
		if (token.type == SyntaxElementType.error)
			return PluginConst.MARKER_TYPE_SYNTAXERROR;
		if (token.hasSemanticError())
			return PluginConst.MARKER_TYPE_MODELERROR;

		return "no marker";

	}

	private void hdlAdded(List<SyntaxElement> tokens, IMarker[] markers, IResource r) {
		HashMap<Integer, IMarker> markerMap = new HashMap<Integer, IMarker>();
		for (int i = 0; i < markers.length; i++)
			markerMap.put(markers[i].getAttribute(IMarker.CHAR_START, -1), markers[i]);

		for (SyntaxElement e : tokens) {
			IMarker marker = markerMap.get(e.from);
			
			if (marker == null && (e.type == SyntaxElementType.error || e.hasSemanticError()))
				createMarker(r, e);
		}

	}

	private void createMarker(String markerType, IResource r, SyntaxElement t, int severity, String message) {
		try {
			IMarker m = r.createMarker(markerType);

			m.setAttribute(IMarker.SEVERITY, severity);
			m.setAttribute(IMarker.CHAR_START, t.from);
			m.setAttribute(IMarker.CHAR_END, t.to + 1);
			m.setAttribute(IMarker.MESSAGE, message);
			m.setAttribute(TOKEN_TEXT, t.value);
			m.setAttribute(IMarker.LINE_NUMBER, getDocument().getLineOfOffset(t.from));

		} catch (CoreException e1) {
			e1.printStackTrace();
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

	}

}
