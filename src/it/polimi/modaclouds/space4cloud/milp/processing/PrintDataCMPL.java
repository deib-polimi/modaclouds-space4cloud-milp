package it.polimi.modaclouds.space4cloud.milp.processing;

import it.polimi.modaclouds.space4cloud.milp.Configuration;
import it.polimi.modaclouds.space4cloud.milp.types.DefaultDataCollection;
import it.polimi.modaclouds.space4cloud.milp.xmldatalists.SolutionList;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;

public class PrintDataCMPL extends PrintData {

	public PrintDataCMPL(String CurrFilePath, String CurrFilePathConst) {
		super(CurrFilePath, CurrFilePathConst);
	}

	@Override
	public boolean printDataFile() {
		if (!dataWasLoaded)
			return false;

		DecimalFormat doubleFormatter = doubleFormatter();

		try {
			PrintWriter out = new PrintWriter(new FileWriter(SaveFilePath));
			out.print("%CONTAINER set <");
			for (int i = 1; i <= newdatacollection.CountContainers; ++i)
				out.printf(" i%d", i);
			out.println(" >");

			out.print("%PROVIDER set <");
			for (int i = 1; i <= newdatacollection.CountProviders; ++i)
				out.printf(" p%d", i);
			out.println(" >");

			out.print("%CLASS_REQUEST set <");
			for (int i = 1; i <= newdatacollection.CountClasses; ++i)
				out.printf(" k%d", i);
			out.println(" >");

			out.print("%TYPE_VM set <");
			for (int i = 1; i <= newdatacollection.CountTypeVMs; ++i)
				out.printf(" v%d", i);
			out.println(" >");

			out.print("%TIME_INT set <");
			for (int i = 1; i <= newdatacollection.CountTimeInts; ++i)
				out.printf(" t%d", i);
			out.println(" >");

			out.print("%COMPONENT set <");
			for (int i = 1; i <= newdatacollection.CountComponents; ++i)
				out.printf(" c%d", i);
			out.println(" >");

			out.printf(
					"%%ProbabilityToBeInComponent[CLASS_REQUEST, COMPONENT] default %s <",
					doubleFormatter
							.format(DefaultDataCollection.ProbabilityToBeInComponent));
			for (int i = 1; i <= newdatacollection.CountClasses; ++i)
				for (int j = 1; j <= newdatacollection.CountComponents; ++j) {
					out.printf(
							" %s",
							doubleFormatter
									.format(newdatacollection.ProbabilityToBeInComponent[i - 1][j - 1]));
				}
			out.println(" >");

			out.printf("%%ArrRate[TIME_INT] default %s <", doubleFormatter
					.format(DefaultDataCollection.ArrRate));
			for (int i = 1; i <= newdatacollection.CountTimeInts; ++i) {
				out.printf(" %s", doubleFormatter
						.format(newdatacollection.ArrRate[i - 1]));
			}
			out.println(" >");

			out.printf("%%MaximumSR[CLASS_REQUEST, COMPONENT] default %s <", doubleFormatter
					.format(DefaultDataCollection.MaximumSR));
			for (int i = 1; i <= newdatacollection.CountClasses; ++i)
				for (int j = 1; j <= newdatacollection.CountComponents; ++j) {
					out.printf(" %s", doubleFormatter
							.format(newdatacollection.MaximumSR[i - 1][j - 1]));
				}
			out.println(" >");

			out.printf("%%PartitionComponents[COMPONENT, CONTAINER] default %d <",
					DefaultDataCollection.PartitionComponents);
			for (int i = 1; i <= newdatacollection.CountComponents; ++i)
				for (int j = 1; j <= newdatacollection.CountContainers; ++j) {
					out.printf(" %d", i, j,
							newdatacollection.PartitionComponents[i - 1][j - 1]);
				}
			out.println(" >");

			out.printf("%%Speed[TYPE_VM, PROVIDER, CONTAINER] default %s <", doubleFormatter
					.format(DefaultDataCollection.Speed));
			for (int i = 1; i <= newdatacollection.CountTypeVMs; ++i)
				for (int j = 1; j <= newdatacollection.CountProviders; ++j)
					for (int k = 1; k <= newdatacollection.CountContainers; ++k) {
						out.printf(
								" %s",
								doubleFormatter
										.format(newdatacollection.Speed[i - 1][j - 1][k - 1]));
					}
			out.println(" >");

			out.printf("%%Cost[TYPE_VM, PROVIDER, CONTAINER] default %s <", doubleFormatter
					.format(DefaultDataCollection.Cost));
			for (int i = 1; i <= newdatacollection.CountTypeVMs; ++i)
				for (int j = 1; j <= newdatacollection.CountProviders; ++j)
					for (int k = 1; k <= newdatacollection.CountContainers; ++k) {
						out.printf(
								" %s",
								doubleFormatter
										.format(newdatacollection.Cost[i - 1][j - 1][k - 1]));
					}
			out.println(" >");

			out.printf("%%MaxResponseTime[CLASS_REQUEST, COMPONENT] default %s <", doubleFormatter
					.format(DefaultDataCollection.MaxResponseTime));
			for (int i = 1; i <= newdatacollection.CountClasses; ++i)
				for (int j = 1; j <= newdatacollection.CountComponents; ++j) {
					out.printf(
							" %s",
							doubleFormatter
									.format(newdatacollection.MaxResponseTime[i - 1][j - 1]));
				}
			out.println(" >");

			out.printf("%%MinProv < %d >\n", newdatacollection.MinProv);

			out.printf("%%MaxVMPerContainer < %d >\n",
					newdatacollection.MaxVMPerContainer);

			out.printf("%%MinArrRate[PROVIDER] default %s <", doubleFormatter
					.format(DefaultDataCollection.MinArrRate));
			for (int i = 1; i <= newdatacollection.CountProviders; ++i) {
				out.printf(" %s", doubleFormatter
						.format(newdatacollection.MinArrRate[i - 1]));
			}
			out.println(" >");

			out.printf("%%Alpha[CLASS_REQUEST] default %s <", doubleFormatter
					.format(DefaultDataCollection.Alpha));
			for (int i = 1; i <= newdatacollection.CountClasses; ++i) {
				out.printf(" %s",
						doubleFormatter.format(newdatacollection.Alpha[i - 1]));
			}
			out.println(" >");

			if (initialSolution != null) { // TODO: azzz
				out.print("param AmountVM default 0 :=");

				for (SolutionList.AmountVM i : initialSolution.amounts)
					if (i.provider != -1)
						out.printf("\nv%d p%d i%d t%d %d", i.resource,
								i.provider, i.tier, i.hour, i.allocation);

				out.println(";");

				out.print("param X default 0 :=");

				for (SolutionList.X i : initialSolution.xs)
					if (i.provider != -1)
						out.printf("\np%d %d", i.provider, i.taken);

				out.println(";");

				out.print("param W default 0 :=");

				for (SolutionList.W i : initialSolution.ws)
					if (i.provider != -1)
						out.printf("\nv%d p%d i%d %d", i.resource, i.provider,
								i.tier, i.taken);

				out.println(";");

			} else if (resEnvExt != null) {
				// out.print("param AmountVM default 0 :=");
				//
				// for (SolutionList.AmountVM i : initialSolution.amounts)
				// if (i.provider != -1)
				// out.printf("\nv%d p%d i%d t%d %d", i.resource, i.provider,
				// i.tier, i.hour, i.allocation);
				//
				// out.println(";");
				//
				// out.print("param X default 0 :=");
				//
				// for (SolutionList.X i : initialSolution.xs)
				// if (i.provider != -1)
				// out.printf("\np%d %d", i.provider, i.taken);
				//
				// out.println(";");
				//
				// out.print("param W default 0 :=");
				//
				// for (SolutionList.W i : initialSolution.ws)
				// if (i.provider != -1)
				// out.printf("\nv%d p%d i%d %d", i.resource, i.provider,
				// i.tier, i.taken);
				//
				// out.println(";");

			}

			double maxUnavailability = 1 - minAvailability;

			out.printf("%%MaxUnavailability < %s >\n",
					doubleFormatter.format(maxUnavailability));

			out.printf("%%Availability[PROVIDER] default %s <", doubleFormatter
					.format(DefaultDataCollection.availability));
			for (int i = 1; i <= newdatacollection.CountProviders; ++i)
				out.printf(" %s", doubleFormatter
						.format(newdatacollection.availabilities[i - 1]));
			out.println(" >");

			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return true;
	}
	
	public static void print(Parser newparser) {
		PrintDataCMPL newprintdata = new PrintDataCMPL(Configuration.RUN_DATA_CMPL, Configuration.CONSTRAINTS);
		// transfer of parser results into converter
		newprintdata.setPrintData(newparser.newparsrepository.resRepositoryList,
				newparser.newparssql.newDBList, newparser.newparssql.resMatrix,
				newparser.newparssolution.solution, newparser.newparsresenvext.solution);
		// creating file data.dat
		newprintdata.printDataFile();
	}

}
