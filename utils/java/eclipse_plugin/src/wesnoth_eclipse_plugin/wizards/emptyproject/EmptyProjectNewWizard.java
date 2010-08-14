/*******************************************************************************
 * Copyright (c) 2010 by Timotei Dolean <timotei21@gmail.com>
 *
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package wesnoth_eclipse_plugin.wizards.emptyproject;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import wesnoth_eclipse_plugin.Logger;
import wesnoth_eclipse_plugin.templates.ReplaceableParameter;
import wesnoth_eclipse_plugin.templates.TemplateProvider;
import wesnoth_eclipse_plugin.utils.Pair;
import wesnoth_eclipse_plugin.utils.ProjectUtils;
import wesnoth_eclipse_plugin.utils.ResourceUtils;
import wesnoth_eclipse_plugin.wizards.NewWizardTemplate;

public class EmptyProjectNewWizard extends NewWizardTemplate
{
	protected EmptyProjectPage0 page0_;
	protected EmptyProjectPage1 page1_;

	@Override
	public void addPages()
	{
		page0_ = new EmptyProjectPage0();
		addPage(page0_);

		page1_ = new EmptyProjectPage1();
		addPage(page1_);

		super.addPages();
	}

	public EmptyProjectNewWizard() {
		setWindowTitle("Create a new empty project");
		setNeedsProgressMonitor(true);
	}

	@Override
	public boolean canFinish()
	{
		// pbl information is not necessary
		return page0_.isPageComplete();
	}

	@Override
	public boolean performFinish()
	{
		try
		{
			// let's create the project
			getContainer().run(false, false, new IRunnableWithProgress() {
				@Override
				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException
				{
					createProject(monitor);
					monitor.done();
				}
			});
		} catch (Exception e)
		{
			Logger.getInstance().logException(e);
			return false;
		}
		return true;
	}

	public void createProject(IProgressMonitor monitor)
	{
		monitor.beginTask("Creating the project structure...", 15);

		try
		{
			IProject currentProject = page0_.getProjectHandle();

			// the project
			if (page0_.getLocationPath().equals(ResourcesPlugin.getWorkspace().getRoot().getLocation()))
			{
				ProjectUtils.createWesnothProject(currentProject, null, page1_.isDataCampaignsProject(),
						!page1_.isDataCampaignsProject(), monitor);
			}
			else
			{
				IProjectDescription newDescription = ResourcesPlugin.getWorkspace().
				newProjectDescription(page0_.getProjectName());
				newDescription.setLocation(page0_.getLocationPath());
				ProjectUtils.createWesnothProject(currentProject, newDescription,
						true, !page1_.isDataCampaignsProject(), monitor);
			}

			monitor.worked(2);

			String emptyProjectStructure = prepareTemplate("empty_project");
			if (emptyProjectStructure == null)
				return;

			List<Pair<String, String>> files;
			List<String> dirs;
			Pair<List<Pair<String, String>>, List<String>> tmp =
					TemplateProvider.getInstance().getFilesDirectories(emptyProjectStructure);
			files = tmp.First;
			dirs = tmp.Second;

			for (Pair<String, String> file : files)
			{
				if (file.Second.equals("pbl") &&
					page1_.getGeneratePBLFile() == false)
					continue;

				ResourceUtils.createFile(currentProject, file.First, prepareTemplate(file.Second), true);
				monitor.worked(1);
			}
			for (String dir : dirs)
			{
				ResourceUtils.createFolder(currentProject, dir);
				monitor.worked(1);
			}
		} catch (CoreException e)
		{
			Logger.getInstance().logException(e);
		}

		monitor.done();
	}

	private String prepareTemplate(String templateName)
	{
		ArrayList<ReplaceableParameter> params = new ArrayList<ReplaceableParameter>();

		params.add(new ReplaceableParameter("$$campaign_name", page1_.getCampaignName()));
		params.add(new ReplaceableParameter("$$author", page1_.getAuthor()));
		params.add(new ReplaceableParameter("$$version", page1_.getVersion()));
		params.add(new ReplaceableParameter("$$description", page1_.getPBLDescription()));
		params.add(new ReplaceableParameter("$$icon", page1_.getIconPath()));
		params.add(new ReplaceableParameter("$$email", page1_.getEmail()));
		params.add(new ReplaceableParameter("$$passphrase", page1_.getPassphrase()));
		params.add(new ReplaceableParameter("$$translations_dir", page1_.getTranslationDir()));

		params.add(new ReplaceableParameter("$$project_name", page0_.getProjectName()));
		params.add(new ReplaceableParameter("$$project_dir_name", page0_.getProjectName()));
		params.add(new ReplaceableParameter("$$type", page1_.getType()));

		return TemplateProvider.getInstance().getProcessedTemplate(templateName, params);
	}
}
