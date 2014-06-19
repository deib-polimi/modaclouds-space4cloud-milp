/**
 * Copyright ${year} deib-polimi
 * Contact: deib-polimi <giovannipaolo.gibilisco@polimi.it>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package it.polimi.modaclouds.space4clouds.milp;

import it.polimi.modaclouds.space4clouds.milp.processing.DataProcessing;
import it.polimi.modaclouds.space4clouds.milp.types.ClassOptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class Solver {
	
	private File resourceModelExt = null;
	private File solution = null;
	private File multiCloudExt = null;
	private ClassOptions options;
	
	public Solver(String projectPath, String workingDirectory,
			String resourceModel, String usageModel,
			String allocationModel, String repositoryModel, String systemModel, String constraintFile, String usageModelExtFile) {
		if (!projectPath.substring(projectPath.length() - 1).equals(File.separator))
			projectPath += File.separator;
		if (!workingDirectory.substring(workingDirectory.length() - 1).equals(File.separator))
			workingDirectory += File.separator;
		
		init(projectPath, workingDirectory, resourceModel, usageModel, allocationModel, repositoryModel, systemModel, constraintFile, usageModelExtFile);
	}
	
	public Solver(String projectPath, String workingDirectory, String constraintFile, String usageModelExtFile) {
		if (!projectPath.substring(projectPath.length() - 1).equals(File.separator))
			projectPath += File.separator;
		if (!workingDirectory.substring(workingDirectory.length() - 1).equals(File.separator))
			workingDirectory += File.separator;
		
		String usageModel      = projectPath + "default.usagemodel";
		String repositoryModel = projectPath + "default.repository";
		String allocationModel = projectPath + "default.allocation";
		String resourceModel   = projectPath + "default.resourceenvironment";
		String systemModel     = projectPath + "default.system";
		
		init(projectPath, workingDirectory, resourceModel, usageModel, allocationModel, repositoryModel, systemModel, constraintFile, usageModelExtFile);
	}
	
	private void init(String projectPath, String workingDirectory,
			String resourceModel, String usageModel,
			String allocationModel, String repositoryModel, String systemModel, String constraintFile, String usageModelExtFile) {
		
		options = Config.SetConfiguration(projectPath, workingDirectory, resourceModel, usageModel, allocationModel, repositoryModel, systemModel, constraintFile, usageModelExtFile);
	}
	
	public void compute() {
		new DataProcessing(options);
		
		if (deleteTempFiles)
			options.deleteTempFiles();
		
		resourceModelExt = new File(options.GeneratedResourceModelExt);
		solution = new File(options.GeneratedSolution);
		multiCloudExt = new File(options.GeneratedMultiCloudExt);
	}
	
	private boolean deleteTempFiles = true;
	
	public void setDeleteTempFiles(boolean deleteTempFiles) {
		this.deleteTempFiles = deleteTempFiles;
	}
	
	public File getResourceModelExt() {
		if (resourceModelExt == null)
			compute();
		
		if (resourceModelExt.exists())
			return resourceModelExt;
		
		reset();
		return null;
	}
	
	public File getSolution() {
		if (solution == null)
			compute();
		
		if (solution.exists())
			return solution;
		
		reset();
		return null;
	}
	
	public File getMultiCloudExt() {
		if (multiCloudExt == null)
			compute();
		
		if (multiCloudExt.exists())
			return multiCloudExt;
		
		reset();
		return null;
	}
	
	public void reset() {
		resourceModelExt = null;
		solution = null;
		multiCloudExt = null;
	}
	
	public void setMinimumNumberOfProviders(int num) {
		options.MAP = num;
		reset();
	}
	
	private ArrayList<String> providers = new ArrayList<String>();
	
	public void setProviders(String... provider) {
		providers.clear();
		for (String p : provider)
			providers.add(p);
		
		if (providers.size() > 0) {
			options.AllowedProviders = providers;
			options.MAP = providers.size();
		} else {
			options.AllowedProviders = null;
			options.MAP = 1;
		}
		reset();
	}
	
	private ArrayList<String> regions = new ArrayList<String>();
	
	public void setRegions(String... region) {
		regions.clear();
		for (String r : region)
			regions.add(r);
		
		if (regions.size() > 0) {
			options.AllowedRegions = regions;
		} else {
			options.AllowedRegions = null;
		}
		reset();
	}
	
	public void setOptions(ArrayList<String> propertyNames, ArrayList<Object> propertyValues) {
		for (int i = 0; i < propertyNames.size(); ++i) {
			
			try {
				Field f = options.getClass().getField(propertyNames.get(i));
				f.set(options, propertyValues.get(i));
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
			
		}
		reset();
	}
	
	public void setOptionsFromFile(File f) {
		Properties conf = new Properties();
		ArrayList<String> propertyNames = new ArrayList<String>();
		ArrayList<Object> propertyValues = new ArrayList<Object>();
		
		try {
			conf.load(new FileInputStream(f.toString()));
			
			for (String name : conf.stringPropertyNames()) {
				String value = conf.getProperty(name);
				if (value != null) {
					propertyNames.add(name);
					propertyValues.add(value);
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		setOptions(propertyNames, propertyValues);
	}
	
	public ClassOptions getOptions() {
		return options;
	}
	
	private static int getNumOfProviders(File f) {
		if (f != null && f.exists()) {
			
			try {
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(f);
				doc.getDocumentElement().normalize();
				
				{
					NodeList nl = doc.getElementsByTagName("Solution");
					
					return nl.getLength();
				}
			} catch (Exception e) {
				return -1;
			}
		}
		
		return -1;
	}
	
	public void setStartingSolution(File f) {
		if (f != null && f.exists() && options.MAP > 1 && getNumOfProviders(f) == options.MAP)
			options.FilePathStartingSolution = f.getAbsolutePath();
		else
			options.FilePathStartingSolution = null;
		
		reset();
	}
	
}
