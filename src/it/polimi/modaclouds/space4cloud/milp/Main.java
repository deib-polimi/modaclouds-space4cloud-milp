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
package it.polimi.modaclouds.space4cloud.milp;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
	
	private static final Logger logger = LoggerFactory.getLogger(Main.class);
	
	public static void doMain(String configuration, String solution, String[] providers) {
		if (configuration == null || !new File(configuration).exists())
			return;
		
		Solver.removeTempFiles = false;
		
		Solver s = null;
		
		if (solution != null)
			s = new Solver(configuration, solution);
		else
			s = new Solver(configuration);
		
		if (providers.length > 0)
			s.setProviders(providers);
		
		try {
			File resourceEnvExtFile = s.getResourceModelExt();
			File initialSolution = s.getSolution();
			File initialMce = s.getMultiCloudExt();
	
			logger.debug("Generated resource model extension: "
					+ resourceEnvExtFile.getAbsolutePath());
			logger.debug("Generated solution: "
					+ initialSolution.getAbsolutePath());
			logger.debug("Generated multi cloud extension: "
					+ initialMce.getAbsolutePath());
		} catch (Exception e) {
			logger.error("Error while computing the solution!", e);
		}
	}
	
	public static void mainInitialSolution(String[] args) {
		String basePath       = "/Users/ft/Development/workspace-s4c-runtime/Constellation/"; //"C:\\Users\\Riccardo\\Desktop\\SPACE4CLOUD\\runtime-New_configuration\\Constellation\\";
		String configuration  = basePath + "OptimizationMacLocal.properties"; //"conference-opt-2p.properties";
		String solution       = basePath + "ContainerExtensions/Computed/Solution-Conference-Amazon.xml";
		
		doMain(configuration, solution, new String[] {});
	}
	
	public static void mainStandard(String[] args) {
		String basePath       = "/Users/ft/Development/workspace-s4c-runtime/Constellation/"; //"C:\\Users\\Riccardo\\Desktop\\SPACE4CLOUD\\runtime-New_configuration\\Constellation\\";
		String configuration  = basePath + "OptimizationMacLocal.properties"; //"conference-opt-2p.properties";
		
//		String[] providers = {"CloudSigma"}; // , "Microsoft"};
		
		doMain(configuration, null, new String[] {}); //, providers);
		
	}
	
	public static void main(String[] args) {
		mainInitialSolution(args);
	}

}
