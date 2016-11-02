package org.cg.eclipse.plugins.ftc;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.templates.Template;

public class TemplateApplicationCircumstances {

	private Template template;
	private String text;
	private int offset;
	
	private static TemplateApplicationCircumstances _default;
	
	public static TemplateApplicationCircumstances getDefault()
	{
		if (_default == null)
			_default = new TemplateApplicationCircumstances();
		
		return _default;
	}
		
	public void setCircumstances(Template template, String text, int offset)
	{
		this.template = template;
		this.text = text;
		this.offset = offset;
	}
	
	public Template getTemplate() {
		return template;
	}

	public String getText() {
		return text;
	}

	public int getOffset() {
		return offset;
	}

}
