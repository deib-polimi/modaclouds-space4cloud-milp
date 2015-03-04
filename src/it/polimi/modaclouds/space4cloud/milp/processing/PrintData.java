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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

// this class is used to create file data.dat for AMPL solver
public abstract class PrintData {

	// container which contains all data to be printed in "data.dat" file
	public DataCollection newdatacollection = null;

	// Path where data.dat file should be saved
	public String SaveFilePath = "";

	// Path to PCM model constraints (memory)
	public String FilePathConst = "";

	// allows to check that newdatacollection is not empty
	public boolean dataWasLoaded = false;

	// the starting solution
	public SolutionList initialSolution;

	// a solution generated from the Resource Environment Extension file
	public SolutionList resEnvExt;

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
			DBList CDBList, SqlBaseParsMatrix newMatrix, SolutionList initialSolution, SolutionList resEnvExt) {
		//			@SuppressWarnings("unused")
		//			createconstraintxml newccxml = new createconstraintxml(Configuration, CRList); // TODO: WTF, sovrascrive il file di constraints fornito!
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

		this.initialSolution = initialSolution;
		this.resEnvExt = resEnvExt;

		dataWasLoaded = true;
	}
	
	protected static DecimalFormat doubleFormatter() {
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.getDefault());
		otherSymbols.setDecimalSeparator('.');
//		DecimalFormat myFormatter = new DecimalFormat("0.000#######", otherSymbols);
		DecimalFormat myFormatter = new DecimalFormat("0.000", otherSymbols);
		return myFormatter;
	}
	
	// this function prints information from newdatacollection to file
	// "data.dat" for AMPL model
	public abstract boolean printDataFile();
	
	public static void print(Parser p) {
		switch (Configuration.SOLVER) {
		case AMPL:
			PrintDataAMPL.print(p);
			break;
		case CMPL:
			PrintDataCMPL.print(p);
			break;
		}
		
	}

}
