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

import it.polimi.modaclouds.qos_models.schema.CloudService;
import it.polimi.modaclouds.qos_models.schema.ResourceContainer;
import it.polimi.modaclouds.qos_models.schema.ResourceModelExtension;
import it.polimi.modaclouds.qos_models.util.XMLHelper;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//contains main options of the program
public class Configuration {

	private static final Logger logger = LoggerFactory.getLogger(Configuration.class);

	// Information about the application
	public static String PALLADIO_USAGE_MODEL;			//Path to UsageModel Diagram
	public static String PALLADIO_REPOSITORY_MODEL;	//Path to Repository Diagram
	public static String PALLADIO_ALLOCATION_MODEL;	//Path to Allocation Diagram
	public static String PALLADIO_RESOURCE_MODEL;		//Path to ResourceEnvironment Diagram
	public static String PALLADIO_SYSTEM_MODEL;		//Path to System Diagram

	public static final String SUFFIX = "-MILP";

	public static InputStream getStream(String file) {
		InputStream res = Configuration.class.getResourceAsStream("/" + file + SUFFIX);
		if (res == null)
			res = Configuration.class.getResourceAsStream("/" + file);
		return res;
	}

	// Information used in the AMPL.run file
	public static String DEFAULTS_WORKING_DIRECTORY = "/tmp/s4c"; //upload directory on AMPL server
	public static final String DEFAULTS_WORKING_DIRECTORY_SUFFIX = "milp";
	public static String RUN_WORKING_DIRECTORY = DEFAULTS_WORKING_DIRECTORY;

	public static String LOCAL_TEMPORARY_FOLDER;

	public static void setWorkingSubDirectory(String date) {
		if (isRunningLocally())
			RUN_WORKING_DIRECTORY = LOCAL_TEMPORARY_FOLDER;
		else
			RUN_WORKING_DIRECTORY = DEFAULTS_WORKING_DIRECTORY + "/" + DEFAULTS_WORKING_DIRECTORY_SUFFIX + "/" + date;
	}

	public static final String RUN_FILE = "AMPL.run"; //sets where temp AMPL file AMPL.run will be saved
	public static final String RUN_MODEL_STANDARD = "model.mod";
	public static final String RUN_MODEL_STARTING_SOLUTION = "modelstartingsolution.mod";
	public static final String RUN_DATA = "data.dat"; //sets where temp AMPL file data.dat will be saved
	public static String RUN_SOLVER = "/usr/optimization/cplex-studio/cplex/bin/x86-64_linux/cplexamp";
	public static String RUN_AMPL_FOLDER = "/usr/optimization/ampl";
	public static final String RUN_LOG = "solution.log"; //"log.tmp";//sets where temp AMPL file log.tmp will be saved
	public static final String RUN_RES = "solution.sol"; //"shortrez.out";//sets where temp AMPL file shortrez.out will be saved
	public static final String DEFAULTS_BASH = "bashAMPL.run";

	public static final String RUN_FILE_CMPL = "CMPL.run";
	public static final String RUN_MODEL_STANDARD_CMPL = "model.cmpl";
	public static final String RUN_MODEL_STARTING_SOLUTION_CMPL = "modelstartingsolution.cmpl";
	public static String RUN_SOLVER_CMPL = "cbc"; // glpk, cbc, scip, gurobi, cplex
	public static final String RUN_DATA_CMPL = "data.cdat";
	public static String RUN_CMPL_FOLDER = "/usr/share/Cmpl";
	public static final String RUN_LOG_CMPL = "solution.log";
	public static final String RUN_RES_CMPL = "solution.sol";
	public static final String DEFAULTS_BASH_CMPL = "bashCMPL.run";
	public static int CMPL_THREADS = 4;

	public static Solver MATH_SOLVER = Solver.CMPL;

	// Information about the DB
	public static String DB_CONNECTION_FILE;

	// Information about the machine with AMPL
	public static String SSH_HOST = "specclient1.dei.polimi.it";	//AMPL server's address (ch14r4.dei.polimi.it)
	public static String SSH_USER_NAME;						//AMPL server's login (should have access to upload directory)
	public static String SSH_PASSWORD;						//AMPL server's password

	public static String CONSTRAINTS;
	public static String USAGE_MODEL_EXTENSION;
	public static String RESOURCE_ENVIRONMENT_EXTENSION;

