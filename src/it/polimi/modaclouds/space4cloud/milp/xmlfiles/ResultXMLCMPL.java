package it.polimi.modaclouds.space4cloud.milp.xmlfiles;

import it.polimi.modaclouds.space4cloud.milp.Configuration;
import it.polimi.modaclouds.space4cloud.milp.db.DBList;
import it.polimi.modaclouds.space4cloud.milp.processing.Parser;
import it.polimi.modaclouds.space4cloud.milp.types.SqlBaseParsMatrix;
import it.polimi.modaclouds.space4cloud.milp.xmldatalists.RepositoryList;

import java.io.FileReader;
import java.util.Scanner;
import java.util.StringTokenizer;

public class ResultXMLCMPL extends ResultXML {
	// constructor
	public ResultXMLCMPL() {
		super();
		LogFilePath = Configuration.RUN_LOG_CMPL;
		ResFilePath = Configuration.RUN_RES_CMPL;
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
			
			Scanner sc = new Scanner(new FileReader(LogFilePath));
			
			String timeModel = "CMPL: Time used for model generation: ";
			String timeSol = "CMPL: Time used for solving the model: ";
			String obj = "Objective value:";
			String fail = "CMPL model generation - failed";
			
			boolean goOn = true;
			
			while (sc.hasNextLine() && goOn && (InputTime.length() == 0 || SolveTime.length() == 0 || Objective.length() == 0)) {
				String line = sc.nextLine();
				int i;
				if ((i = line.indexOf(timeModel)) > -1) {
					InputTime = line.substring(i + timeModel.length());
					InputTime = InputTime.substring(0, InputTime.indexOf("seconds"));
					InputTime.trim();
				} else if ((i = line.indexOf(timeSol)) > -1) {
					SolveTime = line.substring(i + timeSol.length());
					SolveTime = SolveTime.substring(0, SolveTime.indexOf("seconds"));
					SolveTime.trim();
				} else if ((i = line.indexOf(obj)) > -1) {
					Objective = line.substring(i + obj.length());
					Objective.trim();
					hassolution = true;
					isfeasible = true;
				} else if ((i = line.indexOf(fail)) > -1) {
					hassolution = false;
					isfeasible = false;
					goOn = false;
				}
			}
			sc.close();
			
			OutputTime = "0";
			
			time = Math.round((Double.parseDouble(InputTime) + Double.parseDouble(SolveTime) + Double.parseDouble(OutputTime))); // * 1000);
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
			Scanner sc = new Scanner(new FileReader(ResFilePath));
			while (sc.hasNextLine()) {
				String S = sc.nextLine();
				
				StringTokenizer st = new StringTokenizer(S, " ");
				
				if (st.countTokens() != 6)
					continue;
				
				String name = st.nextToken();
				String var = name.substring(0, name.indexOf('['));
				name = name.substring(name.indexOf('[') + 1, name.length() - 1);
				String[] comps = name.split(",");
				
				String Amount = "-1";
				String ArrivalRate = "-1";
				
				int v = -1, p = -1, i = -1, t = -1;
				st.nextToken();
				
				for (String s : comps)
					switch (s.charAt(0)) {
					case 'v':
						v = Integer.parseInt(s.substring(1)) - 1;
						break;
					case 'p':
						p = Integer.parseInt(s.substring(1)) - 1;
						break;
					case 'i':
						i = Integer.parseInt(s.substring(1)) - 1;
						break;
					case 't':
						t = Integer.parseInt(s.substring(1));
						break;
					}
				
				if (var.equals("AmountVM")) {
					Amount = st.nextToken();
				} else if (var.equals("PartialArrRate")) {
					ArrivalRate = st.nextToken();
				} else {
					continue;
				}
				
				if (!ArrivalRate.equals("-1")) {
					double Population = Double.parseDouble(ArrivalRate) * CDBList.thinkTimes[t - 1]; //10;
					newWExtension.ExtensionsArray[p].population[t - 1] = (int) Math
							.round(Population);
					newWExtension.ExtensionsArray[p].ThinkTime[t - 1] = (int)CDBList.thinkTimes[t - 1]; //10;
				}
				if (!Amount.equals("-1")) {
					// saving data in corresponding extension class
					newWExtension.ExtensionsArray[p].ContainerId[i] = CRList.ContainerList.Id[i];

					newWExtension.ExtensionsArray[p].ServiceName[i] = newMatrix.ServiceName[p][v];
					newWExtension.ExtensionsArray[p].ServiceType[i] = "Compute";
					newWExtension.ExtensionsArray[p].VMtypeName[i] = newMatrix.TypeName[p][v];
					
					newWExtension.ExtensionsArray[p].Region[i] = newMatrix.Region[p][v];
					
					newWExtension.ExtensionsArray[p].ProviderName = CDBList.ProviderName[p];
					newWExtension.ExtensionsArray[p].ShouldBePrinted = true;
					
					newWExtension.ExtensionsArray[p].replicas[i][t - 1] = Amount;
				}
				// Prov=p;
				
			}
			
			if (ExportExtensions)
				newWExtension.printExtensions();
			
			sc.close();

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
		ResultXMLCMPL newresultxml = new ResultXMLCMPL();
		newresultxml.printFile(newparser.newparsrepository.resRepositoryList,
				newparser.newparssql.newDBList, newparser.newparssql.resMatrix);
	}
	
}
