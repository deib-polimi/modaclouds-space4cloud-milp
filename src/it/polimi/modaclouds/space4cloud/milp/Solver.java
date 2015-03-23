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
import it.polimi.modaclouds.space4cloud.milp.xmlfiles.ParsResEnvExt;
import it.polimi.modaclouds.space4cloud.milp.xmlfiles.ParsSolution;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
			
			setStartingResEnvExt();
			if (initialSolution != null && Files.exists(Paths.get(initialSolution)))
				setStartingSolution(Paths.get(initialSolution).toFile());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String getDate() {
		Calendar c = Calendar.getInstance();
		
		DecimalFormat f = new DecimalFormat("00");
		
		return String.format("%d%s%s-%s%s%s",
				c.get(Calendar.YEAR),
				f.format(c.get(Calendar.MONTH) + 1),
				f.format(c.get(Calendar.DAY_OF_MONTH)),
				f.format(c.get(Calendar.HOUR_OF_DAY)),
				f.format(c.get(Calendar.MINUTE)),
				f.format(c.get(Calendar.SECOND))
				);
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
		
		try {
			Files.createDirectories(Paths.get(
					Configuration.PROJECT_BASE_FOLDER,
					Configuration.WORKING_DIRECTORY));
		} catch (IOException e) {
			throw new MILPException("Error with dealing with the output folder: " + Paths.get(
					Configuration.PROJECT_BASE_FOLDER,
					Configuration.WORKING_DIRECTORY).toString());
		}
		
		Configuration.RUN_WORKING_DIRECTORY = Configuration.DEFAULTS_WORKING_DIRECTORY + "/" + getDate();
		
		try {
			new DataProcessing();
		} catch (Exception e) {
			throw new MILPException("Error when sending or receiving file or when executing the script.", e);
		}
		
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
	
	public File getResourceModelExt() throws MILPException {
		if (resourceModelExt == null)
			compute();
		
		if (resourceModelExt.exists())
			return resourceModelExt;
		
		reset();
		return null;
	}
	
	public File getSolution() throws MILPException {
		if (solution == null)
			compute();
		
		if (solution.exists())
			return solution;
		
		reset();
		return null;
	}
	
	public File getMultiCloudExt() throws MILPException {
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
	
	public void setStartingSolution(File f) {
		if (f != null && f.exists()) { //&& options.MAP > 1 && getNumOfProviders(f) == options.MAP)
			ArrayList<String> providers = ParsSolution.getProviders(f);
			Configuration.AllowedProviders = providers;
			Configuration.MAP = providers.size();
			Configuration.FilePathStartingSolution = f.getAbsolutePath();
		} else
			Configuration.FilePathStartingSolution = null;
		
		reset();
	}
	
	public void setStartingResEnvExt() {
		File f = Paths.get(Configuration.RESOURCE_ENVIRONMENT_EXTENSION).toFile();
		
		if (f != null && f.exists()) { //&& options.MAP > 1 && getNumOfProviders(f) == options.MAP)
			ArrayList<String> providers = ParsResEnvExt.getProviders(f);
			Configuration.AllowedProviders = providers;
			Configuration.MAP = providers.size();
			Configuration.RESOURCE_ENVIRONMENT_EXTENSION = f.getAbsolutePath();
		} else
			Configuration.RESOURCE_ENVIRONMENT_EXTENSION = null;
		
		reset();
	}
	
}
