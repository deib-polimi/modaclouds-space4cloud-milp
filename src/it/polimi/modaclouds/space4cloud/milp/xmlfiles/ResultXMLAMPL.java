package it.polimi.modaclouds.space4cloud.milp.xmlfiles;

import it.polimi.modaclouds.space4cloud.milp.Configuration;
import it.polimi.modaclouds.space4cloud.milp.db.DBList;
import it.polimi.modaclouds.space4cloud.milp.processing.Parser;
import it.polimi.modaclouds.space4cloud.milp.types.SqlBaseParsMatrix;
import it.polimi.modaclouds.space4cloud.milp.xmldatalists.RepositoryList;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.StringTokenizer;

public class ResultXMLAMPL extends ResultXML {
	// constructor
	public ResultXMLAMPL() {
		super();
		LogFilePath = Configuration.RUN_LOG;
		ResFilePath = Configuration.RUN_RES;
	}
	
	// function which prints final result file
	// CRList - container with information from PCM model
	// CDBList - container with information from AddInfData file
	// newMatrix - container with information from SQL database
	@Override
	public int printFile(RepositoryList CRList, DBList CDBList,
			SqlBaseParsMatrix newMatrix) {
		CRList.DemandperContainerCalc();
		// receving solving times and objective from AMPL log
		long time = 0L;
		double cost = 0.0;
		boolean hassolution = false;
		boolean isfeasible = true;
		
		try {
			String InputTime="";
			String SolveTime="";
			String OutputTime="";
			String Objective="";
			
			BufferedReader br = new BufferedReader(new FileReader(LogFilePath));
			String S = "";
			int i;
			while ((S = br.readLine()) != null) {
				
				if ((i = S.indexOf("Input =")) > -1)
					InputTime = S.substring(i + "Input =".length()).trim();
				else if ((i = S.indexOf("Output =")) > -1)
					OutputTime = S.substring(i + "Output =".length()).trim();
				else if ((i = S.indexOf("Solve =")) > -1) {
					SolveTime = S.substring(i + "Solve =".length()).trim();
					hassolution = true;
				} /*else if ((i = S.indexOf("optimal integer solution; objective")) > -1)
					Objective = S.substring(i + "optimal integer solution; objective".length()).trim();*/
				else if ((i = S.indexOf("; objective")) > -1)
					Objective = S.substring(i + "; objective".length()).trim();
				else if (S.indexOf("infeasible problem") > -1)
					isfeasible = false;
				
			}
			br.close();
			
			time = Math.round((Double.parseDouble(InputTime) + Double.parseDouble(SolveTime) + Double.parseDouble(OutputTime)) * 1000);
			cost = Double.parseDouble(Objective);
		} catch (Exception ioe) {
			ioe.printStackTrace();
			time = 0L;
			cost = 0.0;
		}

		// if no solution, then print it ("No Solution!") and ends.
		if (!hassolution || !isfeasible) {
			noResult();
			return 1;
		}

		CRList.findefficandcheapbetweenproviders();
		// constructor for class which is used to print extension files
		WrapperExtension newWExtension = new WrapperExtension(
				CDBList.countProviders, SaveDirectory,
				CDBList.countTimeIntervals, CRList.ContainerList.ncount);
		// parsing AMPL results, saving them in new XML document and in the
		// container wrapperextension

		try {
			BufferedReader in_buf = new BufferedReader(new FileReader(
					ResFilePath));
			String S = in_buf.readLine();
			while (!(S = in_buf.readLine()).equalsIgnoreCase(";")) {
				
				StringTokenizer st = new StringTokenizer(S, " ");
				
				if (st.countTokens() < 6) {
					int tempCost;
					if ((tempCost = S.indexOf("AmountVM[v,p,i,t] = ")) > -1)
						cost = Double.parseDouble(S.substring(tempCost + "AmountVM[v,p,i,t] = ".length()));
					
					continue;
				}
				
				int p = Integer.parseInt(st.nextToken().substring(1)) - 1;
				int t = Integer.parseInt(st.nextToken().substring(1));
				int i = Integer.parseInt(st.nextToken().substring(1)) - 1;
				int v = Integer.parseInt(st.nextToken().substring(1)) - 1;
				String Amount = st.nextToken();
				String ArrivalRate = st.nextToken();
				

				// saving data in corresponding extension class
				newWExtension.ExtensionsArray[p].ContainerId[i] = CRList.ContainerList.Id[i];

				newWExtension.ExtensionsArray[p].ServiceName[i] = newMatrix.ServiceName[p][v];
				newWExtension.ExtensionsArray[p].ServiceType[i] = "Compute";
				newWExtension.ExtensionsArray[p].VMtypeName[i] = newMatrix.TypeName[p][v];
				
				newWExtension.ExtensionsArray[p].Region[i] = newMatrix.Region[p][v];
				
				newWExtension.ExtensionsArray[p].ProviderName = CDBList.ProviderName[p];
				newWExtension.ExtensionsArray[p].ShouldBePrinted = true;
//					double Population = Double.parseDouble(ArrivalRate)
//							* (10 + CRList.MaxSystemResponseTime);
				double Population = Double.parseDouble(ArrivalRate) * CDBList.thinkTimes[t - 1]; //10;
				newWExtension.ExtensionsArray[p].population[t - 1] = (int) Math
						.round(Population);
				newWExtension.ExtensionsArray[p].ThinkTime[t - 1] = (int)CDBList.thinkTimes[t - 1]; //10;

				newWExtension.ExtensionsArray[p].replicas[i][t - 1] = Amount;
				// Prov=p;
				
			}
			
			if (ExportExtensions)
				newWExtension.printExtensions();
			
			in_buf.close();

		} catch (Exception ioe) {
			ioe.printStackTrace();
		}
		
		/////////////////////////////////////////////
		
		writeMultiCloudExtension(newWExtension);
		
		/////////////////////////////////////////////

		writeResourceModelExtension(newWExtension);
		
		/////////////////////////////////////////////
		
		writeSolution(newWExtension, cost, time);

		return 0;
	}
	
	public static void print(Parser newparser) {
		ResultXMLAMPL newresultxml = new ResultXMLAMPL();
		newresultxml.printFile(newparser.newparsrepository.resRepositoryList,
				newparser.newparssql.newDBList, newparser.newparssql.resMatrix);
	}
}
