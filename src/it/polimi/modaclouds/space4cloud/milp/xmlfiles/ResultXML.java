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
package it.polimi.modaclouds.space4cloud.milp.xmlfiles;

import it.polimi.modaclouds.qos_models.schema.IaasService;
import it.polimi.modaclouds.qos_models.schema.Location;
import it.polimi.modaclouds.qos_models.schema.MultiCloudExtension;
import it.polimi.modaclouds.qos_models.schema.MultiCloudExtensions;
import it.polimi.modaclouds.qos_models.schema.Provider;
import it.polimi.modaclouds.qos_models.schema.Replica;
import it.polimi.modaclouds.qos_models.schema.ReplicaElement;
import it.polimi.modaclouds.qos_models.schema.ResourceContainer;
import it.polimi.modaclouds.qos_models.schema.ResourceModelExtension;
import it.polimi.modaclouds.qos_models.schema.WorkloadPartition;
import it.polimi.modaclouds.space4cloud.milp.Configuration;
import it.polimi.modaclouds.space4cloud.milp.db.DBList;
import it.polimi.modaclouds.space4cloud.milp.types.SqlBaseParsMatrix;
import it.polimi.modaclouds.space4cloud.milp.xmldatalists.RepositoryList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

//this class is used to parse AMPL result
public class ResultXML {

	// File with AMPL logs (on local machine)
	public String LogFilePath;
	// file with AMPL results (on local machine)
	public String ResFilePath;
	// file where final result (with all providers) should be saved
	public String FinalResPath;
	
	public String FinalResPathMce;

	public String SaveDirectory;
	
	public String FinalResPathSolution;
	
	private boolean ExportExtensions = false;

	// constructor
	public ResultXML() {
		LogFilePath = Configuration.RUN_LOG;
		ResFilePath = Configuration.RUN_RES;
		FinalResPath = Paths.get(
				Configuration.PROJECT_BASE_FOLDER,
				Configuration.WORKING_DIRECTORY,
				Configuration.GENERATED_RESOURCE_MODEL_EXT).toString();
		FinalResPathSolution = Paths.get(
				Configuration.PROJECT_BASE_FOLDER,
				Configuration.WORKING_DIRECTORY,
				Configuration.GENERATED_SOLUTION).toString();
		FinalResPathMce = Paths.get(
				Configuration.PROJECT_BASE_FOLDER,
				Configuration.WORKING_DIRECTORY,
				Configuration.GENERATED_MULTI_CLOUD_EXT).toString();
		SaveDirectory = Paths.get(
				Configuration.PROJECT_BASE_FOLDER,
				Configuration.WORKING_DIRECTORY).toString();
		ExportExtensions = Configuration.ExportExtensions;
	}

	private void noResult() {
		ResourceModelExtension rme = new ResourceModelExtension();
		MultiCloudExtensions mces = new MultiCloudExtensions();
		writeFile(rme);
		writeFile(mces);
		
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("SolutionMultiResult");
			doc.appendChild(rootElement);
			rootElement.setAttribute("cost","" + Double.MAX_VALUE);
			rootElement.setAttribute("time", "0");
			rootElement.setAttribute("feasibility","" + false);
			
			writeFile(doc);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}

