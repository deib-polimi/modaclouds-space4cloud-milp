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
package it.polimi.modaclouds.space4cloud.milp.datafiles;

import it.polimi.modaclouds.qos_models.schema.ClosedWorkload;
import it.polimi.modaclouds.qos_models.schema.ClosedWorkloadElement;
import it.polimi.modaclouds.qos_models.schema.OpenWorkload;
import it.polimi.modaclouds.qos_models.schema.OpenWorkloadElement;
import it.polimi.modaclouds.qos_models.schema.UsageModelExtensions;
import it.polimi.modaclouds.qos_models.util.XMLHelper;
import it.polimi.modaclouds.space4cloud.milp.Configuration;
import it.polimi.modaclouds.space4cloud.milp.xmldatalists.AddInfList;
import it.polimi.modaclouds.space4cloud.milp.xmlfiles.ConstraintXML;

import java.io.File;
import java.nio.file.Paths;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

//this class is used to create all necessary additional data:
//arrival rate; minimum arrival rate per provider; border on system response time; minimum number of providers
public class CreateAddInfData {
	
	private static final Logger logger = LoggerFactory.getLogger(CreateAddInfData.class);

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
			StreamResult result = new StreamResult(Paths.get(Configuration.LOCAL_TEMPORARY_FOLDER, AddInfFilePath).toFile());
			transformer.transform(source, result);

		} catch (ParserConfigurationException | TransformerException e) {
			logger.error("Error while writing the add inf data.", e);
		}
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
		ConstraintXML constraints = null;
		UsageModelExtensions umes = null;
		try {
			umes = XMLHelper.deserialize(new File(usageModelExtFile).toURI().toURL(),
					UsageModelExtensions.class);
		} catch (Exception e) {
			logger.error("Error while reading the usage model extension file.", e);
			return;
		}
		
		if (constraintFile != null && constraintFile.length() > 0) {
			constraints = new ConstraintXML(constraintFile);
			
			int tmp = constraints.getMinimumProviders();
			if (tmp > -1)
				MinProvVal = tmp;
			
			newAddInfList.MaxSystemResponseTime = constraints.getAvgMaxResponseTime();
		} else {
			newAddInfList.MaxSystemResponseTime = 0.4;
		}
		
		ClosedWorkload cw = umes.getUsageModelExtension().getClosedWorkload();
		if (cw != null) {
			for (ClosedWorkloadElement we : cw.getWorkloadElement()) {
				int hour = we.getHour();
//				newAddInfList.arrivalrate[hour] = (double)we.getPopulation() / (10 + newAddInfList.MaxSystemResponseTime);
				newAddInfList.arrivalrate[hour] = (double)we.getPopulation() / we.getThinkTime();
				newAddInfList.thinkTimes[hour] = we.getThinkTime();
				newAddInfList.TimeIndex[hour] = hour;
			}
		}
		else {
			
			OpenWorkload ow = umes.getUsageModelExtension().getOpenWorkload();
			if (ow != null) {
				for (OpenWorkloadElement we : ow.getWorkloadElement()) {
					int hour = we.getHour();
//					newAddInfList.arrivalrate[hour] = (double)we.getPopulation() / (10 + newAddInfList.MaxSystemResponseTime);
					newAddInfList.arrivalrate[hour] = (double)we.getPopulation() / 10;
					newAddInfList.thinkTimes[hour] = 10.0;
					newAddInfList.TimeIndex[hour] = hour;
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
			if(constraints != null)
				newAddInfList.MinArrRate[i] = constraints.getAvgWorkloadPercentage();
			newAddInfList.ProviderNames[i] = ProviderNames.get(i);
		}
		newAddInfList.MinProv = MinProvVal;
//		newAddInfList.MaxSystemResponseTime = MSRT;
	}
}
