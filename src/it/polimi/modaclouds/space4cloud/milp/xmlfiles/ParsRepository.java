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

import it.polimi.modaclouds.space4cloud.milp.types.*;
import it.polimi.modaclouds.space4cloud.milp.xmldatalists.RepositoryList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

//this file works with Repository Diagram
public class ParsRepository {
	
	private static final Logger logger = LoggerFactory.getLogger(ParsRepository.class);

	// amount of components
	public int countComponents = 0;
	// amount of services
	public int countServices = 0;
	// amount of actions with Resource Demands
	public int countRDActions = 0;

	// container for all parsed data from Repository Diagram
	public RepositoryList resRepositoryList = new RepositoryList();

	// Probability matrix for Resource Demand Actions
	private double[][] RDProbabilityMatrix = null;
	// Resource Demands for Resource Demand Actions
	private double[] RDDemand = null;
	// Ids of Actions with Resource Demands
	public String[] RDActionIDs = null;

	// list of Service names and ids
	private ServiceNames ListOfServices = null;

	// external variables which allow to save indexes of classes and services
	// with which program working now (recursively)
	private int CurrentClassIndex = -1;
	private int CurrentServiceIndex = -1;

	// XML document
	private Document maindoc;

	// is true, if document was loaded
	private boolean loadrez = false;

	// root element of loaded document
	private Element root;

