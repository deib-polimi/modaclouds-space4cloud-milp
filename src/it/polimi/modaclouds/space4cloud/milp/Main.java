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

public class Main {
	
	public static void mainInitialSolution(String[] args) {
		String basePath       = "C:\\Users\\Riccardo\\Desktop\\SPACE4CLOUD\\runtime-New_configuration\\OfBiz\\";
		String configuration  = basePath + "conf-private-1p.properties";
//		String solution       = basePath + "initial-solution-amazon.xml";
		String solution       = basePath + "initial-solution-amazon-broken.xml";
		
		Solver.removeTempFiles = false;
		
		Solver s = new Solver(configuration, solution);
		
		File resourceEnvExtFile = s.getResourceModelExt();
		File initialSolution = s.getSolution();
		File initialMce = s.getMultiCloudExt();

		System.out.println("Generated resource model extension: "
				+ resourceEnvExtFile.getAbsolutePath());
		System.out.println("Generated solution: "
				+ initialSolution.getAbsolutePath());
		System.out.println("Generated multi cloud extension: "
				+ initialMce.getAbsolutePath());
	}
	
	public static void mainStandard(String[] args) {
		String basePath       = "C:\\Users\\Riccardo\\Desktop\\SPACE4CLOUD\\runtime-New_configuration\\OfBiz\\";
		String configuration  = basePath + "conf-private-1p.properties";
		
		Solver.removeTempFiles = false;
		
		Solver s = new Solver(configuration);
		
		s.setProviders("Amazon"); //, "Microsoft");
		
		File resourceEnvExtFile = s.getResourceModelExt();
		File initialSolution = s.getSolution();
		File initialMce = s.getMultiCloudExt();

		System.out.println("Generated resource model extension: "
				+ resourceEnvExtFile.getAbsolutePath());
		System.out.println("Generated solution: "
				+ initialSolution.getAbsolutePath());
		System.out.println("Generated multi cloud extension: "
				+ initialMce.getAbsolutePath());
	}
	
	public static void main(String[] args) {
		mainStandard(args);
	}

}
