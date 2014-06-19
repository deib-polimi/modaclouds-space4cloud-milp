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
package it.polimi.modaclouds.space4clouds.milp.xmlfiles;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

//is used to create model extension file
public class ExtensionXML {

	// index of the file in wrapperextension class
	// now is not used
	public int fileIndex = 0;
	public String SaveDirectory = "";
	public String TypeOfResult = "optimized";

	// this variable sets, if any information was added to this class
	// elsewise it is empty and will not be printed
	// wrapperextension creates this classes for each provider
	// but this variable controls that only providers from AMPL result will be
	// printed
	public boolean ShouldBePrinted = false;
	// name of current provider (extensionxml creates one per provider)
	public String ProviderName = "";
	// amount of VMs (per time interval and container)
	public String[][] replicas = null;
	// container Id (from Resource Environment Diagram)
	public String[] ContainerId = null;
	// names of VMs types (per container)
	public String[] VMtypeName = null;
	// ServiceType (for example, EC2)
	public String[] ServiceType = null;
	// Service Name (compute)
	public String[] ServiceName = null;
	
	// Region
	public String[] Region = null;

	// information about arrivals
	public int[] ThinkTime = null;
	public int[] population = null;

	// amount of time intervals
	public int counttimes = 0;
	// amount of containers
	public int countcontainers = 0;

	// XML document
	private Document maindoc;

	// is true, if document was loaded
	private boolean loadrez = false;

	// root element of loaded document
	private Element root;

	// constructor
	public ExtensionXML(int index,
			int counttimesn, int countcontainersn) {
//		ExtensionFilePath = NewExtensionFilePath;
//		ExtractModelDirectory(NewExtensionFilePath);
		loadModel();
		fileIndex = index;

		counttimes = counttimesn;
		countcontainers = countcontainersn;

		replicas = new String[countcontainers][counttimes];
		ContainerId = new String[countcontainers];
		VMtypeName = new String[countcontainers];
		ServiceType = new String[countcontainers];
		ServiceName = new String[countcontainers];
		
		Region = new String[countcontainers];

		ThinkTime = new int[counttimes];
		population = new int[counttimes];
	}

	// loads XML document in DOM parser
	public boolean loadModel() {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			
			maindoc = docBuilder.newDocument();
			root = maindoc.createElement("palladioExtension");
			maindoc.appendChild(root);
			
			loadrez = true;
		} catch (Exception e) {
//			e.getMessage();
			e.printStackTrace();
			loadrez = false;
		}
		return loadrez;
	}

	// main creating function
	public int createExtensions() {
		// check that document was loaded
		if (!loadrez)
			return 1;
		// check that data in this class is not empty
		if (!ShouldBePrinted)
			return 0;

		try {

			// new usageModelExtensions
			Element UMExtNew = maindoc.createElement("usageModelExtensions");
			root.appendChild(UMExtNew);

			for (int i = 0; i < counttimes; i++) {
				Element Hour = maindoc.createElement("Hour");
				UMExtNew.appendChild(Hour);

				Attr pattr = maindoc.createAttribute("thinkTime");
				pattr.setValue(Integer.toString(ThinkTime[i]));
				Hour.setAttributeNode(pattr);

				pattr = maindoc.createAttribute("population");
				pattr.setValue(Integer.toString(population[i]));
				Hour.setAttributeNode(pattr);
			}

			// new ResourceContainerExtensions
			Element ResExtNew = maindoc
					.createElement("ResourceContainerExtensions");
			root.appendChild(ResExtNew);

			for (int i = 0; i < countcontainers; i++) {
				Element ResCont = maindoc.createElement("ResourceContainer");
				ResExtNew.appendChild(ResCont);
				Attr pattr = maindoc.createAttribute("id");
				pattr.setValue(ContainerId[i]);
				ResCont.setAttributeNode(pattr);
				pattr = maindoc.createAttribute("Provider");
				pattr.setValue(ProviderName);
				ResCont.setAttributeNode(pattr);

				Element Infrastruct = maindoc.createElement("Infrastructure");
				ResCont.appendChild(Infrastruct);
				Attr cattr = maindoc.createAttribute("serviceType");
				cattr.setValue(ServiceType[i]);
				Infrastruct.setAttributeNode(cattr);
				cattr = maindoc.createAttribute("serviceName");
				cattr.setValue(ServiceName[i]);
				Infrastruct.setAttributeNode(cattr);

				Element ResourceSizeID = maindoc
						.createElement("ResourceSizeID");
				ResourceSizeID.setTextContent(VMtypeName[i]);
				ResCont.appendChild(ResourceSizeID);

				Element Replicas = maindoc.createElement("Replicas");
				ResCont.appendChild(Replicas);

				for (int j = 0; j < counttimes; j++) {
					Element Replica = maindoc.createElement("replica");
					Replicas.appendChild(Replica);

					Attr tattr = maindoc.createAttribute("value");
					tattr.setValue(replicas[i][j]);
					Replica.setAttributeNode(tattr);
					tattr = maindoc.createAttribute("hour");
					tattr.setValue(Integer.toString(j + 1));
					Replica.setAttributeNode(tattr);
				}
			}

			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(maindoc);
			
			File f = new File(SaveDirectory + "extension-" + TypeOfResult + "-" + ProviderName + ".xml");
			StreamResult result = new StreamResult(f);
			transformer.transform(source, result);
			
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
		return 0;
	}
}
