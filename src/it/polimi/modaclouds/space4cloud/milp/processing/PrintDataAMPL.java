package it.polimi.modaclouds.space4cloud.milp.processing;

import it.polimi.modaclouds.space4cloud.milp.Configuration;
import it.polimi.modaclouds.space4cloud.milp.xmldatalists.SolutionList;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;

//this class is used to create file data.dat for AMPL solver
public class PrintDataAMPL extends PrintData {

	public PrintDataAMPL(String CurrFilePath, String CurrFilePathConst) {
		super(CurrFilePath, CurrFilePathConst);
	}

	@Override
	public boolean printDataFile() {
		if (!dataWasLoaded)
			return false;
		
		DecimalFormat doubleFormatter = doubleFormatter();

		try {
			PrintWriter out = new PrintWriter(new FileWriter(SaveFilePath));
			out.print("set CONTAINER :=");
			for (int i = 1; i <= newdatacollection.CountContainers; ++i)
				out.printf(" i%d", i);
			out.println(";");

			out.print("set PROVIDER :=");
			for (int i = 1; i <= newdatacollection.CountProviders; ++i)
				out.printf(" p%d", i);
			out.println(";");

			out.print("set CLASS_REQUEST :=");
			for (int i = 1; i <= newdatacollection.CountClasses; ++i)
				out.printf(" k%d", i);
			out.println(";");

			out.print("set TYPE_VM :=");
			for (int i = 1; i <= newdatacollection.CountTypeVMs; ++i)
				out.printf(" v%d", i);
			out.println(";");

			out.print("set TIME_INT :=");
			for (int i = 1; i <= newdatacollection.CountTimeInts; ++i)
				out.printf(" t%d", i);
			out.println(";");

			out.print("set COMPONENT :=");
			for (int i = 1; i <= newdatacollection.CountComponents; ++i)
				out.printf(" c%d", i);
			out.println(";");

			out.printf("param ProbabilityToBeInComponent default %s :=",
					doubleFormatter.format(newdatacollection.defaultvalues.ProbabilityToBeInComponent));
			for (int i = 1; i <= newdatacollection.CountClasses; ++i)
				for (int j = 1; j <= newdatacollection.CountComponents; ++j) {
					out.printf("\nk%d c%d %s", i, j,
							doubleFormatter.format(newdatacollection.ProbabilityToBeInComponent[i - 1][j - 1]));
				}
			out.println(";");

			out.printf("param ArrRate default %s :=",
					doubleFormatter.format(newdatacollection.defaultvalues.ArrRate));
			for (int i = 1; i <= newdatacollection.CountTimeInts; ++i) {
				out.printf("\nt%d %s", i, doubleFormatter.format(newdatacollection.ArrRate[i - 1]));
			}
			out.println(";");

			out.printf("param MaximumSR default %s :=", 
					doubleFormatter.format(newdatacollection.defaultvalues.MaximumSR));
			for (int i = 1; i <= newdatacollection.CountClasses; ++i)
				for (int j = 1; j <= newdatacollection.CountComponents; ++j) {
					out.printf("\nk%d c%d %s", i, j,
							doubleFormatter.format(newdatacollection.MaximumSR[i - 1][j - 1]));
				}
			out.println(";");

			out.printf("param PartitionComponents default %d :=",
					newdatacollection.defaultvalues.PartitionComponents);
			for (int i = 1; i <= newdatacollection.CountComponents; ++i)
				for (int j = 1; j <= newdatacollection.CountContainers; ++j) {
					out.printf("\nc%d i%d %d", i, j,
							newdatacollection.PartitionComponents[i - 1][j - 1]);
				}
			out.println(";");

			out.printf("param Speed default %s :=",
					doubleFormatter.format(newdatacollection.defaultvalues.Speed));
			for (int i = 1; i <= newdatacollection.CountTypeVMs; ++i)
				for (int j = 1; j <= newdatacollection.CountProviders; ++j)
					for (int k = 1; k <= newdatacollection.CountContainers; ++k) {
						out.printf("\nv%d p%d i%d %s", i, j, k,
								doubleFormatter.format(newdatacollection.Speed[i - 1][j - 1][k - 1]));
					}
			out.println(";");

			out.printf("param Cost default %s :=",
					doubleFormatter.format(newdatacollection.defaultvalues.Cost));
			for (int i = 1; i <= newdatacollection.CountTypeVMs; ++i)
				for (int j = 1; j <= newdatacollection.CountProviders; ++j)
					for (int k = 1; k <= newdatacollection.CountContainers; ++k) {
						out.printf("\nv%d p%d i%d %s", i, j, k,
								doubleFormatter.format(newdatacollection.Cost[i - 1][j - 1][k - 1]));
					}
			out.println(";");

			out.printf("param MaxResponseTime default %s :=",
					doubleFormatter.format(newdatacollection.defaultvalues.MaxResponseTime));
			for (int i = 1; i <= newdatacollection.CountClasses; ++i)
				for (int j = 1; j <= newdatacollection.CountComponents; ++j) {
					out.printf("\nk%d c%d %s", i, j,
							doubleFormatter.format(newdatacollection.MaxResponseTime[i - 1][j - 1]));
				}
			out.println(";");

			out.printf("param MinProv := %d;\n", newdatacollection.MinProv);

			out.printf("param MaxVMPerContainer := %d;\n", newdatacollection.MaxVMPerContainer);

			out.printf("param MinArrRate default %s :=",
					doubleFormatter.format(newdatacollection.defaultvalues.MinArrRate));
			for (int i = 1; i <= newdatacollection.CountProviders; ++i) {
				out.printf("\np%d %s", i,
						doubleFormatter.format(newdatacollection.MinArrRate[i - 1]));
			}
			out.println(";");

			out.printf("param Alpha default %s :=",
					doubleFormatter.format(newdatacollection.defaultvalues.Alpha));
			for (int i = 1; i <= newdatacollection.CountClasses; ++i) {
				out.printf("\nk%d %s", i,
						doubleFormatter.format(newdatacollection.Alpha[i - 1]));
			}
			out.println(";");

			if (initialSolution != null) {
				out.print("param AmountVM default 0 :=");
				
				for (SolutionList.AmountVM i : initialSolution.amounts)
					if (i.provider != -1)
						out.printf("\nv%d p%d i%d t%d %d", i.resource, i.provider, i.tier, i.hour, i.allocation);
				
				out.println(";");
				
				out.print("param X default 0 :=");
				
				for (SolutionList.X i : initialSolution.xs)
					if (i.provider != -1)
						out.printf("\np%d %d", i.provider, i.taken);
				
				out.println(";");
				
				out.print("param W default 0 :=");
				
				for (SolutionList.W i : initialSolution.ws)
					if (i.provider != -1)
						out.printf("\nv%d p%d i%d %d", i.resource, i.provider, i.tier, i.taken);
				
				out.println(";");
					
			} else if (resEnvExt != null) {
//				out.print("param AmountVM default 0 :=");
//				
//				for (SolutionList.AmountVM i : initialSolution.amounts)
//					if (i.provider != -1)
//						out.printf("\nv%d p%d i%d t%d %d", i.resource, i.provider, i.tier, i.hour, i.allocation);
//				
//				out.println(";");
//				
//				out.print("param X default 0 :=");
//				
//				for (SolutionList.X i : initialSolution.xs)
//					if (i.provider != -1)
//						out.printf("\np%d %d", i.provider, i.taken);
//				
//				out.println(";");
//				
//				out.print("param W default 0 :=");
//				
//				for (SolutionList.W i : initialSolution.ws)
//					if (i.provider != -1)
//						out.printf("\nv%d p%d i%d %d", i.resource, i.provider, i.tier, i.taken);
//				
//				out.println(";");
					
			}
			
			double maxUnavailability = 1 - minAvailability;
			
			out.printf("param MaxUnavailability := %s;\n",
					doubleFormatter.format(maxUnavailability));
			
			out.printf("param Availability default %s :=",
					doubleFormatter.format(newdatacollection.defaultvalues.availability));
			for (int i = 1; i <= newdatacollection.CountProviders; ++i)
				out.printf("\np%d %s", i,
						doubleFormatter.format(newdatacollection.availabilities[i - 1]));
			out.println(";");

			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return true;
	}
	
	public static void print(Parser newparser) {
		PrintDataAMPL newprintdata = new PrintDataAMPL(Configuration.RUN_DATA, Configuration.CONSTRAINTS);
		// transfer of parser results into converter
		newprintdata.setPrintData(newparser.newparsrepository.resRepositoryList,
				newparser.newparssql.newDBList, newparser.newparssql.resMatrix,
				newparser.newparssolution.solution, newparser.newparsresenvext.solution);
		// creating file data.dat
		newprintdata.printDataFile();
	}
	
}