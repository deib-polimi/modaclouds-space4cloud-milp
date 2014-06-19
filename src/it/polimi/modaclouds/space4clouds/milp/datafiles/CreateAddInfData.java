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
package it.polimi.modaclouds.space4clouds.milp.datafiles;

import it.polimi.modaclouds.qos_models.schema.ClosedWorkload;
import it.polimi.modaclouds.qos_models.schema.ClosedWorkloadElement;
import it.polimi.modaclouds.qos_models.schema.OpenWorkload;
import it.polimi.modaclouds.qos_models.schema.OpenWorkloadElement;
import it.polimi.modaclouds.qos_models.schema.UsageModelExtensions;
import it.polimi.modaclouds.qos_models.util.XMLHelper;
import it.polimi.modaclouds.space4clouds.milp.xmldatalists.AddInfList;
import it.polimi.modaclouds.space4clouds.milp.xmlfiles.ConstraintXML;

import java.io.File;
import java.util.List;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

//this class is used to create all necessary additional data:
//arrival rate; minimum arrival rate per provider; border on system response time; minimum number of providers
public class CreateAddInfData {

	// main container for parsed data
	public AddInfList newAddInfList = null;

	// saves data from newAddInfList class to file AddInfFilePath
	public void printAddInfData(String AddInfFilePath) {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();

			Element rootElement = doc.createElement("AddInf");
			doc.appendChild(rootElement);

			Element ArRate = doc.createElement("Arrival_Rate");
			rootElement.appendChild(ArRate);

			for (int i = 0; i < newAddInfList.countTimeIntervals; i++) {
				Element ArrRateValue = doc.createElement("Arrival_Rate_Value");
				ArRate.appendChild(ArrRateValue);

				Attr attr = doc.createAttribute("Time_Interval");
				attr.setValue(Integer.toString(newAddInfList.TimeIndex[i]));
				ArrRateValue.setAttributeNode(attr);

				Attr attr2 = doc.createAttribute("Value");
				attr2.setValue(Double.toString(newAddInfList.arrivalrate[i]));
				ArrRateValue.setAttributeNode(attr2);
			}

			Element MinArRate = doc.createElement("Min_Arrival_Rate");
			rootElement.appendChild(MinArRate);

			for (int i = 0; i < newAddInfList.countProviders; i++) {
				Element MinArrRateValue = doc
						.createElement("Min_Arrival_Rate_Value");
				MinArRate.appendChild(MinArrRateValue);

				Attr attr = doc.createAttribute("Provider");
				attr.setValue(newAddInfList.ProviderNames[i]);
				MinArrRateValue.setAttributeNode(attr);

				Attr attr2 = doc.createAttribute("Value");
				attr2.setValue(Double.toString(newAddInfList.MinArrRate[i]));
				MinArrRateValue.setAttributeNode(attr2);
			}

			Element MinProv = doc.createElement("Minimal_amount_of_providers");
			rootElement.appendChild(MinProv);
			Attr attr3 = doc.createAttribute("Value");
			attr3.setValue(Integer.toString(newAddInfList.MinProv));
			MinProv.setAttributeNode(attr3);

			Element MaxSystemResponseTime = doc
					.createElement("Max_system_response_time");
			rootElement.appendChild(MaxSystemResponseTime);
			Attr attr4 = doc.createAttribute("Value");
			attr4.setValue(Double.toString(newAddInfList.MaxSystemResponseTime));
			MaxSystemResponseTime.setAttributeNode(attr4);
			
			Element ThTime = doc.createElement("Think_Time");
			rootElement.appendChild(ThTime);

			for (int i = 0; i < newAddInfList.countTimeIntervals; i++) {
				Element ThinkTimeValue = doc.createElement("Think_Time_Value");
				ThTime.appendChild(ThinkTimeValue);

				Attr attr = doc.createAttribute("Time_Interval");
				attr.setValue(Integer.toString(newAddInfList.TimeIndex[i]));
				ThinkTimeValue.setAttributeNode(attr);

				Attr attr2 = doc.createAttribute("Value");
				attr2.setValue(Double.toString(newAddInfList.thinkTimes[i]));
				ThinkTimeValue.setAttributeNode(attr2);
			}

			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(AddInfFilePath));
			transformer.transform(source, result);

		} catch (ParserConfigurationException | TransformerException e) {
			e.printStackTrace();
		}
	}

	// generates new data in newAddInfList
	// List<String> ProviderNames is used to set minimum arrival rate values in
	// form (ProviderName-value)
	// TimeIntervals - amount of time intervals
	// ProvidersCount - amount of providers
	// AmplitudeAR - max value (without noise) of arrival rate
	// AmplitudeMAR - max value of minimum arrival rate per provider
	// MinProvVal - minimum amount of providers
	// MSRT - maximum system response time
	public void generateData(List<String> ProviderNames, int ProvidersCount,
			double AmplitudeAR, double AmplitudeMAR, int MinProvVal, double MSRT) {
		
		newAddInfList = new AddInfList();
		newAddInfList.setTimeIntervalsCount(24);
		newAddInfList.setProvidersCount(ProvidersCount);
		Random randomGenerator = new Random();

		// sets daily proportions of arrivals
		double[] arrate = new double[] { 0.1, 0.1, 0.1, 0.1, 0.1, 0.2, 0.38,
				0.6, 0.8, 0.7, 0.7, 0.9, 1, 0.8, 0.58, 0.4, 0.25, 0.15, 0.1,
				0.1, 0.1, 0.1, 0.1, 0.1 };
		for (int i = 0; i < 24; i++) {
			// generates arrival rate with noise. Noise increases with
			// increasing of daily proportion of arrivals
			newAddInfList.arrivalrate[i] = arrate[i]
					* (0.5 + randomGenerator.nextDouble()) * AmplitudeAR;
			newAddInfList.TimeIndex[i] = i + 1;
			newAddInfList.thinkTimes[i] = 10.0;
		}
		for (int i = 0; i < ProvidersCount; i++) {
			// generates minimum arrival rate per provider
			newAddInfList.MinArrRate[i] = randomGenerator.nextDouble()
					* AmplitudeMAR;
			newAddInfList.ProviderNames[i] = ProviderNames.get(i);
		}
		newAddInfList.MinProv = MinProvVal;
		newAddInfList.MaxSystemResponseTime = MSRT;
	}
	
	public AddInfList getAddInfList() {
		return newAddInfList;
	}
	
	/**
	 * Generate the data starting from the Usage Model Extension file, using the Constraint file.
	 * 
	 * @param ProviderNames
	 * @param ProvidersCount
	 * @param usageModelExtFile
	 * @param AmplitudeMAR
	 * @param MinProvVal
	 * @param constraintFile
	 */
	public void generateData(List<String> ProviderNames, int ProvidersCount,
			String usageModelExtFile, double AmplitudeMAR, int MinProvVal, String constraintFile) {
		newAddInfList = new AddInfList();
		newAddInfList.setTimeIntervalsCount(24);
		newAddInfList.setProvidersCount(ProvidersCount);
		Random randomGenerator = new Random();
		
		UsageModelExtensions umes = null;
		try {
			umes = XMLHelper.deserialize(new File(usageModelExtFile).toURI().toURL(),
					UsageModelExtensions.class);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		if (constraintFile != null && constraintFile.length() > 0) {
			ConstraintXML constraints = new ConstraintXML(constraintFile);
			newAddInfList.MaxSystemResponseTime = constraints.getAvgMaxResponseTime();
		} else {
			newAddInfList.MaxSystemResponseTime = 0.4;
		}
		
		ClosedWorkload cw = umes.getUsageModelExtension().getClosedWorkload();
		if (cw != null) {
			for (ClosedWorkloadElement we : cw.getWorkloadElement()) {
				int hour = we.getHour();
//				newAddInfList.arrivalrate[hour-1] = (double)we.getPopulation() / (10 + newAddInfList.MaxSystemResponseTime);
				newAddInfList.arrivalrate[hour-1] = (double)we.getPopulation() / we.getThinkTime();
				newAddInfList.thinkTimes[hour-1] = we.getThinkTime();
				newAddInfList.TimeIndex[hour-1] = hour;
			}
		}
		else {
			
			OpenWorkload ow = umes.getUsageModelExtension().getOpenWorkload();
			if (ow != null) {
				for (OpenWorkloadElement we : ow.getWorkloadElement()) {
					int hour = we.getHour();
//					newAddInfList.arrivalrate[hour-1] = (double)we.getPopulation() / (10 + newAddInfList.MaxSystemResponseTime);
					newAddInfList.arrivalrate[hour-1] = (double)we.getPopulation() / 10;
					newAddInfList.thinkTimes[hour-1] = 10.0;
					newAddInfList.TimeIndex[hour-1] = hour;
				}
			}
			else {
				return;
			}
		}
		
		for (int i = 0; i < ProvidersCount; i++) {
			// generates minimum arrival rate per provider
			newAddInfList.MinArrRate[i] = randomGenerator.nextDouble()
					* AmplitudeMAR;
			newAddInfList.ProviderNames[i] = ProviderNames.get(i);
		}
		newAddInfList.MinProv = MinProvVal;
//		newAddInfList.MaxSystemResponseTime = MSRT;
	}
}
