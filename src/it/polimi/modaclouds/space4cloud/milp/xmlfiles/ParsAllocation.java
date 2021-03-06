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

import it.polimi.modaclouds.space4cloud.milp.xmldatalists.AllocationList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

//this class works with Allocation Diagram
public class ParsAllocation {
	
	private static final Logger logger = LoggerFactory.getLogger(ParsAllocation.class);

	// amount of Allocation elements in parsed file
	public int countAllocationPairs = 0;

	// container for all parsed data from Allocation Diagram
	public AllocationList resAllocationList = null;

	// XML document
	private Document maindoc;

	// is true, if document was loaded
	private boolean loadrez = false;

	// root element of loaded document
	private Element root;

	// constructors
	public ParsAllocation(String FilePath) {
		loadModel(FilePath);
	}

	// loads XML document in DOM parser
	public boolean loadModel(String Filepath) {
		try {
			File newfile = new File(Filepath);
			DocumentBuilderFactory docFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			maindoc = docBuilder.parse(newfile);
			root = maindoc.getDocumentElement();
			loadrez = true;
		} catch (Exception e) {
//			e.getMessage();
			logger.error("Error while loading the model.", e);
			loadrez = false;
		}
		return loadrez;
	}

	// main execution function
	public int analyse_doc() {
		// check that document was loaded
		if (!loadrez)
			return 1;

		// receiving list of allocation pairs
		List<Element> NewComponentsList = getElements(root,
				"allocationContexts_Allocation");
		countAllocationPairs = NewComponentsList.size();

		// constructor for container of allocation pairs
		resAllocationList = new AllocationList(countAllocationPairs);

		for (int i = 0; i < countAllocationPairs; i++) {
			// for every pair it extracts it's Id and IDs of corresponding
			// components
			// in System and Resource Environment Diagrams
			Element NewAllocation = NewComponentsList.get(i);
			List<Element> AllocationLinkResEnv = getElements(NewAllocation,
					"resourceContainer_AllocationContext");
			List<Element> AllocationLinkSystem = getElements(NewAllocation,
					"assemblyContext_AllocationContext");
			Element NewResEnvLink = AllocationLinkResEnv.get(0);
			Element NewSystemLink = AllocationLinkSystem.get(0);

			resAllocationList.Id[i] = NewAllocation.getAttribute("id");
			String ResEnvStr = NewResEnvLink.getAttribute("href");
			resAllocationList.ResourceEnvironment[i] = ResEnvStr
					.substring(ResEnvStr.indexOf("#") + 1);
			String SystemStr = NewSystemLink.getAttribute("href");
			resAllocationList.System[i] = SystemStr.substring(SystemStr
					.indexOf("#") + 1);
		}
		return 0;
	}

	// returns subelements of CurrentElem with names ThisType
	private List<Element> getElements(Element CurrentElem, String ThisType) {
		List<Element> res = new ArrayList<Element>();
		if (!loadrez)
			return res;
		NodeList list = CurrentElem.getElementsByTagName(ThisType);
		for (int i = 0; i < list.getLength(); i++)
			if (list.item(i).getNodeType() == Node.ELEMENT_NODE)
				res.add((Element) list.item(i));
		return res;
	}
}
