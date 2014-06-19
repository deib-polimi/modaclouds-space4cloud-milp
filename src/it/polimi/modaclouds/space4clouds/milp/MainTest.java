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

import java.nio.file.Paths;



public class MainTest {

	public static void main(String[] args) {
		String projectPath = "C:\\Users\\Riccardo\\Desktop\\SPACE4CLOUD\\runtime-New_configuration\\OfBiz-bis\\";
		String workingDirectory = "space4cloud\\";
		String constraintFile    = projectPath + "OfBiz-Constraint.xml";
		String usageModelExtFile = projectPath + "ume-1000.xml";

		Solver s = new Solver(projectPath, workingDirectory, constraintFile, usageModelExtFile);
		
		s.setProviders("Amazon", "Microsoft");
//		s.setProviders("Amazon");
//		s.setProviders("Flexiscale");
//		s.setRegions("us-east");
//		s.setDeleteTempFiles(false);
		s.getOptions().SqlDBUrl="jdbc:mysql://localhost:3306/";
		s.getOptions().DBName = "cloud_full";
		s.getOptions().DBUserName="moda";
		s.getOptions().DBPassword="modaclouds";
		s.setStartingSolution(Paths.get(projectPath, "solution-amamicr-1000-2.xml").toFile());
//		s.setStartingSolution(Paths.get(projectPath, "solution-amazon-1000-2.xml").toFile());
		
		System.out.println(s.getResourceModelExt().getAbsolutePath());
		System.out.println(s.getSolution().getAbsolutePath());
		System.out.println(s.getMultiCloudExt().getAbsolutePath());
	}
	
}
