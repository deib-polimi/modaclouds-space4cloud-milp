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
package it.polimi.modaclouds.space4cloud.milp.ssh;

import it.polimi.modaclouds.space4cloud.milp.Configuration;

//this class is used to create connection to AMPL server (wrapper)
public class SshConnector {

	// main execution function
	public static void run() {
		// this block uploads files data.dat and AMPL.run on AMPL server
		ScpTo newScpTo = new ScpTo();
		newScpTo.sendfile(Configuration.RUN_DATA, Configuration.RUN_WORKING_DIRECTORY + "/" + Configuration.RUN_DATA);
		newScpTo.sendfile(Configuration.RUN_FILE, Configuration.RUN_WORKING_DIRECTORY + "/" + Configuration.RUN_FILE);
		
		newScpTo.sendfile(Configuration.RUN_MODEL_STANDARD, Configuration.RUN_WORKING_DIRECTORY + "/" + Configuration.RUN_MODEL_STANDARD);
		newScpTo.sendfile(Configuration.RUN_MODEL_STARTING_SOLUTION, Configuration.RUN_WORKING_DIRECTORY + "/" + Configuration.RUN_MODEL_STARTING_SOLUTION);

		// this block runs bash-script on AMPL server
		ExecSSH newExecSSH = new ExecSSH();
		newExecSSH.mainExec();

		// this block downloads logs and results of AMPL
		ScpFrom newScpFrom = new ScpFrom();
		newScpFrom.receivefile(Configuration.RUN_LOG, Configuration.RUN_WORKING_DIRECTORY + "/" + Configuration.RUN_LOG);
		newScpFrom.receivefile(Configuration.RUN_RES, Configuration.RUN_WORKING_DIRECTORY + "/" + Configuration.RUN_RES);
	}

}
