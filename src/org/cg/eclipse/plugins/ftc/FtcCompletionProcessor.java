package org.cg.eclipse.plugins.ftc;

import java.util.List;

import org.cg.common.check.Check;
import org.cg.eclipse.plugins.ftc.glue.EclipseStyleCompletions;
import org.cg.eclipse.plugins.ftc.glue.FtcPluginClient;
import org.cg.ftc.shared.structures.CodeSnippetCompletion;
import org.cg.ftc.shared.structures.ModelElementCompletion;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateCompletionProcessor;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.editors.text.templates.ContributionContextTypeRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class FtcCompletionProcessor extends TemplateCompletionProcessor {

	private static final String DEFAULT_IMAGE = "$nl$/icons/sample.gif"; //$NON-NLS-1$
	private TemplateContextType templateContextType = null;
	private EclipseStyleCompletions currentCompletions = null;

	private TemplateContextType getTemplateContextType() {
		ContributionContextTypeRegistry registry = new ContributionContextTypeRegistry();
		registry.addContextType(FtcContextType.TYPE);
		return registry.getContextType(FtcContextType.TYPE);
	}
	
	@Override
	protected ICompletionProposal createProposal(Template template, TemplateContext context, IRegion region, int relevance) {
		return new FtcTemplateProposal(template, context, region, getImage(template), relevance);
	}

	@Override
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int documentOffset) {
	
		currentCompletions = getFtcCompletions(viewer.getTextWidget().getText(), documentOffset);
		
		ICompletionProposal[] templates = super.computeCompletionProposals(viewer, documentOffset);
		ICompletionProposal[] completions = getModelElementProposals(currentCompletions.modelElements, documentOffset);

		ICompletionProposal[] result = new ICompletionProposal[templates.length + completions.length];
		
		int idx = 0;
		for (int i = 0; i < templates.length; i++) {
			result[idx] = templates[i];
			idx++;
		}
		for (int i = 0; i < completions.length; i++) {
			result[idx] = completions[i];
			idx++;
		}

		return result;
	}

	private static EclipseStyleCompletions getFtcCompletions(String text, int documentOffset) {
		return FtcPluginClient.getDefault().getCompletions(text, documentOffset);
	}

	private static ICompletionProposal[] getModelElementProposals(List<ModelElementCompletion> modelElements, int documentOffset) {
		ICompletionProposal[] result = new ICompletionProposal[modelElements.size()];
		
		int i = 0;
		for (ModelElementCompletion e : modelElements) {
			result[i] = new CompletionProposal(e.getPatch(), documentOffset, 0, documentOffset);
			i++;
		}
		
		return result;
	}

	public static ICompletionProposal[] getModelElementProposals(String text, int documentOffset)
	{
		return getModelElementProposals(getFtcCompletions(text, documentOffset).modelElements, documentOffset);
	}
	
	@Override
	protected Template[] getTemplates(String contextTypeId) {
		
		Template[] result = new Template[getCurrentCompletions().templates.size()];
		
		int i = 0;
		for (CodeSnippetCompletion template : getCurrentCompletions().templates) {
			result[i] = new Template(template.displayName, "", FtcContextType.TYPE, template.snippet, false);
			i++;
		}
		
		return result;
	}

	@Override
	protected TemplateContextType getContextType(ITextViewer viewer, IRegion region) {
		if (templateContextType == null)
			templateContextType = getTemplateContextType();
		return templateContextType;
	}

	@Override
	protected Image getImage(Template template) {
		ImageRegistry registry = Activator.getDefault().getImageRegistry();
		Image image = registry.get(DEFAULT_IMAGE);
		if (image == null) {
			ImageDescriptor desc = AbstractUIPlugin.imageDescriptorFromPlugin("org.cg.ftceditor.FtcEditor", DEFAULT_IMAGE);
			registry.put(DEFAULT_IMAGE, desc);
			image = registry.get(DEFAULT_IMAGE);

		}
		return image;
	}

	public EclipseStyleCompletions getCurrentCompletions() {
		Check.notNull(currentCompletions);
		return currentCompletions;
	}

}
