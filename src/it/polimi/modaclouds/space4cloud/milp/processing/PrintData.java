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
						String MemoryConstraintStr = newconstraintxml
								.getMemoryConstById(ContainerId);
						double MemoryConstraint = Double
								.parseDouble(MemoryConstraintStr);
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
		
		this.solution = solution;
		
		dataWasLoaded = true;
	}

	// this function prints information from newdatacollection to file
	// "data.dat" for AMPL model
	public boolean printDataFile() {
		if (!dataWasLoaded)
			return false;

		try {
			PrintWriter out = new PrintWriter(new FileWriter(SaveFilePath));
			out.print("set CONTAINER := ");
			for (int i = 1; i < newdatacollection.CountContainers; i++)
				out.print("i" + i + " ");
			out.println("i" + newdatacollection.CountContainers + ";");

			out.print("set PROVIDER := ");
			for (int i = 1; i < newdatacollection.CountProviders; i++)
				out.print("p" + i + " ");
			out.println("p" + newdatacollection.CountProviders + ";");

			out.print("set CLASS_REQUEST := ");
			for (int i = 1; i < newdatacollection.CountClasses; i++)
				out.print("k" + i + " ");
			out.println("k" + newdatacollection.CountClasses + ";");

			out.print("set TYPE_VM := ");
			for (int i = 1; i < newdatacollection.CountTypeVMs; i++)
				out.print("v" + i + " ");
			out.println("v" + newdatacollection.CountTypeVMs + ";");

			out.print("set TIME_INT := ");
			for (int i = 1; i < newdatacollection.CountTimeInts; i++)
				out.print("t" + i + " ");
			out.println("t" + newdatacollection.CountTimeInts + ";");

			out.print("set COMPONENT := ");
			for (int i = 1; i < newdatacollection.CountComponents; i++)
				out.print("c" + i + " ");
			out.println("c" + newdatacollection.CountComponents + ";");

			out.print("param ProbabilityToBeInComponent default "
					+ newdatacollection.defaultvalues.ProbabilityToBeInComponent
					+ " :=");
			for (int i = 1; i < newdatacollection.CountClasses + 1; i++)
				for (int j = 1; j < newdatacollection.CountComponents + 1; j++) {
					out.println();
					out.print("k"
							+ i
							+ " c"
							+ j
							+ " "
							+ newdatacollection.ProbabilityToBeInComponent[i - 1][j - 1]);
				}
			out.println(";");

			out.print("param ArrRate default "
					+ newdatacollection.defaultvalues.ArrRate + " :=");
			for (int i = 1; i < newdatacollection.CountTimeInts + 1; i++) {
				out.println();
				out.print("t" + i + " " + newdatacollection.ArrRate[i - 1]);
			}
			out.println(";");

			out.print("param MaximumSR default "
					+ newdatacollection.defaultvalues.MaximumSR + " :=");
			for (int i = 1; i < newdatacollection.CountClasses + 1; i++)
				for (int j = 1; j < newdatacollection.CountComponents + 1; j++) {
					out.println();
					out.print("k" + i + " c" + j + " "
							+ newdatacollection.MaximumSR[i - 1][j - 1]);
				}
			out.println(";");

			out.print("param PartitionComponents default "
					+ newdatacollection.defaultvalues.PartitionComponents
					+ " :=");
			for (int i = 1; i < newdatacollection.CountComponents + 1; i++)
				for (int j = 1; j < newdatacollection.CountContainers + 1; j++) {
					out.println();
					out.print("c"
							+ i
							+ " i"
							+ j
							+ " "
							+ newdatacollection.PartitionComponents[i - 1][j - 1]);
				}
			out.println(";");

			out.print("param Speed default "
					+ newdatacollection.defaultvalues.Speed + " :=");
			for (int i = 1; i < newdatacollection.CountTypeVMs + 1; i++)
				for (int j = 1; j < newdatacollection.CountProviders + 1; j++)
					for (int k = 1; k < newdatacollection.CountContainers + 1; k++) {
						out.println();
						out.print("v" + i + " p" + j + " i" + k + " "
								+ newdatacollection.Speed[i - 1][j - 1][k - 1]);
					}
			out.println(";");

			out.print("param Cost default "
					+ newdatacollection.defaultvalues.Cost + " :=");
			for (int i = 1; i < newdatacollection.CountTypeVMs + 1; i++)
				for (int j = 1; j < newdatacollection.CountProviders + 1; j++)
					for (int k = 1; k < newdatacollection.CountContainers + 1; k++) {
						out.println();
						out.print("v" + i + " p" + j + " i" + k + " "
								+ newdatacollection.Cost[i - 1][j - 1][k - 1]);
					}
			out.println(";");

			out.print("param MaxResponseTime default "
					+ newdatacollection.defaultvalues.MaxResponseTime + " :=");
			for (int i = 1; i < newdatacollection.CountClasses + 1; i++)
				for (int j = 1; j < newdatacollection.CountComponents + 1; j++) {
					out.println();
					out.print("k" + i + " c" + j + " "
							+ newdatacollection.MaxResponseTime[i - 1][j - 1]);
				}
			out.println(";");

			out.println("param MinProv := " + newdatacollection.MinProv + ";");

			out.println("param MaxVMPerContainer := "
					+ newdatacollection.MaxVMPerContainer + ";");

			out.print("param MinArrRate default "
					+ newdatacollection.defaultvalues.MinArrRate + " :=");
			for (int i = 1; i < newdatacollection.CountProviders + 1; i++) {
				out.println();
				out.print("p" + i + " " + newdatacollection.MinArrRate[i - 1]);
			}
			out.println(";");

			out.print("param Alpha default "
					+ newdatacollection.defaultvalues.Alpha + " :=");
			for (int i = 1; i < newdatacollection.CountClasses + 1; i++) {
				out.println();
				out.print("k" + i + " " + newdatacollection.Alpha[i - 1]);
			}
			out.println(";");

			if (solution != null) {
				
//				out.println("let {v in TYPE_VM, p in PROVIDER, i in CONTAINER, t in TIME_INT}");
//				out.println("    AmountVM[v,p,i,t] := 0;");
//				
//				for (SolutionList.AmountVM i : solution.amounts)
//					if (i.provider != -1)
//						out.printf("let AmountVM['v%d','p%d','i%d','t%d'] := %d;\n", i.resource, i.provider, i.tier, i.hour, i.allocation);
//				
//				out.println("fix AmountVM;");
//				
//				out.println("let {p in PROVIDER}");
//				out.println("    X[p] := 0;");
//				
//				for (SolutionList.X i : solution.xs)
//					if (i.provider != -1)
//						out.printf("let X['p%d'] := %d;\n", i.provider, i.taken);
//				
//				out.println("fix X;");
//				
//				out.println("let {v in TYPE_VM, p in PROVIDER, i in CONTAINER} ");
//				out.println("    W[v,p,i] := 0;");
//				
//				for (SolutionList.W i : solution.ws)
//					if (i.provider != -1)
//						out.printf("let W['v%d','p%d','i%d'] := %d;\n", i.resource, i.provider, i.tier, i.taken);
//				
//				out.println("fix W;");
				
				///////////////
				
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

			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return true;
	}
}
