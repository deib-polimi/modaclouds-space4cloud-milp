package it.polimi.modaclouds.space4clouds.milp.xmlfiles;

import it.polimi.modaclouds.space4clouds.milp.xmldatalists.SystemList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

//this class works with System Diagram
public class ParsSystem {

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
