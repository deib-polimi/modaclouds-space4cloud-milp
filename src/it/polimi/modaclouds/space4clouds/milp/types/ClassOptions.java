package it.polimi.modaclouds.space4clouds.milp.types;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

//contains main options of the program
public class ClassOptions {
	
	public boolean WasChanged=false;//allows to control if some options were entered by visual forms
	
	public String ModelPath="";
	public String TestPrefix="";
	public String FilePathUsage="";//Path to UsageModel Diagram
	public String FilePathRepository="";//Path to Repository Diagram
	public String FilePathAllocation="";//Path to Allocation Diagram
	public String FilePathResenv="";//Path to ResourceEnvironment Diagram
	public String FilePathSystem="";//Path to System Diagram
	public String FilePathAdditionalInformation="";//Path to AddInfFile
	public String SaveDirectory="";
	public String FilePathConstraint="";
	public String FilePathSaveData="";//sets where temp AMPL file data.dat will be saved 
	public String UploadPath="";//upload directory on AMPL server
	public String FilePathRunAMPL="";//sets where temp AMPL file AMPL.run will be saved 
	public String FilePathLogAMPL="";//sets where temp AMPL file log.tmp will be saved 
	public String FilePathResAMPL="";//sets where temp AMPL file shortrez.out will be saved 
	public String GeneratedResourceModelExt="";//sets where file with final result (with all providers) will be saved
	public String SolverTimeLimit="";//time limit for CPLEX solver in seconds
	public String SqlDBUrl="";//SQL database name (jdbc:mysql://localhost:3306/ in jdbc:mysql://localhost:3306/Cloud)
	public String DBName=""; //SQL database name (Cloud in jdbc:mysql://localhost:3306/Cloud)
	public String DBUserName="";//SQL database login
	public String DBPassword="";//SQL database password
	public String DBDriver="com.mysql.jdbc.Driver";//SQL database driver
	public String SSHhost="";//AMPL server's address (ch14r4.dei.polimi.it)
	public String SSHUserName="";//AMPL server's login (should have access to upload directory)
	public String SSHPassword="";//AMPL server's password
	public double Utilization=0;
	
	public int[] MemoryDemand=null;
	
	public double MAR = 0;
	public double MMAR = 0;
	public int MAP =0;
	public double MSR =0;

	public ClassOptions() {
		// directory path on AMPL server (not to be changed)
		// this folder contains bash-script and AMPL model
		UploadPath = "/home/specmeter/new64/";

		// this options set names of temp files
		FilePathSaveData = "data.dat";
		FilePathLogAMPL = "log.tmp";
		FilePathRunAMPL = "AMPL.run";
		FilePathResAMPL = "shortrez.out";
	}

	// this function deletes all temp files
	public void deleteTempFiles() {
		try {
			Path target1 = Paths.get(FilePathSaveData);// file data.dat for AMPL
			Files.delete(target1);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			Path target2 = Paths.get(FilePathLogAMPL);// file log.tmp of AMPL
														// and CPLEX solver
			Files.delete(target2);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			Path target3 = Paths.get(FilePathRunAMPL);// file AMPL.run
			Files.delete(target3);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			Path target4 = Paths.get(FilePathResAMPL);// file shortrez.out for
														// parsed results of
														// AMPL
			Files.delete(target4);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void initmemorydemand(int count, int defval) {
		MemoryDemand = new int[count];
		for (int i = 0; i < count; i++) {
			MemoryDemand[i] = defval;
		}
	}

	/**
	 * Did we configure the system?
	 */
	public boolean Set = false;

	public String FilePathUsageModelExt;

	public String GeneratedSolution;
	
	public String GeneratedMultiCloudExt;

	public ArrayList<String> AllowedProviders = null;
	
	public ArrayList<String> AllowedRegions = null;
	
	public boolean ExportAddInf = false;
	
	public boolean ExportExtensions = false;
	
	public String FilePathStartingSolution = null;

}

