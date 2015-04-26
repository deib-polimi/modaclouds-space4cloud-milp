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

import it.polimi.modaclouds.space4cloud.milp.xmldatalists.SystemList;

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

//this class works with System Diagram
public class ParsSystem {
	
	private static final Logger logger = LoggerFactory.getLogger(ParsSystem.class);

	// amount of AssemlyContext elements in the parsed document
	public int countAssemblyContexts = 0;

	// main container for parsed data
	public SystemList resSystemList = null;

	// XML document
	private Document maindoc;

	// is true, if document was loaded
	private boolean loadrez = false;

	// root element of loaded document
	private Element root;

	// constructor
	public ParsSystem(String Filepath) {
		loadModel(Filepath);
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
			logger.error("Error while reading the model.", e);
			loadrez = false;
		}
		return loadrez;
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

	// main execution function
	public int analyse_doc() {
		// check that document was loaded
		if (!loadrez)
			return 1;
		// receives list of assembly contexts
		List<Element> NewAssemblyContextsList = getElements(root,
				"assemblyContexts__ComposedStructure");
		countAssemblyContexts = NewAssemblyContextsList.size();
		// constructor for the container of assembly contexts
		resSystemList = new SystemList(countAssemblyContexts);
		// for every assembly context it saves its Id, Name and Id of
		// corresponding element from Repository Diagram
		for (int i = 0; i < countAssemblyContexts; i++) {
			// new assembly context
			Element NewAssembly = NewAssemblyContextsList.get(i);
			// it's ID
			resSystemList.Id[i] = NewAssembly.getAttribute("id");
			// it's Name
			resSystemList.Name[i] = NewAssembly.getAttribute("entityName");
			// receiving id of repository element
			List<Element> EncapsulatedComponentsList = getElements(NewAssembly,
					"encapsulatedComponent__AssemblyContext");
			Element EncapsulatedComponent = EncapsulatedComponentsList.get(0);
			String hrefStr = EncapsulatedComponent.getAttribute("href");
			resSystemList.RepositoryId[i] = hrefStr.substring(hrefStr
					.indexOf("#") + 1);
		}
		return 0;
	}
}
