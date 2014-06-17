package it.polimi.modaclouds.space4clouds.milp.xmlfiles;

import it.polimi.modaclouds.space4clouds.milp.xmldatalists.AllocationList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

//this class works with Allocation Diagram
public class ParsAllocation {

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
			e.printStackTrace();
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
