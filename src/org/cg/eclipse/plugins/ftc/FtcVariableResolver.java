
package org.cg.eclipse.plugins.ftc;

import org.cg.common.check.Check;
import org.cg.common.util.StringUtil;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateVariableResolver;

/**
 * proposes
 */
public class FtcVariableResolver extends TemplateVariableResolver {

	@Override
	protected String[] resolveAll(TemplateContext context) {

		Check.isTrue(context instanceof FtcDocumentTemplateContext);
		FtcDocumentTemplateContext ftcContext = (FtcDocumentTemplateContext) context;

		String pattern = ftcContext.getCurrentTemplate().getPattern();
		Check.notNull(pattern);
		String currentText = ftcContext.getDocument().get();
		String patchedText = StringUtil.insert(currentText, ftcContext.getCompletionOffset(),
				prepareForParsing(pattern));
		// + 2 : "${t}" gets changed to " t " on order to allow correct parsing,
		// so the index must be at the "t" rather than "$"
		int variablePosition = ftcContext.getCompletionOffset() + (pattern.indexOf(String.format("${%s}", getType())))
				+ 2;

		/*
		 * MessageConsoleLogger.getDefault().Info("* variable resolver *");
		 * MessageConsoleLogger.getDefault().Info(patchedText);
		 * MessageConsoleLogger.getDefault().Info(String.format("%d",
		 * variablePosition));
		 * MessageConsoleLogger.getDefault().Info(getType());
		 */
		ICompletionProposal[] proposals = FtcCompletionProcessor.getModelElementProposals(patchedText, variablePosition,
				0);
		//MessageConsoleLogger.getDefault().Info(proposals.length + " proposals");
		String[] result = new String[proposals.length];
		for (int i = 0; i < proposals.length; i++)
			result[i] = proposals[i].getDisplayString();
		return result;
	}

	private String prepareForParsing(String pattern) {
		return pattern.replace("$", " ").replace("{", " ").replace("}", " ");
	}
}
