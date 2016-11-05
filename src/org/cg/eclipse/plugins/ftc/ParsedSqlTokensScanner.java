package org.cg.eclipse.plugins.ftc;

import java.util.List;

import org.cg.common.check.Check;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.custom.StyleRange;
import com.google.common.base.Optional;

public class ParsedSqlTokensScanner implements ITokenScanner {

	private final SyntaxColoring coloring; 
	private final boolean debug = false;
	
	List<StyleRange> styles;
	Optional<StyleRange> lastStyle;

	public ParsedSqlTokensScanner(SyntaxColoring coloring)
	{
		this.coloring = coloring;
	}
	
	@Override
	public void setRange(IDocument document, int offset, int length) {
		coloring.setText(document.get());
		styles = coloring.getStyles(offset, offset + length);
	}

	@Override
	public IToken nextToken() {
		if (styles == null || styles.size() == 0) {
			lastStyle = Optional.absent();
			return Token.EOF;
		} else {
			lastStyle = Optional.of(styles.get(0));
			styles.remove(0);
			return new Token(new TextAttribute(lastStyle.get().foreground));
		}
	}

	@Override
	public int getTokenOffset() {
		Check.isTrue(lastStyle.isPresent());
		debug(String.format("off: %d col: %s", lastStyle.get().start, lastStyle.get().foreground));
		return lastStyle.get().start;
	}

	@Override
	public int getTokenLength() {
		Check.isTrue(lastStyle.isPresent());

		debug(String.format("len: %d", lastStyle.get().length));
		return lastStyle.get().length;
	}
	
	private void debug(String s){
		if (debug)
			MessageConsoleLogger.getDefault().Info(s);
	}

}
