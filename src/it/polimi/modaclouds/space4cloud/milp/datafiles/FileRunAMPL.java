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

import it.polimi.modaclouds.space4cloud.milp.Configuration;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.Scanner;

//this class creates local copy of AMPL.run file
//it is created and uploaded each time to have possibility to set CPLEX solver time limit
public class FileRunAMPL extends FileRun {

	// main function
	// file saves in AMPLrunFilePath
	// TimeLimit is used to set CPLEX solver time limit
	// UploadPath - directory om AMPL server (as it is required to use command
	// "cd UploadPath" in AMPL
	@Override
	public void print(String AMPLrunFilePath, String TimeLimit,
			String UploadPath, String FilePathStartingSolution) {
		
		try {
			PrintWriter out = new PrintWriter(new FileWriter(Paths.get(Configuration.LOCAL_TEMPORARY_FOLDER, AMPLrunFilePath).toFile()));
			
			String baseFile = ""; //new String(Files.readAllBytes(Paths.get(Configuration.DEFAULTS_FOLDER, Configuration.RUN_FILE))); //, Charset.defaultCharset()); // StandardCharsets.UTF_8);
			
//			String baseFile = new String(Files.readAllBytes(Paths.get(this.getClass().getResource(Configuration.RUN_FILE).toURI())));
			
			Scanner sc = new Scanner(Configuration.getStream(Configuration.RUN_FILE));
			
			while (sc.hasNextLine())
				baseFile += sc.nextLine() + "\n";
			
			sc.close();
			
			out.printf(baseFile,
					Configuration.RUN_WORKING_DIRECTORY,
					FilePathStartingSolution != null ? Configuration.RUN_MODEL_STARTING_SOLUTION : Configuration.RUN_MODEL_STANDARD,
					Configuration.RUN_DATA,
					Configuration.RUN_SOLVER,
					Configuration.RUN_LOG,
					Configuration.RUN_RES
					);
			
			out.flush();
			out.close();
		} catch (Exception e) {
			return;
		}
	}
	
	public static void print() {
		FileRunAMPL newAMPLrun = new FileRunAMPL();
		newAMPLrun.print(Configuration.RUN_FILE,
				Configuration.SolverTimeLimit, Configuration.RUN_WORKING_DIRECTORY, Configuration.FilePathStartingSolution);
	}
}
