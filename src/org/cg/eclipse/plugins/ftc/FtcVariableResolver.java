
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

		TemplateApplicationCircumstances circumstances = TemplateApplicationCircumstances.getDefault();
		String pattern = circumstances.getTemplate().getPattern();
		Check.notNull(pattern);

		String currentText = circumstances.getText();
		String patchedText = StringUtil.insert(currentText, circumstances.getOffset(), prepareForParsing(pattern));
		// a "${t}" gets changed to "  t " for allow correct parsing, so the index must be at the "t" rather than "$"
		int variablePosition = circumstances.getOffset() + (pattern.indexOf(String.format("${%s}", getType()))) + 2;

		MessageConsoleLogger.getDefault().Info("* variable resolver *");
		MessageConsoleLogger.getDefault().Info(patchedText);
		MessageConsoleLogger.getDefault().Info(String.format("%d", variablePosition));
		MessageConsoleLogger.getDefault().Info(getType());

		ICompletionProposal[] proposals = FtcCompletionProcessor.getModelElementProposals(patchedText,
				variablePosition);
		MessageConsoleLogger.getDefault().Info(proposals.length + " proposals");
		String[] result = new String[proposals.length];
		for (int i = 0; i < proposals.length; i++)
			result[i] = proposals[i].getDisplayString();
		return result;
	}

	private String prepareForParsing(String pattern) {
		return pattern.replace("$", " ").replace("{", " ").replace("}", " ");
	}
}
