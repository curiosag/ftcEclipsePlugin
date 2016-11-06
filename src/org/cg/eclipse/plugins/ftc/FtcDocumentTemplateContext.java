package org.cg.eclipse.plugins.ftc;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.templates.DocumentTemplateContext;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateContextType;

public class FtcDocumentTemplateContext extends DocumentTemplateContext {

	private Template template;	
	private String text;
	private int offset;
	
	public FtcDocumentTemplateContext(TemplateContextType type, IDocument document, int offset, int length, Template template) {
		super(type, document, offset, length);
		this.template = template;		
	}

	public FtcDocumentTemplateContext(DocumentTemplateContext context, Template template) {
		this(context.getContextType(), context.getDocument(), context.getStart(), context.getCompletionLength(), template);
	}

	public Template getTemplate() {
		return template;
	}

	public void setTemplate(Template currentTemplate) {
		this.template = currentTemplate;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}
	
	public int getOffset() {
		return offset;
	}

}
