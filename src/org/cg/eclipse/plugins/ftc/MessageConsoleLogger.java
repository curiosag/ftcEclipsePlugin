package org.cg.eclipse.plugins.ftc;

import org.cg.common.core.AbstractLogger;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import com.google.common.base.Optional;

public class MessageConsoleLogger extends AbstractLogger {

	private final String consoleName = "ftc";
	private MessageConsole console;
	private MessageConsoleStream outputStream;
	private static MessageConsoleLogger _default;

	private MessageConsole findConsole(String name) {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		for (int i = 0; i < existing.length; i++)
			if (name.equals(existing[i].getName()))
				return (MessageConsole) existing[i];

		MessageConsole myConsole = new MessageConsole(name, null);
		conMan.addConsoles(new IConsole[] { myConsole });
		return myConsole;
	}

	public static MessageConsoleLogger getDefault() {
		if (_default == null)
			_default = new MessageConsoleLogger();
		return _default;
	}

	private MessageConsoleLogger() {
		console = findConsole(consoleName);
		outputStream = console.newMessageStream();
	}

	private Optional<IWorkbenchPage> getPage() {
		IWorkbench wb = PlatformUI.getWorkbench();
		IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
		IWorkbenchPage page = win.getActivePage();
		return Optional.fromNullable(page);
	}

	private void write(String s) {
		outputStream.println(s);
	}

	public boolean reveal() {
		Optional<IWorkbenchPage> optPage = getPage();
		if (optPage.isPresent()) {
			IWorkbenchPage page = optPage.get();
			String id = IConsoleConstants.ID_CONSOLE_VIEW;
			IConsoleView view;
			try {
				view = (IConsoleView) page.showView(id);
			} catch (PartInitException e) {
				return false;
			}
			view.display(console);
			return true;
		}
		return false;
	}

	@Override
	protected void hdlInfo(String info) {
		write(info);
	}

	@Override
	protected void hdlError(String error) {
		write(error);
	}

}
