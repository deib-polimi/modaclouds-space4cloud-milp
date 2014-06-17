package it.polimi.modaclouds.space4clouds.milp.processing;

import it.polimi.modaclouds.space4clouds.milp.datafiles.CreateAddInfData;
import it.polimi.modaclouds.space4clouds.milp.db.SQLParser;
import it.polimi.modaclouds.space4clouds.milp.types.ClassOptions;
import it.polimi.modaclouds.space4clouds.milp.xmldatalists.AddInfList;
import it.polimi.modaclouds.space4clouds.milp.xmlfiles.ParsAllocation;
import it.polimi.modaclouds.space4clouds.milp.xmlfiles.ParsRepository;
import it.polimi.modaclouds.space4clouds.milp.xmlfiles.ParsResEnv;
import it.polimi.modaclouds.space4clouds.milp.xmlfiles.ParsSolution;
import it.polimi.modaclouds.space4clouds.milp.xmlfiles.ParsSystem;
import it.polimi.modaclouds.space4clouds.milp.xmlfiles.ParsUsage;

import java.io.File;
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

//	// parser for AdditionalInformation file
//	public ParsAddInf newaddinf = null;

	// constructor
	public Parser(ClassOptions CurrOptions) {
		run(CurrOptions);
	}

	// main execution function
	public void run(ClassOptions CurrOptions) {
		// parsing SQL database
//		newparssql = new sqlparser(CurrOptions.SqlDBname,
//				CurrOptions.DBUserName, CurrOptions.DBPassword);
		newparssql = new SQLParser(CurrOptions);
		
		// creating ethalon speed (Speed_e in the thesis)
		// it finds minimum Speed and calculates Capacities for all VM types
		newparssql.resMatrix.makeSpeedNorm();

		// constructor for parser of UsageModel Diagram
		newparsusage = new ParsUsage(CurrOptions.FilePathUsage);
		// parsing UsageModel Diagram
		newparsusage.analyse_doc();

		// constructor for Repository Diagram
		newparsrepository = new ParsRepository(CurrOptions.FilePathRepository);
		// transfers results from UsageModel Diagram parser to Repository
		// Diagram parser
		newparsrepository.resRepositoryList
				.setClasses(newparsusage.resUsageList);
		// parsing Repository Diagram
		newparsrepository.analyse_doc();
		newparsrepository.resRepositoryList.U = CurrOptions.Utilization;

		// constructor for System Diagram parser
		newparssystem = new ParsSystem(CurrOptions.FilePathSystem);
		// parsing System Diagram
		newparssystem.analyse_doc();
		// transfers results from System Diagram parser to Repository Diagram
		// parser
		newparsrepository.resRepositoryList
				.setSystemIdsandNames(newparssystem.resSystemList);

		// constructor for Allocation Diagram parser
		newparsallocation = new ParsAllocation(CurrOptions.FilePathAllocation);
		// parsing Allocation Diagram
		newparsallocation.analyse_doc();
		// transfers results from Allocation Diagram parser to Repository
		// Diagram parser
		newparsrepository.resRepositoryList
				.setAllocationandResEnvIds(newparsallocation.resAllocationList);

		// constructor for Resource Environment Diagram parser
		newparsresenv = new ParsResEnv(CurrOptions.FilePathResenv);
		// parsing Resource environment Diagram
		newparsresenv.analyse_doc();
		// transfers results from Resource environment Diagram parser to
		// Repository Diagram parser
		newparsrepository.resRepositoryList
				.setContainers(newparsresenv.newResEnvList);
		
		newparssolution = new ParsSolution(CurrOptions.FilePathStartingSolution, newparssql.resMatrix, newparsrepository.resRepositoryList);
		
		
		////////////////////
		
		// constructor for AddInfFile generator
		CreateAddInfData newcreateAddInfData = new CreateAddInfData();
		// receives list of Provider names
		List<String> newProviderList = newparssql.newDBList
				.getProviderList();
		
		if (CurrOptions.FilePathUsageModelExt != null && new File(CurrOptions.FilePathUsageModelExt).exists() &&
				CurrOptions.FilePathConstraint != null && new File(CurrOptions.FilePathConstraint).exists()) {
			// generates data for AddInfFile starting from the usagemodelext file
			newcreateAddInfData.generateData(newProviderList,
					newparssql.newDBList.countProviders, CurrOptions.FilePathUsageModelExt,
					CurrOptions.MMAR, CurrOptions.MAP, CurrOptions.FilePathConstraint);
		} else {
			// generates data for AddInfFile
			// newProviderList - list of provider names
			// newparssql.newDBList.countProviders - amount of providers
			// 100,0.1 - maximum value (without noise) of arrival rate and
			// minimum arrival rate per provider
			// 1,1 - minimum amount of providers and Maximum System Response
			// time
			newcreateAddInfData.generateData(newProviderList,
					newparssql.newDBList.countProviders, CurrOptions.MAR, CurrOptions.MMAR, CurrOptions.MAP, CurrOptions.MSR);
		}
		
		// saves generated data in corresponding XML file
		if (CurrOptions.ExportAddInf)
			newcreateAddInfData.printAddInfData(CurrOptions.FilePathAdditionalInformation);
		
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
