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
import it.polimi.modaclouds.space4cloud.milp.Solver;
import it.polimi.modaclouds.space4cloud.milp.ssh.ExecSSH;
import it.polimi.modaclouds.space4cloud.milp.ssh.ScpTo;

//this class is used to create connection to AMPL server (wrapper)
public class SshConnector {

	// main execution function
	public static void run() {
		// this object runs bash-script on AMPL server
		ExecSSH newExecSSH = new ExecSSH();
		
		newExecSSH.mainExec(String.format("mkdir %s", Configuration.RUN_WORKING_DIRECTORY));
		
		// this object uploads files on AMPL server
		ScpTo newScpTo = new ScpTo();
		newScpTo.sendfile(Configuration.RUN_DATA, Configuration.RUN_WORKING_DIRECTORY + "/" + Configuration.RUN_DATA);
		newScpTo.sendfile(Configuration.RUN_FILE, Configuration.RUN_WORKING_DIRECTORY + "/" + Configuration.RUN_FILE);
		
		newScpTo.sendfile(Configuration.DEFAULTS_BASH, Configuration.RUN_WORKING_DIRECTORY + "/" + Configuration.DEFAULTS_BASH);
		newScpTo.sendfile(Configuration.RUN_MODEL_STANDARD, Configuration.RUN_WORKING_DIRECTORY + "/" + Configuration.RUN_MODEL_STANDARD);
		newScpTo.sendfile(Configuration.RUN_MODEL_STARTING_SOLUTION, Configuration.RUN_WORKING_DIRECTORY + "/" + Configuration.RUN_MODEL_STARTING_SOLUTION);
		
		newExecSSH.mainExec(
				String.format("cd %1$s && tr -d '\r' < %2$s > %2$s-bak && mv %2$s-bak %2$s",
						Configuration.RUN_WORKING_DIRECTORY,
						Configuration.RUN_DATA));
		newExecSSH.mainExec(
				String.format("cd %1$s && tr -d '\r' < %2$s > %2$s-bak && mv %2$s-bak %2$s",
						Configuration.RUN_WORKING_DIRECTORY,
						Configuration.RUN_FILE));
		newExecSSH.mainExec(
				String.format("cd %1$s && tr -d '\r' < %2$s > %2$s-bak && mv %2$s-bak %2$s",
						Configuration.RUN_WORKING_DIRECTORY,
						Configuration.DEFAULTS_BASH));
		newExecSSH.mainExec(
				String.format("cd %1$s && tr -d '\r' < %2$s > %2$s-bak && mv %2$s-bak %2$s",
						Configuration.RUN_WORKING_DIRECTORY,
						Configuration.RUN_MODEL_STANDARD));
		newExecSSH.mainExec(
				String.format("cd %1$s && tr -d '\r' < %2$s > %2$s-bak && mv %2$s-bak %2$s",
						Configuration.RUN_WORKING_DIRECTORY,
						Configuration.RUN_MODEL_STARTING_SOLUTION));
		
		newExecSSH.mainExec();

		// this block downloads logs and results of AMPL
		ScpFrom newScpFrom = new ScpFrom();
		newScpFrom.receivefile(Configuration.RUN_LOG, Configuration.RUN_WORKING_DIRECTORY + "/" + Configuration.RUN_LOG);
		newScpFrom.receivefile(Configuration.RUN_RES, Configuration.RUN_WORKING_DIRECTORY + "/" + Configuration.RUN_RES);
		
		if (Solver.removeTempFiles)
			newExecSSH.mainExec(String.format("rm -rf %s", Configuration.RUN_WORKING_DIRECTORY));
	}

}
