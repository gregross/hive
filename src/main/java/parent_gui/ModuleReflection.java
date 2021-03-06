/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is HIVE .
 *
 * The Initial Developer of the Original Code is
 * Greg Ross.
 * Portions created by the Initial Developer are Copyright (C) 2000-2004
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s): Greg Ross <gr@dcs.gla.ac.uk>
 		   Matthew Chalmers <matthew@dcs.gla.ac.uk>
 *                 Alistair Morrison <morrisaj@dcs.gla.ac.uk>
 *		   Andrew Didsbury
 *           		
 *	
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** */
/**
 * Algorithmic testbed
 *
 * ModuleReflection
 *
 * This class examines the atb/alg directory and loads
 * all VisualModule classes.
 *
 *  @author Greg Ross
 */
package parent_gui;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.lang.reflect.Constructor;

import java.io.FileInputStream;

public class ModuleReflection
{
	// An array of VisualModule Classess
	
	private ArrayList visMods;
	
	// A HashMap of VisualModule Classes
	
	private HashMap classMap;
	
	// An array of VisualModule Objects
	
	private ArrayList modules;
	private Mdi mdiForm;
	
	public ModuleReflection(Mdi mdiForm)
	{
		this.mdiForm = mdiForm;
		loadModules();
	}
	
	/**
	* Examine the atb/alg directory to look for VisualModule class files
	*/
	
	public void loadModules()
	{
		visMods = new ArrayList();
		classMap = new HashMap();
		String[] filenameArray;
		File f = new File("target/classes/alg");
     	filenameArray = f.list();
		
		for (int i = 0; i < filenameArray.length; i++)
		{
			// Is the file a .class?
			
			Class c;
			
			// Remove the .class extension
			
			int index = filenameArray[i].indexOf(".class");
			
			if (index > -1)
			{
				// Use the reflection API to determine whether
				// the class is a VisualModule
				
				String className = filenameArray[i].substring(0, index);
				
				try
				{
					// One way of loading a class:
					
					//c = Class.forName("atb.alg." + className);
					
					// A second way of loading a class:
					
					//c = Thread.currentThread().getContextClassLoader().loadClass( "atb.alg." + className );
					
					// Third, custom class loading:
					
					CustomClassLoader customClassLoader = new CustomClassLoader();
					c = customClassLoader.loadClass("alg." + className);
					
					// The class's superclass is DefaultVisualModule or HybridAdaptor
					// then the class represents a VisualModule
					
					Class superClass = c.getSuperclass();
					String superClassName = superClass.getName();
					
					if (superClassName.indexOf("HybridAdaptor") > -1)
					{
						visMods.add(c);
						classMap.put(className, c);
					}
					else if (superClassName.equals("parent_gui.DefaultVisualModule"))
					{
						visMods.add(c);
						classMap.put(className, c);
					}
				}
				catch(Exception e){}
			}
		}
		
		// If any VisualModule classes have been found, instantiate them
		
		instantiateVisMods();
	}
	
	private class CustomClassLoader extends ClassLoader
	{
		public CustomClassLoader()
		{
			super();
		}
		
		public Class findClass(String name) throws ClassNotFoundException
		{
			FileInputStream fi = null;
			
			try
			{
				// load the class data from the connection
				
				String path = name.replace('.', '/');
				fi = new FileInputStream(path + ".class");
				byte[] classBytes = new byte[fi.available()];
				fi.read(classBytes);
				Class c =  defineClass(name, classBytes, 0, classBytes.length);
				resolveClass(c);
				
				return c; 
			}
			catch (Exception e){return null;}
		}
	}
	
	/**
	* If atb/alg contains any VisualModule classes, instantiate them
	*/
	
	private void instantiateVisMods()
	{
		if (visMods.size() > 0)
		{
			DrawingCanvas drawPane;
			
			if (mdiForm.getModuleVP() != null)
				drawPane = mdiForm.getModuleVP().getDrawingCanvas();
			else
			{
				DrawingForm form = new DrawingForm("Modules", mdiForm);
				drawPane = form.getDrawPane();
			}
			
			modules = new ArrayList(visMods.size());
			
			for (int i = 0; i < visMods.size(); i++)
			{
				try
				{
					Class c = ((Class)visMods.get(i));
					Class[] intArgsClass = new Class[] {mdiForm.getClass(), drawPane.getClass()};
					Constructor cons = c.getConstructor(intArgsClass);
					Object[] intArgs = new Object[] {mdiForm, drawPane};
					modules.add(cons.newInstance(intArgs));
				}
				catch (Exception e){}
			}
		}
	}
	
	/**
	* Return the array list of VisualModules
	*/
	
	public ArrayList getModules()
	{
		return modules;
	}
	
	/**
	* Return the HashMap of classes
	*/
	
	public HashMap getClasses()
	{
		return classMap;
	}
}
