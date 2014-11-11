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

import java.nio.file.Paths;
import java.util.LinkedHashMap;

//this class reads model constraint file
public class ConstraintXML {

	// path to file with model constraints
	public String FilePathConstraint = "";

	// is true, if document was loaded
	public boolean loadrez = false;

	private LinkedHashMap<String, Float> memoryConstraints = new LinkedHashMap<String, Float>();
	private LinkedHashMap<String, Float> responseTimesConstraints = new LinkedHashMap<String, Float>();
	private LinkedHashMap<String, Float> availabilitiesConstraints = new LinkedHashMap<String, Float>();
	private LinkedHashMap<String, Float> workloadPercentagesConstraints = new LinkedHashMap<String, Float>();

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
				
				String metric = cons.getMetric();
				String target = cons.getTargetResourceIDRef();
				Float minValue = cons.getRange().getHasMinValue();
				Float maxValue = cons.getRange().getHasMaxValue();
				
				switch (metric) {
				case "RAM":
					memoryConstraints.put(target, minValue);
					break;
				case "ResponseTime":
					responseTimesConstraints.put(target, new Float(maxValue * 0.001)); // saved in seconds where expressed in ms
					break;
				case "Availability":
					availabilitiesConstraints.put(target, new Float(minValue * 0.01)); // saved with a value from 0 to 1 where expressed in %
					break;
				case "WorkloadPercentage":
					workloadPercentagesConstraints.put(target, new Float(minValue * 0.01)); // saved with a value from 0 to 1 where expressed in %
					break;
				}
			}
			
			loadrez = true;
		} catch (Exception e) {
//			e.getMessage();
			e.printStackTrace();
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
			return 0.0;
		
		double sum = 0.0;
		
		for (double d : workloadPercentagesConstraints.values())
			sum += d;
		
		return sum/workloadPercentagesConstraints.size();
	}
	
}
