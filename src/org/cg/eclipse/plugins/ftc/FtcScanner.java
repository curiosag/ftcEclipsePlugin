package org.cg.eclipse.plugins.ftc;

import org.eclipse.jface.text.rules.*;

public class FtcScanner extends RuleBasedScanner {

	public int getOffset(){
		return fOffset;
	}
	
	public FtcScanner(ColorManager manager) {
		IToken stringToken = manager.getColoredToken(IColorConstants.STRING);
		IToken numberToken = manager.getColoredToken(IColorConstants.NUMBER);
		
		IRule[] rules = new IRule[4];

		rules[0] = new SingleLineRule("\"", "\"", stringToken, '\\');
		rules[1] = new SingleLineRule("'", "'", stringToken, '\\');
		rules[2] = new NumberRule(numberToken);
		rules[3] = new WhitespaceRule(new WhitespaceDetector());
		
		setRules(rules);
		
		this.setDefaultReturnToken(manager.getColoredToken(IColorConstants.DEFAULT));
	}
}
