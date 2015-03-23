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
package it.polimi.modaclouds.space4cloud.milp.processing;

import it.polimi.modaclouds.space4cloud.milp.Configuration;
import it.polimi.modaclouds.space4cloud.milp.datafiles.CreateAddInfData;
import it.polimi.modaclouds.space4cloud.milp.db.SQLParser;
import it.polimi.modaclouds.space4cloud.milp.xmldatalists.AddInfList;
import it.polimi.modaclouds.space4cloud.milp.xmlfiles.ParsAllocation;
import it.polimi.modaclouds.space4cloud.milp.xmlfiles.ParsRepository;
import it.polimi.modaclouds.space4cloud.milp.xmlfiles.ParsResEnv;
import it.polimi.modaclouds.space4cloud.milp.xmlfiles.ParsResEnvExt;
import it.polimi.modaclouds.space4cloud.milp.xmlfiles.ParsSolution;
import it.polimi.modaclouds.space4cloud.milp.xmlfiles.ParsSystem;
import it.polimi.modaclouds.space4cloud.milp.xmlfiles.ParsUsage;

import java.nio.file.Paths;
import java.util.List;

//parser class (wrapper)
//contains parsers for PCM files, SQL database and AddInfData 
public class Parser {

	// parser for SQL database
	public SQLParser newparssql = null;

	// parser for UsageModel Diagram
	public ParsUsage newparsusage = null;

	// parser for Repository Diagram
	public ParsRepository newparsrepository = null;

	// parser for Allocation Diagram
	public ParsAllocation newparsallocation = null;

	// parser for ResourceEnvironment Diagram
	public ParsResEnv newparsresenv = null;

	// parser for System Diagram
	public ParsSystem newparssystem = null;
	
	public ParsSolution newparssolution = null;
	
	public ParsResEnvExt newparsresenvext = null;

//	// parser for AdditionalInformation file
//	public ParsAddInf newaddinf = null;

	// constructor
	public Parser() throws Exception {
		run();
	}

	// main execution function
	public void run() throws Exception {
		// parsing SQL database
//		newparssql = new sqlparser(Configuration.SqlDBname,
//				Configuration.DBUserName, Configuration.DBPassword);
		newparssql = new SQLParser();
		
		// creating ethalon speed (Speed_e in the thesis)
		// it finds minimum Speed and calculates Capacities for all VM types
		newparssql.resMatrix.makeSpeedNorm();

		// constructor for parser of UsageModel Diagram
		ParsUsage newparsusage = new ParsUsage(Configuration.PALLADIO_USAGE_MODEL);
		// parsing UsageModel Diagram
		newparsusage.analyse_doc();

		// constructor for Repository Diagram
		newparsrepository = new ParsRepository(Configuration.PALLADIO_REPOSITORY_MODEL);
		// transfers results from UsageModel Diagram parser to Repository
		// Diagram parser
		newparsrepository.resRepositoryList
				.setClasses(newparsusage.resUsageList);
		// parsing Repository Diagram
		newparsrepository.analyse_doc();
		newparsrepository.resRepositoryList.U = Configuration.Utilization;

		// constructor for System Diagram parser
		ParsSystem newparssystem = new ParsSystem(Configuration.PALLADIO_SYSTEM_MODEL);
		// parsing System Diagram
		newparssystem.analyse_doc();
		// transfers results from System Diagram parser to Repository Diagram
		// parser
		newparsrepository.resRepositoryList
				.setSystemIdsandNames(newparssystem.resSystemList);

		// constructor for Allocation Diagram parser
		newparsallocation = new ParsAllocation(Configuration.PALLADIO_ALLOCATION_MODEL);
		// parsing Allocation Diagram
		newparsallocation.analyse_doc();
		// transfers results from Allocation Diagram parser to Repository
		// Diagram parser
		newparsrepository.resRepositoryList
				.setAllocationandResEnvIds(newparsallocation.resAllocationList);

		// constructor for Resource Environment Diagram parser
		newparsresenv = new ParsResEnv(Configuration.PALLADIO_RESOURCE_MODEL);
		// parsing Resource environment Diagram
		newparsresenv.analyse_doc();
		// transfers results from Resource environment Diagram parser to
		// Repository Diagram parser
		newparsrepository.resRepositoryList
				.setContainers(newparsresenv.newResEnvList);
		
		newparssolution = new ParsSolution(Configuration.FilePathStartingSolution, newparssql.resMatrix, newparsrepository.resRepositoryList);
		
		newparsresenvext = new ParsResEnvExt(Configuration.RESOURCE_ENVIRONMENT_EXTENSION, newparssql.resMatrix, newparsrepository.resRepositoryList);
		
		////////////////////
		
		// constructor for AddInfFile generator
		CreateAddInfData newcreateAddInfData = new CreateAddInfData();
		// receives list of Provider names
		List<String> newProviderList = newparssql.newDBList
				.getProviderList();
		
		// generates data for AddInfFile starting from the usagemodelext file
		newcreateAddInfData.generateData(newProviderList,
				newparssql.newDBList.countProviders, Configuration.USAGE_MODEL_EXTENSION,
				Configuration.MMAR, Configuration.MAP, Configuration.CONSTRAINTS);
		
		// saves generated data in corresponding XML file
		if (Configuration.ExportAddInf)
			newcreateAddInfData.printAddInfData(
					Paths.get(
							Configuration.PROJECT_BASE_FOLDER,
							Configuration.WORKING_DIRECTORY,
							Configuration.GENERATED_ADDINF).toString());
		
		AddInfList addInfList = newcreateAddInfData.getAddInfList();
		
		newparssql.newDBList.setAddInfData(addInfList);
		
		////////////////////

		// sets System response time border and ethalon Speed to RepositoryList
		// container
		newparsrepository.resRepositoryList
				.setMaxSystemResponseTime(addInfList.MaxSystemResponseTime); //newaddinf.newAddInfList.MaxSystemResponseTime);
		newparsrepository.resRepositoryList
				.setVMtypesProcessingRateNorm(newparssql.resMatrix.SpeedNorm);
		// calculates Maximum Service Rates and Maximum response times for
		// classes and components
		newparsrepository.resRepositoryList
				.calcMaximumServiceRateAndMaxResponseTime();
	}
}
