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
package it.polimi.modaclouds.space4clouds.milp;

import java.io.File;




public class MainTest {
	
	public static final String PROJECT_PATH = "C:\\Users\\Riccardo\\Desktop\\SPACE4CLOUD\\workspace\\Piccolo3"; //runtime-New_configuration\\OfBiz"; //"C:\\Users\\GiovanniPaolo\\Workspaces\\runtime-SPACE4CLOUD\\OfBizSimple"; 
	public static final String WORKING_DIRECTORY = "space4cloud";
	public static final String RESOURCE_MODEL = PROJECT_PATH + "\\default.resourceenvironment";
	public static final String USAGE_MODEL = PROJECT_PATH + "\\default.usagemodel";
	public static final String ALLOCATION_MODEL = PROJECT_PATH + "\\default.allocation";
	public static final String REPOSITORY_MODEL = PROJECT_PATH + "\\default.repository";
	public static final String USAGE_MODEL_EXTENSION = PROJECT_PATH + "//ume-1000.xml"; //"\\OfBiz-UsageExtension.xml";
	public static final String CONSTRAINT = PROJECT_PATH + "\\OfBiz-Constraint.xml";
	

	public static void main(String[] args) {
		
		//		String workingDirectory = "space4cloud\\";
		//		String constraintFile    = projectPath + "OfBiz-Constraint.xml";
		//		String usageModelExtFile = projectPath + "OfBiz-UsageExtension.xml";
		//
		//		Solver s = new Solver(projectPath, workingDirectory, constraintFile, usageModelExtFile);
		//		
		////		s.setProviders("Amazon", "Microsoft");
		//		s.setProviders("Amazon");
		////		s.setProviders("Flexiscale");
		////		s.setRegions("us-east");
		////		s.setDeleteTempFiles(false);
		//		s.getOptions().SqlDBUrl="jdbc:mysql://resourcemodel.cloudapp.net:3306/";
		//		s.getOptions().DBName = "cloud";
		//		s.getOptions().DBUserName="moda";
		//		s.getOptions().DBPassword="modaclouds";
		//		s.setStartingSolution(Paths.get(projectPath, "solution-amamicr-1000-2.xml").toFile());
		////		s.setStartingSolution(Paths.get(projectPath, "solution-amazon-1000-2.xml").toFile());
		//		
		//		System.out.println(s.getResourceModelExt().getAbsolutePath());
		//		System.out.println(s.getSolution().getAbsolutePath());
		//		System.out.println(s.getMultiCloudExt().getAbsolutePath());



		// resourceEnvExtFile = null;
		// programLogger.warn("Generation of the first solution disabled at the moment!");

		// ///////////////////////////
		RussianEvaluator.setSSH_HOST("ch14r4.dei.polimi.it");
		RussianEvaluator.setSSH_PASSWORD("modaclouds2014");
		RussianEvaluator.setSSH_USER_NAME("specmeter");
		RussianEvaluator re = new RussianEvaluator(new File(USAGE_MODEL_EXTENSION), new File(CONSTRAINT));
		String[] providersInitial = {"Microsoft"}; //{"Amazon"}; //, "Microsoft", "Flexiscale"};
		re.setProviders(providersInitial);
		
//		re.setStartingSolution(new File(PROJECT_PATH + "\\soltmp.xml"));

		try {
			re.eval();
		} catch (Exception e) {
			System.err.println("Error! It's impossible to generate the solution! Are you connected?");
			e.printStackTrace();
			return;
		}

		// override values provided with those generated by the initial solution
		File resourceEnvExtFile = re.getResourceEnvExt();
		File initialSolution = re.getSolution();
		File initialMce = re.getMultiCloudExt();

		System.out.println("Generated resource model extension: "
				+ resourceEnvExtFile.getAbsolutePath());
		System.out.println("Generated solution: "
				+ initialSolution.getAbsolutePath());
		System.out.println("Generated multi cloud extension: "
				+ initialMce.getAbsolutePath());
		System.out.println("Cost: " + re.getCost() + ", computed in: "
				+ re.getEvaluationTime() + " ms");

	}

}
