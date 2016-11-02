package org.cg.eclipse.plugins.ftc;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class WorkbenchUtil {

	public static IViewPart showView(String viewId) {
		try {
			return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(viewId);
		} catch (PartInitException e) {
			System.out.println(String.format("Error opening view %s: %s", viewId, e.getMessage()));
			return null;
		}
	}

	public static void runJob(IWorkspaceRunnable runnable, IResource resource) {

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		try {
			workspace.run(runnable, workspace.getRuleFactory().markerRule(resource), 0, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}

	}
}