	public static String PROJECT_BASE_FOLDER;
	public static String WORKING_DIRECTORY = "space4cloud";

	public static String SolverTimeLimit = "720";//time limit for CPLEX solver in seconds
	public static double MAR = 200.0;		//max value of arrival rate (without noise)
	public static double MMAR = 0.1;		//max value of minimum proportion of arrival rate per provider (without noise)
	public static int MAP = 1;				//minimum number of providers
	public static double MSR = 0.4;			//system response time (in seconds)
	public static double Utilization = 0.6;	//Utilization

	public static Benchmark BENCHMARK = Benchmark.None;

	// Benchmarks
	public static enum Benchmark {
		None, DaCapo, Filebench;

		public static Benchmark getById(int id) {
			Benchmark[] values = Benchmark.values();
			if (id < 0)
				id = 0;
			else if (id >= values.length)
				id = values.length - 1;
			return values[id];
		}

		public static int size() {
			return Benchmark.values().length;
		}

	}

	// this function deletes all temp files
	public static void deleteTempFiles() {
		try {
			Files.deleteIfExists(Paths.get(LOCAL_TEMPORARY_FOLDER, RUN_FILE));
			Files.deleteIfExists(Paths.get(LOCAL_TEMPORARY_FOLDER, RUN_DATA));
			Files.deleteIfExists(Paths.get(LOCAL_TEMPORARY_FOLDER, RUN_RES));
			Files.deleteIfExists(Paths.get(LOCAL_TEMPORARY_FOLDER, RUN_LOG));
			Files.deleteIfExists(Paths.get(LOCAL_TEMPORARY_FOLDER, RUN_MODEL_STANDARD));
			Files.deleteIfExists(Paths.get(LOCAL_TEMPORARY_FOLDER, RUN_MODEL_STARTING_SOLUTION));
			Files.deleteIfExists(Paths.get(LOCAL_TEMPORARY_FOLDER, DEFAULTS_BASH));

			Files.deleteIfExists(Paths.get(LOCAL_TEMPORARY_FOLDER, RUN_FILE_CMPL));
			Files.deleteIfExists(Paths.get(LOCAL_TEMPORARY_FOLDER, RUN_DATA_CMPL));
			Files.deleteIfExists(Paths.get(LOCAL_TEMPORARY_FOLDER, RUN_RES_CMPL));
			Files.deleteIfExists(Paths.get(LOCAL_TEMPORARY_FOLDER, RUN_LOG_CMPL));
			Files.deleteIfExists(Paths.get(LOCAL_TEMPORARY_FOLDER, RUN_MODEL_STANDARD_CMPL));
			Files.deleteIfExists(Paths.get(LOCAL_TEMPORARY_FOLDER, RUN_MODEL_STARTING_SOLUTION_CMPL));
			Files.deleteIfExists(Paths.get(LOCAL_TEMPORARY_FOLDER, DEFAULTS_BASH_CMPL));

			Files.deleteIfExists(Paths.get(LOCAL_TEMPORARY_FOLDER));
		} catch (IOException e) {
			logger.error("Error while deleting the temporary files.", e);
		}
	}

	public static ArrayList<String> AllowedProviders = null;

	public static ArrayList<String> AllowedRegions = null;

	public static boolean ExportAddInf = false;

	public static boolean ExportExtensions = false;

	public static String FilePathStartingSolution = null;

	public static final String GENERATED_RESOURCE_MODEL_EXT = "generated-rme.xml";
	public static final String GENERATED_MULTI_CLOUD_EXT = "generated-mce.xml";
	public static final String GENERATED_SOLUTION = "generated-solution.xml";
	public static final String GENERATED_ADDINF = "generated-addinf.xml";

	public static enum Solver {
		AMPL("AMPL"), CMPL("CMPL");

		private String name;

		private Solver(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public static Solver getById(int id) {
			Solver[] values = Solver.values();
			if (id < 0)
				id = 0;
			else if (id >= values.length)
				id = values.length - 1;
			return values[id];
		}

		public static int size() {
			return Solver.values().length;
		}

		public static Solver getByName(String name) {
			Solver[] values = Solver.values();
			for (Solver s : values)
				if (s.name.equals(name))
					return s;
			return values[0];
		}

	}