	private void writeFile(ResourceModelExtension rme) {
		try {
			// create JAXB context and instantiate marshaller
			JAXBContext context = JAXBContext
					.newInstance(ResourceModelExtension.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			// Write to System.out
//			m.marshal(rme, System.out);

			// Write to File
			m.marshal(rme, new File(FinalResPath));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void writeFile(MultiCloudExtensions mces) {
		try {
			// create JAXB context and instantiate marshaller
			JAXBContext context = JAXBContext
					.newInstance(MultiCloudExtensions.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			// Write to System.out
//			m.marshal(rme, System.out);

			// Write to File
			m.marshal(mces, new File(FinalResPathMce));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void writeFile(Document doc) {
		try {
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer;
			transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			File file = new File(FinalResPathSolution);
			StreamResult result = new StreamResult(file);
	
			// Output to console for testing
			// StreamResult result = new StreamResult(System.out);
	
			transformer.transform(source, result);
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// function which prints final result file
	// CRList - container with information from PCM model
	// CDBList - container with information from AddInfData file
	// newMatrix - container with information from SQL database
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
//				double Population = Double.parseDouble(ArrivalRate)
//						* (10 + CRList.MaxSystemResponseTime);
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
	
	private void writeMultiCloudExtension(WrapperExtension newWExtension) {
		MultiCloudExtensions mces = new MultiCloudExtensions();
		
		MultiCloudExtension mce = new MultiCloudExtension();
		mces.setMultiCloudExtensions(mce);
		
		mce.setId("id");
		
		for (ExtensionXML x : newWExtension.ExtensionsArray) {
			if (x.ProviderName == null || x.ProviderName.length() == 0)
				continue;
			
			Provider p = new Provider();
			p.setName(x.ProviderName);
			
			for (int i = 0; i < x.population.length; ++i) {
				WorkloadPartition wp = new WorkloadPartition();
				wp.setHour(i);
				
				int totalPopulation = 0;
				for (ExtensionXML y : newWExtension.ExtensionsArray) {
					totalPopulation += y.population[i];
				}
				
				wp.setValue((int)Math.round((double)x.population[i] * 100 / totalPopulation));
				p.getWorkloadPartition().add(wp);
			}
			
			mce.getProvider().add(p);
		}
		
		writeFile(mces);
	}

//	private void writeResourceModelExtensionOld(WrapperExtension newWExtension) {
//		ResourceModelExtension rme = new ResourceModelExtension();
//		
//		for (int w = 0; w < newWExtension.ExtensionsArray.length; ++w) {
//			ExtensionXML x = newWExtension.ExtensionsArray[w];
//
//			for (int i = 0; i < x.ContainerId.length; ++i) {
//				if (x.ContainerId[i] == null)
//					continue;
//
//				ResourceContainer rc = new ResourceContainer();
//				rc.setProvider(x.ProviderName);
//				rc.setId(x.ContainerId[i]);
//
//				IaasService is = new IaasService();
//				is.setServiceName(x.ServiceName[i]);
//				is.setServiceType(x.ServiceType[i]);
//				
//				String region = x.Region[i]; 
//				if (region != null && region.length() > 0) {
//					Location l = new Location();
//					l.setRegion(x.Region[i]);
//					is.setLocation(l);
//				}
//
//				rc.setCloudResource(is);
//				rme.getResourceContainer().add(rc);
//			}
//		}
//
//		writeFile(rme);
//	}
	
	private void writeResourceModelExtension(WrapperExtension newWExtension) {
		ResourceModelExtension rme = new ResourceModelExtension();
		
		for (int w = 0; w < newWExtension.ExtensionsArray.length; ++w) {
			ExtensionXML x = newWExtension.ExtensionsArray[w];

			for (int i = 0; i < x.ContainerId.length; ++i) {
				if (x.ContainerId[i] == null)
					continue;

				ResourceContainer rc = new ResourceContainer();
				rc.setProvider(x.ProviderName);
				rc.setId(x.ContainerId[i]);

				IaasService is = new IaasService();
				is.setServiceName(x.ServiceName[i]);
				is.setServiceType(x.ServiceType[i]);
				
				String region = x.Region[i]; 
				if (region != null && region.length() > 0) {
					Location l = new Location();
					l.setRegion(x.Region[i]);
					is.setLocation(l);
				}

				is.setResourceSizeID(x.VMtypeName[i]);
				
				Replica replicas = new Replica();
				
				for (int j = 0; j < 24; ++j) {
					ReplicaElement re = new ReplicaElement();
					re.setHour(j);
					re.setValue(Integer.parseInt(x.replicas[i][j]));
					replicas.getReplicaElement().add(re);
				}
				
				is.setReplicas(replicas);
				
				rc.setCloudResource(is);
				rme.getResourceContainer().add(rc);
			}
		}

		writeFile(rme);
	}

	private void writeSolution(WrapperExtension newWExtension, double cost, long time) {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
	
			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("SolutionMultiResult");
			doc.appendChild(rootElement);
	
			// set cost
			rootElement.setAttribute("cost","" + cost); // 0);
			//set evaluationtime
			rootElement.setAttribute("time","" + time); //0);
			//set feasibility
			rootElement.setAttribute("feasibility","" + true); // false);
			
			ArrayList<String> providers = new ArrayList<String>();
			for (int w = 0; w < newWExtension.ExtensionsArray.length; ++w) {
				ExtensionXML x = newWExtension.ExtensionsArray[w];
	
				for (int i = 0; i < x.ContainerId.length; ++i) {
					if (x.ContainerId[i] == null)
						continue;
	
					if (!providers.contains(x.ProviderName)) {
						providers.add(x.ProviderName);
					}
				}
			}
			
			boolean added = false;
			
			for (String provider : providers) {
				for (int w = 0; w < newWExtension.ExtensionsArray.length; ++w) {
					ExtensionXML x = newWExtension.ExtensionsArray[w];
					
					Element solution = doc.createElement("Solution");
	//				rootElement.appendChild(solution);
					added = false;
					
					// set cost
					solution.setAttribute("cost","" + 0);
					//set evaluationtime
					solution.setAttribute("time","" + 0);
					//set feasibility
					solution.setAttribute("feasibility","" + false);
				
	
					//create tier container element
					Element tiers = doc.createElement("Tiers");
					solution.appendChild(tiers);
	
					for (int i = 0; i < x.ContainerId.length; ++i) {
						if (x.ContainerId[i] == null || !x.ProviderName.equals(provider))
							continue;
			
						if (!added) {
							rootElement.appendChild(solution);
							added = true;
						}
						
						Element tier = doc.createElement("Tier");
						tiers.appendChild(tier);			
		
						//set id, name, provider name, service name, resource name, service type
						tier.setAttribute("id", x.ContainerId[i]);
						tier.setAttribute("name", "");
						tier.setAttribute("providerName", x.ProviderName);
						tier.setAttribute("serviceName", x.ServiceName[i]);
						tier.setAttribute("resourceName", x.VMtypeName[i]);
						tier.setAttribute("serviceType", x.ServiceType[i]);
		
						for (int j = 0; j < 24; ++j) {					
							//create the allocation element
							Element hourAllocation = doc.createElement("HourAllocation");
							tier.appendChild(hourAllocation);
							hourAllocation.setAttribute("hour","" + j);
							hourAllocation.setAttribute("allocation","" + x.replicas[i][j]);
		
						}
					}
				}
			}
			
			writeFile(doc);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
