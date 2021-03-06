/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.core;

import java.io.File;
import java.util.Enumeration;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

import com.aptana.core.util.PlatformUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.js.debug.core.internal.browsers.Firefox;

/**
 * @author Max Stepanov
 */
public final class JSLaunchConfigurationHelper
{

	public static final String FIREFOX = "Firefox"; //$NON-NLS-1$
	public static final String INTERNET_EXPLORER = "Internet Explorer"; //$NON-NLS-1$
	public static final String SAFARI = "Safari"; //$NON-NLS-1$

	private JSLaunchConfigurationHelper()
	{
	}

	/**
	 * setDefaults
	 * 
	 * @param configuration
	 */
	public static void setDefaults(ILaunchConfigurationWorkingCopy configuration, String nature)
	{
		setBrowserDefaults(configuration, nature);
		setServerDefaults(configuration);
		setHttpDefaults(configuration);
		setAdvancedDefaults(configuration);
	}

	/**
	 * setBrowserDefaults
	 * 
	 * @param configuration
	 */
	@SuppressWarnings("rawtypes")
	public static void setBrowserDefaults(ILaunchConfigurationWorkingCopy configuration, String nature)
	{
		String browser = StringUtil.EMPTY;
		if (nature == null)
		{
			try
			{
				nature = configuration.getAttribute(ILaunchConfigurationConstants.CONFIGURATION_BROWSER_NATURE,
						(String) null);
			}
			catch (CoreException e)
			{
			}
		}
		do
		{
			if (Platform.OS_WIN32.equals(Platform.getOS()))
			{
				/* Firefox */
				if (FIREFOX.equals(nature) || (nature == null))
				{
					for (String location : ILaunchConfigurationConstants.DEFAULT_BROWSER_WINDOWS_FIREFOX)
					{
						location = PlatformUtil.expandEnvironmentStrings(location);
						File file = new File(location);
						if (file.exists() && !file.isDirectory())
						{
							browser = location;
							break;
						}
					}
					if (browser.length() != 0)
					{
						break;
					}
				}

				/* IE */
				if (INTERNET_EXPLORER.equals(nature) || (nature == null))
				{
					String path = PlatformUtil
							.expandEnvironmentStrings(ILaunchConfigurationConstants.DEFAULT_BROWSER_WINDOWS_IE);
					if (new File(path).exists())
					{
						browser = path;
						break;
					}
				}
			}
			else if (Platform.OS_MACOSX.equals(Platform.getOS()))
			{
				/* Firefox */
				if (FIREFOX.equals(nature) || (nature == null))
				{
					for (String location : ILaunchConfigurationConstants.DEFAULT_BROWSER_MACOSX_FIREFOX)
					{
						location = PlatformUtil.expandEnvironmentStrings(location);
						File file = new File(location);
						if (file.exists() && file.isDirectory())
						{
							browser = location;
							break;
						}
					}
					if (browser.length() != 0)
					{
						break;
					}
				}

				/* Safari */
				if (SAFARI.equals(nature) || (nature == null))
				{
					String path = ILaunchConfigurationConstants.DEFAULT_BROWSER_MACOSX_SAFARI;
					if (new File(path).exists())
					{
						browser = path;
						break;
					}
				}
			}
			else if (Platform.OS_LINUX.equals(Platform.getOS()))
			{
				/* Firefox */
				if (FIREFOX.equals(nature) || (nature == null))
				{
					for (String location : ILaunchConfigurationConstants.DEFAULT_BROWSER_LINUX_FIREFOX)
					{
						location = PlatformUtil.expandEnvironmentStrings(location);
						File file = new File(location);
						if (file.exists() && file.isFile())
						{
							browser = location;
							break;
						}
					}
					if (browser.length() != 0)
					{
						break;
					}
				}
			}

			/* Check configured browsers */
			Enumeration enumeration = (Enumeration) new JSLaunchConfigurationHelper()
					.getContributedAdapter(Enumeration.class);
			if (enumeration != null)
			{
				while (enumeration.hasMoreElements())
				{
					String path = (String) enumeration.nextElement();
					/* Firefox */
					if (FIREFOX.equals(nature) || (nature == null))
					{
						if (Firefox.isBrowserExecutable(path))
						{
							browser = path;
							break;
						}
					}
					/* Safari */
					if (SAFARI.equals(nature) || (nature == null))
					{
						if (path.toLowerCase().indexOf("safari") != -1) //$NON-NLS-1$
						{
							browser = path;
							break;
						}
					}
				}
			}

		}
		while (false);

		configuration.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_BROWSER_EXECUTABLE, browser);
		if (nature != null)
		{
			configuration.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_BROWSER_NATURE, nature);
		}
	}

	/**
	 * setDebugDefaults
	 * 
	 * @param configuration
	 */
	public static void setServerDefaults(ILaunchConfigurationWorkingCopy configuration)
	{
		configuration.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_START_ACTION_TYPE,
				ILaunchConfigurationConstants.DEFAULT_START_ACTION_TYPE);
		configuration.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_SERVER_TYPE,
				ILaunchConfigurationConstants.DEFAULT_SERVER_TYPE);
		configuration.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_START_PAGE_PATH, StringUtil.EMPTY);
		configuration.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_START_PAGE_URL, StringUtil.EMPTY);
		configuration.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_EXTERNAL_BASE_URL, StringUtil.EMPTY);
		configuration.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_APPEND_PROJECT_NAME, true);
	}

	/**
	 * setAdvancedDefaults
	 * 
	 * @param configuration
	 */
	public static void setAdvancedDefaults(ILaunchConfigurationWorkingCopy configuration)
	{
		configuration.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_ADVANCED_RUN_ENABLED, false);
	}

	/**
	 * setHttpDefaults
	 * 
	 * @param configuration
	 */
	public static void setHttpDefaults(ILaunchConfigurationWorkingCopy configuration)
	{
		configuration.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_HTTP_GET_QUERY, StringUtil.EMPTY);
		configuration.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_HTTP_POST_DATA, StringUtil.EMPTY);
		configuration.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_HTTP_POST_CONTENT_TYPE,
				StringUtil.EMPTY);
	}

	/**
	 * getContributedAdapter
	 * 
	 * @param object
	 * @param clazz
	 * @return Object
	 */
	private static Object getContributedAdapter(Object object, Class<?> clazz)
	{
		Object adapter = null;
		IAdapterManager manager = Platform.getAdapterManager();
		if (manager.hasAdapter(object, clazz.getName()))
		{
			adapter = manager.getAdapter(object, clazz.getName());
			if (adapter == null)
			{
				adapter = manager.loadAdapter(object, clazz.getName());
			}
		}
		return adapter;
	}

	/**
	 * getContributedAdapter
	 * 
	 * @param clazz
	 * @return Object
	 */
	private Object getContributedAdapter(Class<?> clazz)
	{
		return getContributedAdapter(this, clazz);
	}

}