	public static void addToConfiguration(Properties prop) throws IOException {
		prop.put("RUN_SOLVER", RUN_SOLVER);
		prop.put("RUN_AMPL_FOLDER", RUN_AMPL_FOLDER);

		prop.put("RUN_SOLVER_CMPL", RUN_SOLVER_CMPL);
		prop.put("RUN_CMPL_FOLDER", RUN_CMPL_FOLDER);
		prop.put("CMPL_THREADS", String.valueOf(CMPL_THREADS));

		prop.put("MATH_SOLVER", MATH_SOLVER.getName());

		prop.put("DEFAULTS_WORKING_DIRECTORY", DEFAULTS_WORKING_DIRECTORY);
	}

	public static void saveConfiguration(String filePath) throws IOException{
		FileOutputStream fos = new FileOutputStream(filePath);
		Properties prop = new Properties();
		prop.put("PALLADIO_REPOSITORY_MODEL", PALLADIO_REPOSITORY_MODEL);
		prop.put("PALLADIO_SYSTEM_MODEL", PALLADIO_SYSTEM_MODEL);
		prop.put("PALLADIO_ALLOCATION_MODEL", PALLADIO_ALLOCATION_MODEL);
		prop.put("PALLADIO_USAGE_MODEL", PALLADIO_USAGE_MODEL);
		prop.put("PALLADIO_RESOURCE_MODEL", PALLADIO_RESOURCE_MODEL);
		prop.put("USAGE_MODEL_EXTENSION", USAGE_MODEL_EXTENSION);
		prop.put("RESOURCE_ENVIRONMENT_EXTENSION", RESOURCE_ENVIRONMENT_EXTENSION);
		prop.put("CONSTRAINTS", CONSTRAINTS);
		prop.put("PROJECT_BASE_FOLDER", PROJECT_BASE_FOLDER);
		prop.put("WORKING_DIRECTORY", WORKING_DIRECTORY);
		prop.put("DB_CONNECTION_FILE", DB_CONNECTION_FILE);

		prop.put("SSH_HOST", SSH_HOST);
		prop.put("SSH_USER_NAME", SSH_USER_NAME);
		prop.put("SSH_PASSWORD", SSH_PASSWORD);

		prop.put("DEFAULTS_WORKING_DIRECTORY", DEFAULTS_WORKING_DIRECTORY);

		prop.put("RUN_SOLVER", RUN_SOLVER);
		prop.put("RUN_AMPL_FOLDER", RUN_AMPL_FOLDER);

		prop.put("RUN_SOLVER_CMPL", RUN_SOLVER_CMPL);
		prop.put("RUN_CMPL_FOLDER", RUN_CMPL_FOLDER);
		prop.put("CMPL_THREADS", String.valueOf(CMPL_THREADS));

		prop.put("MATH_SOLVER", MATH_SOLVER.getName());

		prop.put("BENCHMARK", BENCHMARK.toString());

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
		RESOURCE_ENVIRONMENT_EXTENSION = prop.getProperty("RESOURCE_ENVIRONMENT_EXTENSION", RESOURCE_ENVIRONMENT_EXTENSION);
		CONSTRAINTS = prop.getProperty("CONSTRAINTS", CONSTRAINTS);
		PROJECT_BASE_FOLDER = prop.getProperty("PROJECT_BASE_FOLDER", PROJECT_BASE_FOLDER);
		WORKING_DIRECTORY = prop.getProperty("WORKING_DIRECTORY", WORKING_DIRECTORY);
		DB_CONNECTION_FILE= prop.getProperty("DB_CONNECTION_FILE", DB_CONNECTION_FILE);
		SSH_PASSWORD = prop.getProperty("SSH_PASSWORD", SSH_PASSWORD);
		SSH_USER_NAME = prop.getProperty("SSH_USER_NAME", SSH_USER_NAME);
		SSH_HOST = prop.getProperty("SSH_HOST", SSH_HOST);

		DEFAULTS_WORKING_DIRECTORY = prop.getProperty("DEFAULTS_WORKING_DIRECTORY", DEFAULTS_WORKING_DIRECTORY);
		RUN_SOLVER = prop.getProperty("RUN_SOLVER", RUN_SOLVER);
		RUN_AMPL_FOLDER = prop.getProperty("RUN_AMPL_FOLDER", RUN_AMPL_FOLDER);

		RUN_SOLVER_CMPL = prop.getProperty("RUN_SOLVER_CMPL", RUN_SOLVER_CMPL);
		RUN_CMPL_FOLDER = prop.getProperty("RUN_CMPL_FOLDER", RUN_CMPL_FOLDER);
		try {
			CMPL_THREADS = Integer.parseInt(prop.getProperty("CMPL_THREADS", String.valueOf(CMPL_THREADS)));
		} catch (Exception e) { }

		MATH_SOLVER = Solver.getByName(prop.getProperty("MATH_SOLVER", MATH_SOLVER.getName()));

		try {
			LOCAL_TEMPORARY_FOLDER = Files.createTempDirectory(DEFAULTS_WORKING_DIRECTORY_SUFFIX).toString();
		} catch (Exception e) {
			logger.error("Error while creating a temporary folder.", e);
			LOCAL_TEMPORARY_FOLDER = ".";
		}

		BENCHMARK = Benchmark.valueOf(prop.getProperty("BENCHMARK", BENCHMARK.toString()));
	}

