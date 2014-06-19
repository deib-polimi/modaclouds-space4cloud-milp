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

import it.polimi.modaclouds.space4clouds.milp.xmldatalists.ResEnvList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

//this file works with Resource Environment Diagram
public class ParsResEnv {

	// amount of containers
	public int countContainers = 0;

	// container for all parsed data from ResourceEnvironment Diagram
	public ResEnvList newResEnvList = null;

	// XML document
	private Document maindoc;

	// is true, if document was loaded
	private boolean loadrez = false;

	// root element of loaded document
	private Element root;

	// constructors
	public ParsResEnv(String Filepath) {
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
			e.printStackTrace();
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

		// receives list of containers
		List<Element> NewContainerList = getElements(root,
				"resourceContainer_ResourceEnvironment");
		countContainers = NewContainerList.size();

		// constructor for the class, in which this function saves data of
		// containers
		newResEnvList = new ResEnvList(countContainers);

		for (int i = 0; i < countContainers; i++) {
			// receiving Id and Name of each container
			Element Container = NewContainerList.get(i);
			newResEnvList.Id[i] = Container.getAttribute("id");
			newResEnvList.Name[i] = Container.getAttribute("entityName");
		}
		return 0;
	}

}
