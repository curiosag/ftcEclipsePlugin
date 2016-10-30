package org.cg.eclipse.ftcplugin;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class WorkbenchUtil {

	public static void showView(String viewId) 
	{
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(viewId);
		} catch (PartInitException e) {
			System.out.println(String.format("Error opening view %s: %s", viewId, e.getMessage()));
		}
	}
}
