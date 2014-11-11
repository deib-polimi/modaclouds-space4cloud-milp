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
package it.polimi.modaclouds.space4cloud.milp;

import it.polimi.modaclouds.space4cloud.milp.processing.DataProcessing;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class Solver {
	
	private File resourceModelExt = null;
	private File solution = null;
	private File multiCloudExt = null;
	
	public Solver(String configurationFile) {
		this(configurationFile, null);
	}
	
	public Solver(String configurationFile, String initialSolution) {
		try {
			Configuration.loadConfiguration(configurationFile);
			if (initialSolution != null && Files.exists(Paths.get(initialSolution)))
				setStartingSolution(Paths.get(initialSolution).toFile());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void compute() throws MILPException {
		List<String> errors = Configuration.checkValidity(); 
		if (errors.size() == 1)
			throw new MILPException("There is 1 problem with the configuration:\n- " + errors.get(0)); 
		else if (errors.size() > 1) {
			String message = "There are " + errors.size() + " problems with the configuration:";
			for (String s : errors)
				message += "\n- " + s;
			throw new MILPException(message);
		}
		
		new DataProcessing();
		
		if (removeTempFiles)
			Configuration.deleteTempFiles();
		
		resourceModelExt = Paths.get(
				Configuration.PROJECT_BASE_FOLDER,
				Configuration.WORKING_DIRECTORY,
				Configuration.GENERATED_RESOURCE_MODEL_EXT).toFile();
		solution = Paths.get(
				Configuration.PROJECT_BASE_FOLDER,
				Configuration.WORKING_DIRECTORY,
				Configuration.GENERATED_SOLUTION).toFile();
		multiCloudExt = Paths.get(
				Configuration.PROJECT_BASE_FOLDER,
				Configuration.WORKING_DIRECTORY,
				Configuration.GENERATED_MULTI_CLOUD_EXT).toFile();
	}
	
	public static boolean removeTempFiles = true;
	
	public File getResourceModelExt() {
		if (resourceModelExt == null)
			try {
				compute();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		
		if (resourceModelExt.exists())
			return resourceModelExt;
		
		reset();
		return null;
	}
	
	public File getSolution() {
		if (solution == null)
			try {
				compute();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		
		if (solution.exists())
			return solution;
		
		reset();
		return null;
	}
	
	public File getMultiCloudExt() {
		if (multiCloudExt == null)
			try {
				compute();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		
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
		Configuration.MAP = num;
		reset();
	}
	
	private ArrayList<String> providers = new ArrayList<String>();
	
	public void setProviders(String... provider) {
		providers.clear();
		for (String p : provider)
			providers.add(p);
		
		if (providers.size() > 0) {
			Configuration.AllowedProviders = providers;
			Configuration.MAP = providers.size();
		} else {
			Configuration.AllowedProviders = null;
			Configuration.MAP = 1;
		}
		reset();
	}
	
	private ArrayList<String> regions = new ArrayList<String>();
	
	public void setRegions(String... region) {
		regions.clear();
		for (String r : region)
			regions.add(r);
		
		if (regions.size() > 0) {
			Configuration.AllowedRegions = regions;
		} else {
			Configuration.AllowedRegions = null;
		}
		reset();
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
		if (f != null && f.exists()) { //&& options.MAP > 1 && getNumOfProviders(f) == options.MAP)
			Configuration.MAP = getNumOfProviders(f);
			Configuration.FilePathStartingSolution = f.getAbsolutePath();
		} else
			Configuration.FilePathStartingSolution = null;
		
		reset();
	}
	
}
