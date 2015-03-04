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
import it.polimi.modaclouds.space4cloud.milp.processing.Parser;
import it.polimi.modaclouds.space4cloud.milp.types.SqlBaseParsMatrix;
import it.polimi.modaclouds.space4cloud.milp.xmldatalists.RepositoryList;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;

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
public abstract class ResultXML {

	// File with AMPL logs (on local machine)
	public String LogFilePath;
	// file with AMPL results (on local machine)
	public String ResFilePath;
	// file where final result (with all providers) should be saved
	public String FinalResPath;
	
	public String FinalResPathMce;

	public String SaveDirectory;
	
	public String FinalResPathSolution;
	
	protected boolean ExportExtensions = false;
	
	public ResultXML() {
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

	protected void noResult() {
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

	protected void writeFile(ResourceModelExtension rme) {
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
	
	protected void writeFile(MultiCloudExtensions mces) {
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
	
	protected void writeFile(Document doc) {
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
	public abstract int printFile(RepositoryList CRList, DBList CDBList,
			SqlBaseParsMatrix newMatrix);
	
	protected void writeMultiCloudExtension(WrapperExtension newWExtension) {
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

//		private void writeResourceModelExtensionOld(WrapperExtension newWExtension) {
//			ResourceModelExtension rme = new ResourceModelExtension();
//			
//			for (int w = 0; w < newWExtension.ExtensionsArray.length; ++w) {
//				ExtensionXML x = newWExtension.ExtensionsArray[w];
//
//				for (int i = 0; i < x.ContainerId.length; ++i) {
//					if (x.ContainerId[i] == null)
//						continue;
//
//					ResourceContainer rc = new ResourceContainer();
//					rc.setProvider(x.ProviderName);
//					rc.setId(x.ContainerId[i]);
//
//					IaasService is = new IaasService();
//					is.setServiceName(x.ServiceName[i]);
//					is.setServiceType(x.ServiceType[i]);
//					
//					String region = x.Region[i]; 
//					if (region != null && region.length() > 0) {
//						Location l = new Location();
//						l.setRegion(x.Region[i]);
//						is.setLocation(l);
//					}
//
//					rc.setCloudResource(is);
//					rme.getResourceContainer().add(rc);
//				}
//			}
//
//			writeFile(rme);
//		}
	
	protected void writeResourceModelExtension(WrapperExtension newWExtension) {
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

	protected void writeSolution(WrapperExtension newWExtension, double cost, long time) {
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
	
	public static void print(Parser p) {
		switch (Configuration.SOLVER) {
		case AMPL:
			ResultXMLAMPL.print(p);
			break;
		case CMPL:
			ResultXMLCMPL.print(p);
			break;
		}
		
	}

}
