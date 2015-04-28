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

import it.polimi.modaclouds.qos_models.schema.Constraint;
import it.polimi.modaclouds.qos_models.schema.Constraints;
import it.polimi.modaclouds.qos_models.util.XMLHelper;
import it.polimi.modaclouds.space4cloud.milp.Configuration;

import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//this class reads model constraint file
public class ConstraintXML {
	
	private static final Logger logger = LoggerFactory.getLogger(ConstraintXML.class);

	// path to file with model constraints
	public String FilePathConstraint = "";

	// is true, if document was loaded
	public boolean loadrez = false;

	private LinkedHashMap<String, Float> memoryConstraints = new LinkedHashMap<String, Float>();
	private LinkedHashMap<String, Float> responseTimesConstraints = new LinkedHashMap<String, Float>();
	private LinkedHashMap<String, Float> availabilitiesConstraints = new LinkedHashMap<String, Float>();
	private LinkedHashMap<String, Float> workloadPercentagesConstraints = new LinkedHashMap<String, Float>();
	
	private int minimumProviders = -1;

	// constructors
	public ConstraintXML(String FPConstr) {
		FilePathConstraint = FPConstr;

		extractConstraints();
	}

	private Constraints loadedConstraints;

	// loads XML document in DOM parser
	public boolean extractConstraints() {
		if (loadrez)
			return true;

		try {
			loadedConstraints = XMLHelper.deserialize(Paths.get(FilePathConstraint).toUri().toURL(),Constraints.class);

			for (Constraint cons : loadedConstraints.getConstraints()) {

				Metric metric = Metric.getMetricFromTag(cons.getMetric());
				String target = cons.getTargetResourceIDRef().trim();
				Float minValue = cons.getRange().getHasMinValue();
				Float maxValue = cons.getRange().getHasMaxValue();
				String aggregation = "";
				if (cons.getMetricAggregation() != null)
					aggregation = cons.getMetricAggregation().getAggregateFunction().trim();
				if(metric != null){
					switch (metric) {
					case RAM:
						memoryConstraints.put(target, minValue);
						break;
					case RESPONSETIME:
						if (aggregation.equals("Average"))
							responseTimesConstraints.put(target, new Float(maxValue * 0.001)); // saved in seconds where expressed in ms
						break;
					case AVAILABILITY:
						availabilitiesConstraints.put(target, new Float(minValue * 0.01)); // saved with a value from 0 to 1 where expressed in %
						break;
					case WORKLOADPERCENTAGE:
						workloadPercentagesConstraints.put(target, new Float(minValue * 0.01)); // saved with a value from 0 to 1 where expressed in %
						break;
					case NUMBERPROVIDERS:
						minimumProviders = minValue.intValue();
						break;
					default:
						break;
					}
				}
			}

			loadrez = true;
		} catch (Exception e) {
			//			e.getMessage();
			logger.error("Error while reading the constraints file.", e);
			loadrez = false;
		}
		return loadrez;
	}

	// returns memory demand of container by id of container
	public double getMemoryConstById(String ContainerId) {
		Float res = memoryConstraints.get(ContainerId);
		if (res == null)
			return 0.0;
		else
			return res;
	}

	public double getAvgMinAvailability() {
		if (availabilitiesConstraints.size() == 0)
			return 0.01;

		double sum = 0.0;

		for (double d : availabilitiesConstraints.values())
			sum += d;

		return sum/availabilitiesConstraints.size();
	}

	public double getAvgMaxResponseTime() {
		if (responseTimesConstraints.size() == 0)
			return 0.0;

		double sum = 0.0;

		for (double d : responseTimesConstraints.values())
			sum += d;

		return sum/responseTimesConstraints.size();
	}

	public double getAvgWorkloadPercentage() {
		if (workloadPercentagesConstraints.size() == 0)
			return new Random().nextDouble()*Configuration.MMAR;

		double sum = 0.0;

		for (double d : workloadPercentagesConstraints.values())
			sum += d;

		return sum/workloadPercentagesConstraints.size();
	}
	
	public int getMinimumProviders() {
		return minimumProviders;
	}

	public enum Metric {
		REPLICATION("Replication"), RAM("RAM"), HDD("HardDisk"), CORES("Cores"), CPU(
				"CPUUtilization"), MACHINETYPE("MachineType"), SERVICETYPE(
						"ServiceType"), RESPONSETIME("ResponseTime"), AVAILABILITY(
								"Availability"), RELIABILITY("Reliability"), WORKLOADPERCENTAGE("WorkloadPercentage"),
								NUMBERPROVIDERS("NumberProviders");

		public static Metric getMetricFromTag(String tag) {
			switch (tag) {
			case "Replication":
				return REPLICATION;
			case "RAM":
				return RAM;
				//TODO:not supported
			case"HardDisk":
				return HDD;
				//TODO:not supported
			case "Cores":
				return CORES;
			case "CPUUtilization":
				return CPU;
				//TODO:not supported
			case "MachineType":
				return MACHINETYPE;
				//TODO:not supported
			case "ServiceType":
				return SERVICETYPE;
			case "ResponseTime":
				return RESPONSETIME;
				//TODO:not supported
			case "Availability":
				return AVAILABILITY;
				//TODO:not supported
			case "Reliability":
				return RELIABILITY;
			case "WorkloadPercentage":
				return WORKLOADPERCENTAGE;
			case "NumberProviders":
				return NUMBERPROVIDERS;
			default:
				return null;
			}
		}

		private String xmlTag; // the tag of the type attribute in the xml file

		private Metric(String xmlTag) {
			this.xmlTag = xmlTag;
		}

		public String getXmlTag() {
			return xmlTag;
		}

		public static String getSupportedMetricNames() {
			String value="";
			for (Metric m : Metric.values()) {
				value 	+= m.getXmlTag()+" ";
			}
			return value;
		}
	}

}
