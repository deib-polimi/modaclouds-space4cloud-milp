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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Properties;

//contains main options of the program
public class Configuration {
	
	// Information about the application
	public static String PALLADIO_USAGE_MODEL;			//Path to UsageModel Diagram
	public static String PALLADIO_REPOSITORY_MODEL;	//Path to Repository Diagram
	public static String PALLADIO_ALLOCATION_MODEL;	//Path to Allocation Diagram
	public static String PALLADIO_RESOURCE_MODEL;		//Path to ResourceEnvironment Diagram
	public static String PALLADIO_SYSTEM_MODEL;		//Path to System Diagram
	
	// Information used in the AMPL.run file
	public static String RUN_WORKING_DIRECTORY = "/home/s4c/new64"; //upload directory on AMPL server
	public static String RUN_FILE = "AMPL.run"; //sets where temp AMPL file AMPL.run will be saved
	public static String RUN_MODEL_STANDARD = "model.mod";
	public static String RUN_MODEL_STARTING_SOLUTION = "modelstartingsolution.mod";
	public static String RUN_DATA = "data.dat"; //sets where temp AMPL file data.dat will be saved 
	public static String RUN_SOLVER = "/usr/optimization/CPLEX_Studio_Preview126/cplex/bin/x86-64_linux/cplexamp";
	public static String RUN_LOG = "log.tmp";//sets where temp AMPL file log.tmp will be saved 
	public static String RUN_RES = "shortrez.out";//sets where temp AMPL file shortrez.out will be saved
	public static String DEFAULTS_BASH = "bash.run";
	
	// Information about the DB
	public static String DB_CONNECTION_FILE;

	// Information about the machine with AMPL
	public static String SSH_HOST = "ch14r4.dei.polimi.it";	//AMPL server's address (ch14r4.dei.polimi.it)
	public static String SSH_USER_NAME;						//AMPL server's login (should have access to upload directory)
	public static String SSH_PASSWORD;						//AMPL server's password
	
	public static String CONSTRAINTS;
	public static String USAGE_MODEL_EXTENSION;
	
	public static String PROJECT_BASE_FOLDER;
	public static String WORKING_DIRECTORY = "space4cloud";
	
	public static String SolverTimeLimit = "720";//time limit for CPLEX solver in seconds
	public static int[] MemoryDemand = null;
	public static double MAR = 200.0;		//max value of arrival rate (without noise)
	public static double MMAR = 0.1;		//max value of minimum proportion of arrival rate per provider (without noise)
	public static int MAP = 1;				//minimum number of providers
	public static double MSR = 0.4;			//system response time (in seconds)
	public static double Utilization = 0.6;	//Utilization