	/**
	 * Checks if the configuration is valid returning a list of errors
	 * @return
	 */
	public static List<String> checkValidity() {
		ArrayList<String> errors = new ArrayList<String>();

		//check Palladio Model Files
		if(fileNotSpecifiedORNotExist(PALLADIO_REPOSITORY_MODEL))
			errors.add("The palladio repository model has not been specified");
		if(fileNotSpecifiedORNotExist(PALLADIO_SYSTEM_MODEL))
			errors.add("The palladio system model has not been specified");
		if(fileNotSpecifiedORNotExist(PALLADIO_RESOURCE_MODEL))
			errors.add("The palladio resource environment model has not been specified");
		if(fileNotSpecifiedORNotExist(PALLADIO_ALLOCATION_MODEL))
			errors.add("The palladio allocation model has not been specified");
		if(fileNotSpecifiedORNotExist(PALLADIO_USAGE_MODEL))
			errors.add("The palladio usage model has not been specified");
		//check extensions
		if(fileNotSpecifiedORNotExist(USAGE_MODEL_EXTENSION))
			errors.add("The usage model extension has not been specified");
		if(fileNotSpecifiedORNotExist(RESOURCE_ENVIRONMENT_EXTENSION))
			errors.add("The resource environment extension has not been specified");
		if(fileNotSpecifiedORNotExist(CONSTRAINTS))
			errors.add("The constraint file has not been specified");
		//check functionality and the solver
		if(fileNotSpecifiedORNotExist(DB_CONNECTION_FILE))
			errors.add("The database connection file has not been specified");

		if(SSH_HOST==null|| SSH_HOST.isEmpty())
			errors.add("The host for SSH connection has to be provided to perform the initial solution generation");
		else if (!Configuration.isRunningLocally()) {
			if(SSH_USER_NAME==null|| SSH_USER_NAME.isEmpty())
				errors.add("The user name for SSH connection has to be provided to perform the initial solution generation");
			if(SSH_PASSWORD==null|| SSH_PASSWORD.isEmpty())
				errors.add("The password for SSH connection has to be provided to perform the initial solution generation");
		}

		return errors;
	}

	private static boolean fileNotSpecifiedORNotExist(String filePath){
		return filePath == null || filePath.isEmpty() || !Paths.get(filePath).toFile().exists();
	}

	public static boolean isRunningLocally() {
		return (SSH_HOST.equals("localhost") || SSH_HOST.equals("127.0.0.1"));
	}

	public static boolean usesPaaS() {
		try {
			ResourceModelExtension rme = XMLHelper.deserialize(Paths.get(RESOURCE_ENVIRONMENT_EXTENSION).toUri().toURL(),ResourceModelExtension.class);

			for (ResourceContainer rc : rme.getResourceContainer()) {
				CloudService resource = rc.getCloudElement();
				String serviceType = resource.getServiceType();
				String serviceCategory = resource.getServiceCategory();

				if (serviceCategory != null && serviceCategory.equals("PaaS"))
					return true;
				if (serviceType != null && !serviceType.equals("Compute"))
					return true;
			}
		} catch (Exception e) {
			logger.error("Error while checking if the solution uses PaaS.", e);
			return false;
		}

		return false;
	}
}