	// constructors
	public ParsRepository(String FilePath) {
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

	// returns child-nodes of CurrentElem
	private List<Element> getChilds(Element CurrentElem) {
		List<Element> res = new ArrayList<Element>();
		if (!loadrez)
			return res;
		NodeList list = CurrentElem.getChildNodes();
		for (int i = 0; i < list.getLength(); i++)
			if (list.item(i).getNodeType() == Node.ELEMENT_NODE)
				res.add((Element) list.item(i));
		return res;
	}

	// returns all sub-actions with seff:InternalAction of CurrentElem
	private List<Element> getInternalActions(Element CurrentElem) {
		List<Element> res = new ArrayList<Element>();
		if (!loadrez)
			return res;
		List<Element> list = getElements(CurrentElem, "steps_Behaviour");
		for (int i = 0; i < list.size(); i++) {
			Element ListElem = list.get(i);
			String XSIType = ListElem.getAttribute("xsi:type");
			if (XSIType.equalsIgnoreCase("seff:InternalAction"))
				res.add(ListElem);
		}
		return res;
	}

	// returns names and ids of Services in subelements of CurrentElem
	private ServiceNames getServiceNames(Element CurrentElem,
			String NameServiceEffectSpecifications) {
		List<Element> FullServiceEffectList = getElements(CurrentElem,
				NameServiceEffectSpecifications);
		ServiceNames res = new ServiceNames(FullServiceEffectList.size());
		for (int i = 0; i < FullServiceEffectList.size(); i++) {
			Element ServiceEffect = FullServiceEffectList.get(i);

			res.Id[i] = ServiceEffect.getAttribute("id");
			Node NewSEFFNode = ServiceEffect
					.getAttributeNode("describedService__SEFF");
			res.SEFFName[i] = NewSEFFNode.getNodeValue();
		}
		return res;
	}

	// returns indexes of Services (in program memory) by their SEFFNames
	private int getServiceIndexBySEFFName(String ServiceSEFFName) {
		int i = 0;
		while (!(ListOfServices.SEFFName[i].equalsIgnoreCase(ServiceSEFFName)))
			i++;
		return i;
	}

	// returns indexes (in program memory) of Actions with Resource Demands by
	// their ids
	private int getRDAIndexById(String RDAId) {
		int i = 0;
		while (!(RDActionIDs[i].equalsIgnoreCase(RDAId)))
			i++;
		return i;
	}

	// returns indexes (in program memory) of Components by their ids
	// (ComponentName)
	public int getComponentRepositoryNum(String ComponentName) {
		int i = 0;
		while (!((countComponents == i) || (resRepositoryList.ComponentIds[i]
				.equalsIgnoreCase(ComponentName))))
			i++;
		return i;
	}
	
	@SuppressWarnings("unused")
	private void printElement(Element e) {
		StringBuilder sb = new StringBuilder();
		sb.append("\"" + e.getTagName() + "\" : {\n");
		NamedNodeMap map = e.getAttributes();
		for (int i = 0; i < map.getLength(); ++i) {
			Node n = map.item(i);
			sb.append("\t\"" + n.getNodeName() + "\" : \"" + n.getNodeValue() + "\"\n");
		}
		String body = e.getTextContent().trim();
		if (body.length() > 0)
			sb.append("\t\"body\" : \"" + body + "\"\n");
		sb.append("}");
		System.out.println(sb.toString());
	}
	
	private int getActualCount() {
		// receive list of components
		List<Element> NewComponentsList = getElements(root,
				"components__Repository");
		
		int count = 0;
		
		for (Element CurrComponent : NewComponentsList)
			count += getInternalActions(CurrComponent).size();
		
		return count;
	}
	
	private double getAvgResourceDemand() {
		// receive list of components
		List<Element> NewComponentsList = getElements(root,
				"components__Repository");
		
		int countValid = 0;
		double sumValid = 0.0;
		
		for (Element CurrComponent : NewComponentsList) {
			List<Element> NewRDActionListInComponent = getInternalActions(CurrComponent);
			for (Element CurrAction : NewRDActionListInComponent) {
				List<Element> NewSpecificationList = getElements(CurrAction,
						"specification_ParametericResourceDemand");
				try {
					Element SpecificationElem = NewSpecificationList.get(0);
					String demandStr = SpecificationElem
							.getAttribute("specification");
					sumValid += Double.parseDouble(demandStr);
					countValid++;
				} catch (Exception e) { }
			}
		}
		
		if (countValid > 0)
			return sumValid / countValid;
		else
			return 0.0;
	}

	// main execution function
	public int analyse_doc() {
		// check that document was loaded
		if (!loadrez)
			return 1;

		// receive list of components
		List<Element> NewComponentsList = getElements(root,
				"components__Repository");
		countComponents = NewComponentsList.size();

//		// receive list of Actions with Resource Demands
//		List<Element> NewResourceDemandActionList = getElements(root,
//				"resourceDemand_Action");
//		countRDActions = NewResourceDemandActionList.size();
		
		countRDActions = getActualCount();

		// initialization block
		RDDemand = new double[countRDActions];
		RDActionIDs = new String[countRDActions];
		RDProbabilityMatrix = new double[countRDActions][resRepositoryList.ncountClasses];
		for (int i = 0; i < countRDActions; i++)
			for (int j = 0; j < resRepositoryList.ncountClasses; j++)
				RDProbabilityMatrix[i][j] = 0;
		resRepositoryList.Init(countRDActions);
		// this block creates list of actions with Resource Demands
		// it extracts numerical values of Resource Demands
		// sets Ids of ResDemsnd actions
		// and Ids and Names of corresponding components
		int kcount = 0;
		
		double avgResourceDemand = getAvgResourceDemand();
		
		for (int i = 0; i < countComponents; i++) {
			Element CurrComponent = NewComponentsList.get(i);
			List<Element> NewRDActionListInComponent = getInternalActions(CurrComponent);
			String TempStr1 = CurrComponent.getAttribute("id");
			String TempStr2 = CurrComponent.getAttribute("entityName");
			for (int j = 0; j < NewRDActionListInComponent.size(); j++) {
				Element CurrAction = NewRDActionListInComponent.get(j);
				String RDAIdStr = CurrAction.getAttribute("id");
				RDActionIDs[kcount] = RDAIdStr;
				List<Element> NewSpecificationList = getElements(CurrAction,
						"specification_ParametericResourceDemand");
				try {
					Element SpecificationElem = NewSpecificationList.get(0);
					String demandStr = SpecificationElem
							.getAttribute("specification");
					RDDemand[kcount] = Double.parseDouble(demandStr);
				} catch (Exception e) {
					// TODO: check this
					RDDemand[kcount] = avgResourceDemand; //0.01;
//					continue;
				}
				resRepositoryList.ComponentIds[kcount] = TempStr1;
				resRepositoryList.ComponentNames[kcount] = TempStr2;
				kcount++;
			}
		}
		// saves demands of Actions in Container RepositoryList
		for (int j = 0; j < countRDActions; j++) {
			resRepositoryList.ComponentDemand[j] = RDDemand[j];
		}
		// receives list of Services
		ListOfServices = getServiceNames(root,
				"serviceEffectSpecifications__BasicComponent");
		List<Element> ServiceEffectSpecificationsList = getElements(root,
				"serviceEffectSpecifications__BasicComponent");
		countServices = ServiceEffectSpecificationsList.size();

		for (int i = 0; i < resRepositoryList.ncountClasses; i++)
			resRepositoryList.ServiceIds[i] = ListOfServices.Id[getServiceIndexBySEFFName(resRepositoryList.ClassIds[i])];

		// for every class it receives first service in execution way and runs
		// recursive part of algorithm
		for (int i = 0; i < resRepositoryList.ncountClasses; i++) {
			// saves current class index
			CurrentClassIndex = i;

			// receives current service index (index of first service for class
			// i)
			int indexStartService = getServiceIndexBySEFFName(resRepositoryList.ClassIds[i]);
			CurrentServiceIndex = indexStartService;
			Element NewService = ServiceEffectSpecificationsList
					.get(indexStartService);
			// call for recursive part
			SystemTreeDemandArray NewSystemTreeDemandArray = serviceEffectSpecification(
					NewService, 1);
			// saves demands of actions for class i in container RepositoryList
			resRepositoryList.setRelativeDemand(i, NewSystemTreeDemandArray);
		}
		// as probabilities are calculated as frequencies therefore this
		// function normalize it
		// (makes sum equal to 1)
		normalizeProb();

		// saves probability matrix into container RepositoryList
		for (int i = 0; i < resRepositoryList.ncountClasses; i++)
			for (int j = 0; j < countRDActions; j++) {
				resRepositoryList.ProbMatrClassesComponents[i][j] = RDProbabilityMatrix[j][i];
			}
		return 0;
	}

	// this function works with serviceEffectSpecification nodes
	// it receives all subnodes (as 1st level of execution tree (for mode
	// details see class -
	// SystemTreeDemandArray from types package))
	// then it calls StepsBehaviour function for every received action
	// and then summaries Demand Arrays of actions on this level
	// currprob - probability value on previous tree level
	private SystemTreeDemandArray serviceEffectSpecification(Element Service,
			double currprob) {
		SystemTreeDemandArray res = new SystemTreeDemandArray(countRDActions);
		List<Element> ChildList = getChilds(Service);
		int childlistsize = ChildList.size();
		for (int i = 0; i < childlistsize; i++) {
			Element step_behaviour = ChildList.get(i);
			SystemTreeDemandArray TempArray = StepsBehaviour(step_behaviour,
					currprob);
			res.SumArrays(TempArray);
		}
		return res;
	}

	// this function analyzes received action: is it Loop, Resource Demand
	// Action or probability-defined action
	// and calls corresponding functions for action (Resource Demand) or
	// subactions (all other types)
	// if received action is External call, then it saves current service index
	// and calls serviceEffectSpecification function
	private SystemTreeDemandArray StepsBehaviour(Element steps_behaviour,
			double currprob) {
		SystemTreeDemandArray res = new SystemTreeDemandArray(countRDActions);
		List<Element> ChildList = getChilds(steps_behaviour);
		String StrType = steps_behaviour.getAttribute("xsi:type");
		if (StrType.equalsIgnoreCase("seff:ExternalCallAction")) {
			String NewCall = steps_behaviour
					.getAttribute("calledService_ExternalService");
			int SaveCurrentServiceIndex = CurrentServiceIndex;

			CurrentServiceIndex = getServiceIndexBySEFFName(NewCall);
			List<Element> ServiceEffectSpecificationsList = getElements(root,
					"serviceEffectSpecifications__BasicComponent");
			Element NewService = ServiceEffectSpecificationsList
					.get(CurrentServiceIndex);
			res = serviceEffectSpecification(NewService, currprob);
			CurrentServiceIndex = SaveCurrentServiceIndex;
		} else
			for (int i = 0; i < ChildList.size(); i++) {
				Element CurrentElem = ChildList.get(i);
				String NameOfElem = CurrentElem.getNodeName();
				if (NameOfElem.equalsIgnoreCase("branches_Branch")) {
					res.SelectMin(branch_transitions(CurrentElem, currprob));
				}
				if (NameOfElem.equalsIgnoreCase("resourceDemand_Action")) {
					String RDAId = steps_behaviour.getAttribute("id");
					int RDAIndex = getRDAIndexById(RDAId);
					res = resourceDemand_Action(CurrentElem, currprob, RDAIndex);
				}
				if (NameOfElem.equalsIgnoreCase("iterationCount_LoopAction")) {
					Node TempNode = CurrentElem
							.getAttributeNode("specification");
					int loopSpecification = Integer.parseInt(TempNode
							.getNodeValue());
					for (int j = 0; j < ChildList.size(); j++) {
						Element SecondLvlElem = ChildList.get(j);
						String NameOfSecondLvlElem = SecondLvlElem
								.getNodeName();
						if (NameOfSecondLvlElem
								.equalsIgnoreCase("bodyBehaviour_Loop")) {
							res = body_behaviour_loop(SecondLvlElem,
									loopSpecification * currprob);
							res.SetLoop(loopSpecification);
						}
					}
				}
			}
		return res;
	}

	// works with Resource Demand Actions
	// it calls function probabilityUpdate to save current probability value
	// then it creates new SystemTreeDemandArray with all 0 excluding current
	// action
	// sets demands and amount of this action in created SystemTreeDemandArray
	private SystemTreeDemandArray resourceDemand_Action(
			Element operat_sign_elem, double currprob, int RDAIndex) {
		SystemTreeDemandArray res = new SystemTreeDemandArray(countRDActions);
		probabilityUpdate(currprob, RDAIndex);
		res.SetRDAction(RDAIndex, RDDemand[RDAIndex]);
		return res;
	}

	// as body_behaviour_loop has the same structure as
	// serviceEffectSpecification
	// so it calls function serviceEffectSpecification to analyse received
	// action
	private SystemTreeDemandArray body_behaviour_loop(
			Element body_behaviour_loop_elem, double currprob) {
		SystemTreeDemandArray res = serviceEffectSpecification(
				body_behaviour_loop_elem, currprob);
		return res;
	}

	// this function works with actions, that contains probability-defined
	// subactions
	// it calls functions for each subaction and multiplies probability of
	// current level on probability of subaction
	private SystemTreeDemandArray branch_transitions(
			Element branch_transit_elem, double currprob) {
		SystemTreeDemandArray res = new SystemTreeDemandArray(countRDActions);
		List<Element> ChildList = getChilds(branch_transit_elem);
		Node newprobStr = branch_transit_elem
				.getAttributeNode("branchProbability");
		double newprob = Double.parseDouble(newprobStr.getNodeValue());
		for (int i = 0; i < ChildList.size(); i++) {
			Element NewElem = ChildList.get(i);
			res = branched_behaviour(NewElem, newprob * currprob);
		}
		return res;
	}

	// as branched_behaviour has the same structure as
	// serviceEffectSpecification
	// so it calls function serviceEffectSpecification to analyze received
	// action
	private SystemTreeDemandArray branched_behaviour(
			Element branched_behaviour_elem, double currprob) {
		SystemTreeDemandArray res = serviceEffectSpecification(
				branched_behaviour_elem, currprob);
		return res;
	}

	// saves current value of probability for class with index CurrentClassIndex
	// and action with index RDAindex
	private void probabilityUpdate(double currprob, int RDAindex) {
		RDProbabilityMatrix[RDAindex][CurrentClassIndex] += currprob;
	}

	// Normalizes probabilities of Resource Demand actions
	// (in recursive part they are calculated as frequencies)
	private void normalizeProb() {
		for (int i = 0; i < resRepositoryList.ncountClasses; i++) {
			double sum = 0;
			for (int j = 0; j < countRDActions; j++)
				sum += RDProbabilityMatrix[j][i];
			if (sum == 0)
				sum = 1;
			for (int j = 0; j < countRDActions; j++)
				RDProbabilityMatrix[j][i] = RDProbabilityMatrix[j][i] / sum;
		}
	}
}