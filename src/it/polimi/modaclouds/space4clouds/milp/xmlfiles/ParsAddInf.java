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

import it.polimi.modaclouds.space4clouds.milp.xmldatalists.AddInfList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

//this class is used to parse file with AddInfData
public class ParsAddInf {

	// container for information from file AddInfData
	public AddInfList newAddInfList = new AddInfList();

	// XML document
	private Document maindoc;

	// is true, if document was loaded
	private boolean loadrez = false;

	// root element of loaded document
	private Element root;

	// constructors
	public ParsAddInf(String NewAddInfFilePath) {
		loadModel(NewAddInfFilePath);
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

	public int analyse_doc() {
		// check that document was loaded
		if (!loadrez)
			return 1;

		// receiving lists of parameters
		List<Element> NewArrivalRateList = getElements(root,
				"Arrival_Rate_Value");
		List<Element> NewMinArrivalRateList = getElements(root,
				"Min_Arrival_Rate_Value");
		List<Element> NewMinProv = getElements(root,
				"Minimal_amount_of_providers");
		List<Element> NewMaxSystemResponseTime = getElements(root,
				"Max_system_response_time");
		List<Element> NewThinkTimeList = getElements(root,
				"Think_Time_Value");

		// sets amounts of providers and time intervals
		newAddInfList.setProvidersCount(NewMinArrivalRateList.size());
		newAddInfList.setTimeIntervalsCount(NewArrivalRateList.size());

		// receives value of minimum amount of providers
		Element MinProvElement = NewMinProv.get(0);
		String MinProvStr = MinProvElement.getAttribute("Value");
		newAddInfList.MinProv = Integer.parseInt(MinProvStr);

		// receives value of minimum amount of maximum system response time
		Element MaxSystemResponseTimeElement = NewMaxSystemResponseTime.get(0);
		String MaxSystemResponseTimeStr = MaxSystemResponseTimeElement
				.getAttribute("Value");
		newAddInfList.MaxSystemResponseTime = Double
				.parseDouble(MaxSystemResponseTimeStr);

		// receives values of minimum arrival rate per provider
		for (int i = 0; i < newAddInfList.countProviders; i++) {
			Element MinArrivalRateElement = NewMinArrivalRateList.get(i);
			newAddInfList.ProviderNames[i] = MinArrivalRateElement
					.getAttribute("Provider");
			String StrMArrRate = MinArrivalRateElement.getAttribute("Value");
			newAddInfList.MinArrRate[i] = Double.parseDouble(StrMArrRate);
		}

		// receives values of arrival rate
		for (int i = 0; i < newAddInfList.countTimeIntervals; i++) {
			Element ArrivalRateElement = NewArrivalRateList.get(i);
			String StrTimeIndex = ArrivalRateElement
					.getAttribute("Time_Interval");
			newAddInfList.TimeIndex[i] = Integer.parseInt(StrTimeIndex);
			String StrArrRate = ArrivalRateElement.getAttribute("Value");
			newAddInfList.arrivalrate[i] = Double.parseDouble(StrArrRate);
		}
		
		for (int i = 0; i < newAddInfList.countTimeIntervals; i++) {
			Element ThinkTimeElement = NewThinkTimeList.get(i);
			String StrTimeIndex = ThinkTimeElement
					.getAttribute("Time_Interval");
			newAddInfList.TimeIndex[i] = Integer.parseInt(StrTimeIndex);
			String StrArrRate = ThinkTimeElement.getAttribute("Value");
			newAddInfList.thinkTimes[i] = Double.parseDouble(StrArrRate);
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
