/**
 * @author Timotei Dolean
 *
 */
package org.wesnoth.ui.syntax;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.xtext.ui.editor.syntaxcoloring.DefaultHighlightingConfiguration;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightingConfigurationAcceptor;
import org.eclipse.xtext.ui.editor.utils.TextStyle;

public class WMLHighlightingConfiguration extends DefaultHighlightingConfiguration
{
	public static final String	RULE_WML_TAG	= "wmlTag";
	public static final String	RULE_WML_KEY	= "wmlKey";
	public static final String	RULE_WML_MACRO	= "wmlMacro";

	@Override
	public void configure(IHighlightingConfigurationAcceptor acceptor)
	{
		super.configure(acceptor);
		acceptor.acceptDefaultHighlighting(RULE_WML_TAG, "WML Tag", tagTextStyle());
		acceptor.acceptDefaultHighlighting(RULE_WML_KEY, "WML Key", keyTextStyle());
		acceptor.acceptDefaultHighlighting(RULE_WML_MACRO, "WML Macro", macroTextStyle());
	}

	public TextStyle macroTextStyle()
	{
		TextStyle textStyle = defaultTextStyle().copy();
		textStyle.setColor(new RGB(197, 137, 23));
		textStyle.setStyle(SWT.ITALIC);
		return textStyle;
	}

	public TextStyle tagTextStyle()
	{
		TextStyle textStyle = defaultTextStyle().copy();
		textStyle.setColor(new RGB(128, 0, 0));
		textStyle.setStyle(SWT.BOLD);
		return textStyle;
	}

	public TextStyle keyTextStyle()
	{
		TextStyle textStyle = defaultTextStyle().copy();
		textStyle.setColor(new RGB(0, 128, 128));
		textStyle.setStyle(SWT.BOLD);
		return textStyle;
	}
}
