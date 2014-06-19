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
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

//this class reads model constraint file
public class ConstraintXML {

	// path to file with model constraints
	public String FilePathConstraint = "";

	// IDs of containers
	public List<String> Ids = null;
	// memory demands of containers
	public List<String> MemoryConstValues = null;

	// XML document
	private Document maindoc;

	// is true, if document was loaded
	public boolean loadrez = false;

	// root element of loaded document
	private Element root;

	// constructors
	public ConstraintXML(String FPConstr) {
		FilePathConstraint = FPConstr;
		if (!FilePathConstraint.equalsIgnoreCase(""))
			loadModel(FilePathConstraint);
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

	// returns memory demand of container by id of container
	public String getMemoryConstById(String ContainerId) {
		String MemoryConst = "0";
		int i = 0;
		while ((i < Ids.size()) && (!ContainerId.equalsIgnoreCase(Ids.get(i))))
			i++;
		if (i < Ids.size())
			MemoryConst = MemoryConstValues.get(i);
		return MemoryConst;
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

	// receives constraints from the file
	public int extractConstraints() {
		// initialization
		Ids = new ArrayList<String>();
		MemoryConstValues = new ArrayList<String>();

		// check that document was loaded
		if (!loadrez)
			return 1;

		// receiving list of constraints
		List<Element> NewConstrList = getElements(root, "constraint");
		for (int i = 0; i < NewConstrList.size(); i++) {
			// for every container receiving its metric
			Element NewConstr = NewConstrList.get(i);
			List<Element> NewListMetric = getElements(NewConstr, "metric");
			Element NewMetric = NewListMetric.get(0);
			String MetricStr = NewMetric.getTextContent();
			// if metric = RAM then it is memory constraint
			if (MetricStr.equalsIgnoreCase("RAM")) {
				// receiving id of container
				List<Element> NewListIds = getElements(NewConstr,
						"targetResourceIDRef");
				Element NewId = NewListIds.get(0);
				String IdStr = NewId.getTextContent();
				Ids.add(IdStr);

				// receiving memory constraint of container
				List<Element> NewListMaxValues = getElements(NewConstr,
						"hasMinValue");
				Element NewMaxValue = NewListMaxValues.get(0);
				String MaxValueStr = NewMaxValue.getTextContent();
				MemoryConstValues.add(MaxValueStr);
			}
		}
		return 0;
	}
	
	private List<Double> getMaxResponseTimes() {
		ArrayList<Double> responseTimes = new ArrayList<Double>();
		
		// check that document was loaded
		if (!loadrez)
			return responseTimes;

		// receiving list of constraints
		List<Element> newConstrList = getElements(root, "constraint");
		for (int i = 0; i < newConstrList.size(); i++) {
			// for every container receiving its metric
			Element newConstr = newConstrList.get(i);
			List<Element> newListMetric = getElements(newConstr, "metric");
			Element newMetric = newListMetric.get(0);
			String metric = newMetric.getTextContent();
			// if metric = RAM then it is memory constraint
			if (metric.equalsIgnoreCase("ResponseTime")) {
				// receiving the unit
				List<Element> newListMaxValues = getElements(newConstr, "unit");
				Element newMaxValue = newListMaxValues.get(0);
				String unit = newMaxValue.getTextContent();
				
				
				// receiving response time constraints
				newListMaxValues = getElements(newConstr, "hasMaxValue");
				newMaxValue = newListMaxValues.get(0);
				double value = 0.0;
				try {
					value = (double)Integer.parseInt(newMaxValue.getTextContent());
				} catch (Exception e) {
					value = 0.0;
				}
				
				switch (unit) {
				case "ms":
					value *= 0.001;
				}
				
				responseTimes.add(value);
			}
		}
		
		return responseTimes;
	}
	
	public double getMaxMaxResponseTime() {
		List<Double> responseTimes = getMaxResponseTimes();
		
		if (responseTimes.size() == 0)
			return 0.0;
		
		double max = Double.MIN_VALUE;
		
		for (double d : responseTimes)
			if (d > max)
				max = d;
		
		return max;
	}
	
	public double getMinMaxResponseTime() {
		List<Double> responseTimes = getMaxResponseTimes();
		
		if (responseTimes.size() == 0)
			return 0.0;
		
		double min = Double.MAX_VALUE;
		
		for (double d : responseTimes)
			if (d < min)
				min = d;
		
		return min;
	}
	
	public double getAvgMaxResponseTime() {
		List<Double> responseTimes = getMaxResponseTimes();
		
		if (responseTimes.size() == 0)
			return 0.0;
		
		double sum = 0.0;
		
		for (double d : responseTimes)
			sum += d;
		
		return sum/responseTimes.size();
	}
	
}
