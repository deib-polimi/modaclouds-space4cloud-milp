package it.polimi.modaclouds.space4clouds.milp.processing;

import it.polimi.modaclouds.space4clouds.milp.datafiles.AMPLrun;
import it.polimi.modaclouds.space4clouds.milp.ssh.*;
import it.polimi.modaclouds.space4clouds.milp.types.*;
import it.polimi.modaclouds.space4clouds.milp.xmlfiles.*;

//main execution class (wrapper)
//consists of 4 different parts: parser, converter of parser results into data.dat
//SSH connector and parser for AMPL results
public class DataProcessing {

	// parser for PCM model and SQL database
	public Parser newparser = null;

	// converter for PCM and SQL parser results into data.dat
	public PrintData newprintdata = null;

	// parser for AMPL results
	public ResultXML newresultxml = null;

	// SSH connector to AMPL server
	public SshConnector newSshConnector = null;

	// constructor
	public DataProcessing(ClassOptions CurrOptions) {
		if (CurrOptions.Set)
			run(CurrOptions);
		else
			System.out.println("You did not configure the system!");
	}

	// main execution function
	public void run(ClassOptions CurrOptions) {
		// constructor for PCM and SQL parser
		// all parsing functions are called by it
		newparser = new Parser(CurrOptions);

		// constructor for converter of PCM-SQL parser results into data.dat
		newprintdata = new PrintData(CurrOptions.FilePathSaveData,
				CurrOptions.FilePathConstraint);
		// transfer of parser results into converter
		newprintdata.setPrintData(CurrOptions,
				newparser.newparsrepository.resRepositoryList,
				newparser.newparssql.newDBList, newparser.newparssql.resMatrix, newparser.newparssolution.solution);
		// creating file data.dat
		newprintdata.printDataFile();

		// creating file AMPL.run
		AMPLrun newAMPLrun = new AMPLrun();
		newAMPLrun.AMPLrunToFile(CurrOptions.FilePathRunAMPL,
				CurrOptions.SolverTimeLimit, CurrOptions.UploadPath, CurrOptions.FilePathStartingSolution);

		// constructor for SSH connector
		// creates connection to AMPL server
		// uploads data.dat and AMPL.run
		// runs bash-script on AMPL server
		// downloads log and results of AMPL
		newSshConnector = new SshConnector(CurrOptions);

		// parses AMPL log and results and saves into readable format
		newresultxml = new ResultXML(CurrOptions);
		newresultxml.printFile(newparser.newparsrepository.resRepositoryList,
				newparser.newparssql.newDBList, newparser.newparssql.resMatrix);
	}
}
