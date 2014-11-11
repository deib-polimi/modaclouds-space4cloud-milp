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
import it.polimi.modaclouds.space4cloud.milp.db.DBList;
import it.polimi.modaclouds.space4cloud.milp.types.DataCollection;
import it.polimi.modaclouds.space4cloud.milp.types.SqlBaseParsMatrix;
import it.polimi.modaclouds.space4cloud.milp.xmldatalists.RepositoryList;
import it.polimi.modaclouds.space4cloud.milp.xmldatalists.SolutionList;
import it.polimi.modaclouds.space4cloud.milp.xmlfiles.ConstraintXML;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

//this class is used to create file data.dat for AMPL solver
public class PrintData {

	// container which contains all data to be printed in "data.dat" file
	public DataCollection newdatacollection = null;

	// Path where data.dat file should be saved
	public String SaveFilePath = "";

	// Path to PCM model constraints (memory)
	public String FilePathConst = "";

	// allows to check that newdatacollection is not empty
	public boolean dataWasLoaded = false;
	
	// the starting solution
	public SolutionList solution;
	
	public double minAvailability;

	// constructor
	public PrintData(String CurrFilePath, String CurrFilePathConst) {
		SaveFilePath = CurrFilePath;
		FilePathConst = CurrFilePathConst;
	}

	// collects information from different containers:
	// CRList contains information from PCM model
	// CDBList contains information from AddInfData file
	// newMatrix contains information from SQL database
	public void setPrintData(RepositoryList CRList,
			DBList CDBList, SqlBaseParsMatrix newMatrix, SolutionList solution) {
//		@SuppressWarnings("unused")
//		createconstraintxml newccxml = new createconstraintxml(Configuration, CRList); // TODO: WTF, sovrascrive il file di constraints fornito!
		// receives constraints from FilePathConst
		ConstraintXML newconstraintxml = new ConstraintXML(
				Configuration.CONSTRAINTS);
		newconstraintxml.extractConstraints();
		CRList.initefficcheaparrays(newMatrix.y);

		newdatacollection = new DataCollection();
		newdatacollection.CountClasses = CRList.ncountClasses;
		newdatacollection.CountComponents = CRList.ncountComponents;
		newdatacollection.CountContainers = CRList.ContainerList.ncount;
		newdatacollection.CountProviders = newMatrix.y;
		newdatacollection.CountTypeVMs = newMatrix.x;
		newdatacollection.CountTimeInts = CDBList.countTimeIntervals;

		newdatacollection.initialization();
		
		minAvailability = 0.01;

		newdatacollection.MinProv = CDBList.MinProv;
		for (int i = 0; i < newdatacollection.CountTypeVMs; i++)
			for (int j = 0; j < newdatacollection.CountProviders; j++)
				for (int k = 0; k < newdatacollection.CountContainers; k++) {
					// checks that constraints were received from the file
					// and makes pre-selection of VM types
					// if VM type should not be selected, it set cost 1200 and
					// speed 0
					if (newconstraintxml.loadrez) {
						String ContainerId = CRList.ContainerList.Id[k];
						double MemoryConstraint = newconstraintxml
								.getMemoryConstById(ContainerId);
						if (MemoryConstraint <= newMatrix.MemorySize[j][i]) {
							newdatacollection.Cost[i][j][k] = newMatrix.cost[j][i];
							newdatacollection.Speed[i][j][k] = CRList.U
									* newMatrix.Speed[j][i];
							CRList.updateefficcheap(i, j, k,
									newMatrix.cost[j][i], newMatrix.Speed[j][i]);
						} else {
							newdatacollection.Cost[i][j][k] = 1200;
							newdatacollection.Speed[i][j][k] = 0;
						}
						
						minAvailability = newconstraintxml.getAvgMinAvailability();
					} else {
						newdatacollection.Cost[i][j][k] = newMatrix.cost[j][i];
						newdatacollection.Speed[i][j][k] = CRList.U
								* newMatrix.Speed[j][i];
						CRList.updateefficcheap(i, j, k, newMatrix.cost[j][i],
								newMatrix.Speed[j][i]);
					}
				}

		for (int j = 0; j < newdatacollection.CountClasses; j++)
			// newdatacollection.Alpha[j]=CRList.ProbVectorClasses[j];
			newdatacollection.Alpha[j] = 1;

		for (int i = 0; i < newdatacollection.CountClasses; i++)
			for (int j = 0; j < newdatacollection.CountComponents; j++) {
				newdatacollection.ProbabilityToBeInComponent[i][j] = CRList.ProbMatrClassesComponents[i][j];
				newdatacollection.MaximumSR[i][j] = CRList.MaximumSR[i][j];
				newdatacollection.MaxResponseTime[i][j] = CRList.MaxResponseTime[i][j];
			}

		for (int i = 0; i < newdatacollection.CountComponents; i++) {
			for (int j = 0; j < newdatacollection.CountContainers; j++)
				newdatacollection.PartitionComponents[i][j] = 0;
			int atemp = CRList.CorrespondContainerByComponent[i];
			newdatacollection.PartitionComponents[i][atemp] = 1;
		}

		for (int i = 0; i < newdatacollection.CountProviders; i++)
			newdatacollection.MinArrRate[i] = CDBList.MinArrRate[i];

		for (int i = 0; i < newdatacollection.CountTimeInts; i++)
			newdatacollection.ArrRate[i] = CDBList.arrivalrate[i];
		
		for (int i = 0; i < newdatacollection.CountProviders; i++)
			newdatacollection.availabilities[i] = CDBList.availabilities[i];
		
		this.solution = solution;
		
		dataWasLoaded = true;
	}

	// this function prints information from newdatacollection to file
	// "data.dat" for AMPL model
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

			if (solution != null) {
				out.print("param AmountVM default 0 :=");
				
				for (SolutionList.AmountVM i : solution.amounts)
					if (i.provider != -1)
						out.printf("\nv%d p%d i%d t%d %d", i.resource, i.provider, i.tier, i.hour, i.allocation);
				
				out.println(";");
				
				out.print("param X default 0 :=");
				
				for (SolutionList.X i : solution.xs)
					if (i.provider != -1)
						out.printf("\np%d %d", i.provider, i.taken);
				
				out.println(";");
				
				out.print("param W default 0 :=");
				
				for (SolutionList.W i : solution.ws)
					if (i.provider != -1)
						out.printf("\nv%d p%d i%d %d", i.resource, i.provider, i.tier, i.taken);
				
				out.println(";");
					
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
	
	private static DecimalFormat doubleFormatter() {
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.getDefault());
		otherSymbols.setDecimalSeparator('.');
//		DecimalFormat myFormatter = new DecimalFormat("0.000#######", otherSymbols);
		DecimalFormat myFormatter = new DecimalFormat("0.000", otherSymbols);
		return myFormatter;
	}
}
