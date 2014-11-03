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

import it.polimi.modaclouds.space4cloud.milp.xmldatalists.UsageList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

//this class works with UsageModel Diagram
public class ParsUsage {

	// amount of classes
	public int classescount = 0;

	// container for all parsed data from UsageModel Diagram
	public UsageList resUsageList = null;

	// XML document
	private Document maindoc;

	// is true, if document was loaded
	private boolean loadrez = false;

	// root element of loaded document
	private Element root;

	// constructor
	public ParsUsage(String Filepath) {
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
			e.getMessage();
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

	// main execution function
	public int analyse_doc() {
		// check that document was loaded
		if (!loadrez)
			return 1;

		// receives list of UsageScenarios
		List<Element> NewUsageScenarioList = getElements(root,
				"usageScenario_UsageModel");
		int countusagescenario = NewUsageScenarioList.size();
		for (int i = 0; i < countusagescenario; i++) {
			// for every UsageScenario it receives list of UsageBehaviour
			Element UsageScenario = NewUsageScenarioList.get(i);
			List<Element> NewScenarioBehaviourList = getElements(UsageScenario,
					"scenarioBehaviour_UsageScenario");

			for (int j = 0; j < NewScenarioBehaviourList.size(); j++) {
				Element ScenarioBehaviour = NewScenarioBehaviourList.get(j);
				// creates list of classes
				// calls constructor for main container of info from UsageModel
				// Diagram
				// saves Ids of classes and sets to 0 starting probabilities of
				// every class
				List<Element> NewOperationSignatureList = getElements(
						ScenarioBehaviour,
						"operationSignature__EntryLevelSystemCall");
				List<Element> NewOperationSignatureCheckedList = CheckedListOfCalls(NewOperationSignatureList);
				classescount = NewOperationSignatureCheckedList.size();
				resUsageList = new UsageList(classescount);
				for (int k = 0; k < classescount; k++) {
					Element CurrElem = NewOperationSignatureCheckedList.get(k);
					Node AttrCurrElem = CurrElem.getAttributeNode("href");
					resUsageList.ClassRepositoryId[k] = AttrCurrElem
							.getNodeValue();
					resUsageList.probdistr[k] = 0;
				}

				// calls recursive part of algorithm for selected UsageBehaviour
				// with probability on this level equals to 1
				scenarioBehaviour_usage(ScenarioBehaviour, 1);
			}
		}
		// normalization of probability arrays (they are calculated as
		// frequencies)
		normalizeProb();
		// extracting Ids of repository services (this function deletes part
		// before # in every Id)
		convertRepositoryIds();
		return 0;
	}

	// this function creates list of actions in UsageBehavior
	// for every action in this list it calls action_scenariobehaviour function
	private boolean scenarioBehaviour_usage(Element ScenarioBehaviour,
			double currprob) {
		List<Element> ChildList = getChilds(ScenarioBehaviour);
		int childlistsize = ChildList.size();
		for (int i = 0; i < childlistsize; i++) {
			Element action_scenario = ChildList.get(i);
			if (action_scenario.hasChildNodes()) {
				action_scenariobehaviour(action_scenario, currprob);
			}
		}
		return true;
	}

	// this function analyzes received action: is it Loop, Internal Call or
	// probability-defined action
	// and calls corresponding functions for action (Internal Call) or
	// subactions (all other types)
	private void action_scenariobehaviour(Element action_scenario,
			double currprob) {
		List<Element> ChildList = getChilds(action_scenario);
		int loopSpecification = 0;
		for (int i = 0; i < ChildList.size(); i++) {
			Element CurrentElem = ChildList.get(i);
			String NameOfElem = CurrentElem.getNodeName();
			if (NameOfElem.equalsIgnoreCase("branchTransitions_Branch")) {
				branch_transitions(CurrentElem, currprob);
			}
			if (NameOfElem
					.equalsIgnoreCase("operationSignature__EntryLevelSystemCall")) {
				operation_signature(CurrentElem, currprob);
			}
			if (NameOfElem.equalsIgnoreCase("loopIteration_Loop")) {
				Node TempNode = CurrentElem.getAttributeNode("specification");
				loopSpecification = Integer.parseInt(TempNode.getNodeValue());
				for (int j = 0; j < ChildList.size(); j++) {
					Element SecondLvlElem = ChildList.get(j);
					String NameOfSecondLvlElem = SecondLvlElem.getNodeName();
					if (NameOfSecondLvlElem
							.equalsIgnoreCase("bodyBehaviour_Loop")) {
						body_behaviour_loop(SecondLvlElem, loopSpecification
								* currprob);
					}
				}
			}
		}
	}

	// action which has internal call
	// calculates Id of corresponding class and updates probabilities
	private void operation_signature(Element operat_sign_elem, double currprob) {
		Node Temp = operat_sign_elem.getAttributeNode("href");
		String hrefStr = Temp.getNodeValue();
		probabilityUpdate(hrefStr, currprob);
	}

	// as body_behaviour_loop has the same structure as scenario behavior
	// so it calls function scenariobehavior_usage to analyze received action
	private void body_behaviour_loop(Element body_behaviour_loop_elem,
			double currprob) {
		scenarioBehaviour_usage(body_behaviour_loop_elem, currprob);
	}

	// this function works with actions, that contain probability-defined
	// subactions
	// it calls functions for each subaction and multiplies probability of
	// current level on probability of subaction
	private void branch_transitions(Element branch_transit_elem, double currprob) {
		List<Element> ChildList = getChilds(branch_transit_elem);
		Node newprobStr = branch_transit_elem
				.getAttributeNode("branchProbability");
		double newprob = Double.parseDouble(newprobStr.getNodeValue());
		for (int i = 0; i < ChildList.size(); i++) {
			Element NewElem = ChildList.get(i);
			branched_behaviour(NewElem, newprob * currprob);
		}
	}

	// as branched_behaviour has the same structure as scenario behavior
	// so it calls function scenariobehavior_usage to analyze received action
	private void branched_behaviour(Element branched_behaviour_elem,
			double currprob) {
		scenarioBehaviour_usage(branched_behaviour_elem, currprob);
	}

	// saves current value of probability for class with Id = nameOfCall
	private void probabilityUpdate(String nameOfCall, double currprob) {
		int i = 0;
		while (!(resUsageList.ClassRepositoryId[i].equalsIgnoreCase(nameOfCall)))
			i++;
		resUsageList.probdistr[i] += currprob;
	}

	// normalization of probability arrays (they are calculated as frequencies)
	private void normalizeProb() {
		double sum = 0;
		for (int i = 0; i < classescount; i++)
			sum += resUsageList.probdistr[i];
		for (int i = 0; i < classescount; i++)
			resUsageList.probdistr[i] = resUsageList.probdistr[i] / sum;
	}

	// function removes dublicates from CurrentList
	private List<Element> CheckedListOfCalls(List<Element> CurrentList) {
		List<Element> res = new ArrayList<Element>();
		for (int i = 0; i < CurrentList.size(); i++) {
			boolean check = false;
			Element E1 = CurrentList.get(i);
			String Str1 = E1.getAttribute("href");
			for (int j = i + 1; j < CurrentList.size(); j++) {
				Element E2 = CurrentList.get(j);
				String Str2 = E2.getAttribute("href");
				if (Str1.equalsIgnoreCase(Str2))
					check = true;
			}
			if (!check)
				res.add(E1);
		}
		return res;
	}

	// extracting Ids of repository services (this function deletes part before
	// # in every Id)
	private void convertRepositoryIds() {
		for (int i = 0; i < classescount; i++) {
			int Symbol = resUsageList.ClassRepositoryId[i].indexOf('#');
			resUsageList.ClassRepositoryId[i] = resUsageList.ClassRepositoryId[i]
					.substring(Symbol + 1);
		}
	}
}