	// this function deletes all temp files
	public static void deleteTempFiles() {
		try {
			Files.deleteIfExists(Paths.get(RUN_FILE));
			Files.deleteIfExists(Paths.get(RUN_DATA));
			Files.deleteIfExists(Paths.get(RUN_RES));
			Files.deleteIfExists(Paths.get(RUN_LOG));
			Files.deleteIfExists(Paths.get(RUN_MODEL_STANDARD));
			Files.deleteIfExists(Paths.get(RUN_MODEL_STARTING_SOLUTION));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static {
		initmemorydemand(5,0);
	}
	
	public static void initmemorydemand(int count, int defval) {
		MemoryDemand = new int[count];
		for (int i = 0; i < count; i++) {
			MemoryDemand[i] = defval;
		}
	}

	public static ArrayList<String> AllowedProviders = null;
	
	public static ArrayList<String> AllowedRegions = null;
	
	public static boolean ExportAddInf = false;
	
	public static boolean ExportExtensions = false;
	
	public static String FilePathStartingSolution = null;
	
	public static String GENERATED_RESOURCE_MODEL_EXT = "generated-rme.xml";
	public static String GENERATED_MULTI_CLOUD_EXT = "generated-mce.xml";
	public static String GENERATED_SOLUTION = "generated-solution.xml";
	public static String GENERATED_ADDINF = "generated-addinf.xml";

	public static void saveConfiguration(String filePath) throws IOException{
		FileOutputStream fos = new FileOutputStream(filePath);
		Properties prop = new Properties();
		prop.put("PALLADIO_REPOSITORY_MODEL", PALLADIO_REPOSITORY_MODEL);
		prop.put("PALLADIO_SYSTEM_MODEL", PALLADIO_SYSTEM_MODEL);
		prop.put("PALLADIO_ALLOCATION_MODEL", PALLADIO_ALLOCATION_MODEL);
		prop.put("PALLADIO_USAGE_MODEL", PALLADIO_USAGE_MODEL);
		prop.put("PALLADIO_RESOURCE_MODEL", PALLADIO_RESOURCE_MODEL);
		prop.put("USAGE_MODEL_EXTENSION", USAGE_MODEL_EXTENSION);
		prop.put("CONSTRAINTS", CONSTRAINTS);
		prop.put("PROJECT_BASE_FOLDER", PROJECT_BASE_FOLDER);
		prop.put("WORKING_DIRECTORY", WORKING_DIRECTORY);
		prop.put("DB_CONNECTION_FILE", DB_CONNECTION_FILE);
		
		prop.put("SSH_HOST", SSH_HOST);
		prop.put("SSH_USER_NAME", SSH_USER_NAME);
		prop.put("SSH_PASSWORD", SSH_PASSWORD);
		
		prop.put("RUN_WORKING_DIRECTORY", RUN_WORKING_DIRECTORY);
		prop.put("RUN_MODEL_STANDARD", RUN_MODEL_STANDARD);
		prop.put("RUN_MODEL_STARTING_SOLUTION", RUN_MODEL_STARTING_SOLUTION);
		prop.put("RUN_DATA", RUN_DATA);
		prop.put("RUN_SOLVER", RUN_SOLVER);
		prop.put("RUN_FILE", RUN_FILE);
		prop.put("RUN_LOG", RUN_LOG);
		prop.put("RUN_RES", RUN_RES);
		
		prop.put("DEFAULTS_BASH", DEFAULTS_BASH);
		
		prop.store(fos, "S4C-MILP configuration properties");
		fos.flush();
	}
	
	public static void loadConfiguration(String filePath) throws IOException {
		Properties prop = new Properties();
		FileInputStream fis = new FileInputStream(filePath);
		prop.load(fis);
		PALLADIO_REPOSITORY_MODEL = prop.getProperty("PALLADIO_REPOSITORY_MODEL", PALLADIO_REPOSITORY_MODEL);
		PALLADIO_SYSTEM_MODEL = prop.getProperty("PALLADIO_SYSTEM_MODEL", PALLADIO_SYSTEM_MODEL);
		PALLADIO_ALLOCATION_MODEL = prop.getProperty("PALLADIO_ALLOCATION_MODEL", PALLADIO_ALLOCATION_MODEL);
		PALLADIO_USAGE_MODEL = prop.getProperty("PALLADIO_USAGE_MODEL", PALLADIO_USAGE_MODEL);
		PALLADIO_RESOURCE_MODEL = prop.getProperty("PALLADIO_RESOURCE_MODEL", PALLADIO_RESOURCE_MODEL);
		USAGE_MODEL_EXTENSION = prop.getProperty("USAGE_MODEL_EXTENSION", USAGE_MODEL_EXTENSION);
		CONSTRAINTS = prop.getProperty("CONSTRAINTS", CONSTRAINTS);
		PROJECT_BASE_FOLDER = prop.getProperty("PROJECT_BASE_FOLDER", PROJECT_BASE_FOLDER);
		WORKING_DIRECTORY = prop.getProperty("WORKING_DIRECTORY", WORKING_DIRECTORY);
		DB_CONNECTION_FILE= prop.getProperty("DB_CONNECTION_FILE", DB_CONNECTION_FILE);
		SSH_PASSWORD = prop.getProperty("SSH_PASSWORD", SSH_PASSWORD);
		SSH_USER_NAME = prop.getProperty("SSH_USER_NAME", SSH_USER_NAME);
		SSH_HOST = prop.getProperty("SSH_HOST", SSH_HOST);
		
		RUN_WORKING_DIRECTORY = prop.getProperty("RUN_WORKING_DIRECTORY", RUN_WORKING_DIRECTORY);
		RUN_MODEL_STANDARD = prop.getProperty("RUN_MODEL_STANDARD", RUN_MODEL_STANDARD);
		RUN_MODEL_STARTING_SOLUTION = prop.getProperty("RUN_MODEL_STARTING_SOLUTION", RUN_MODEL_STARTING_SOLUTION);
		RUN_DATA = prop.getProperty("RUN_DATA", RUN_DATA);
		RUN_SOLVER = prop.getProperty("RUN_SOLVER", RUN_SOLVER);
		RUN_FILE = prop.getProperty("RUN_FILE", RUN_FILE);
		RUN_LOG = prop.getProperty("RUN_LOG", RUN_LOG);
		RUN_RES = prop.getProperty("RUN_RES", RUN_RES);
		
		DEFAULTS_BASH = prop.getProperty("DEFAULTS_BASH", DEFAULTS_BASH);
	}
}

