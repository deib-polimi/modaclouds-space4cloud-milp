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
package it.polimi.modaclouds.space4clouds.milp.ssh;

import it.polimi.modaclouds.space4clouds.milp.types.ClassOptions;

//this class is used to create connection to AMPL server (wrapper)
public class SshConnector {

	// uses to download files from the AMPL server
	public ScpFrom newScpFrom = null;
	// uses to execute bash commands on AMPL server
	public ExecSSH newExecSSH = null;
	// uses to upload files on AMPL server
	public ScpTo newScpTo = null;

	// constructor
	public SshConnector(ClassOptions CurrOptions) {
		run(CurrOptions);
	}

	// main execution function
	public void run(ClassOptions CurrOptions) {
		// this block uploads files data.dat and AMPL.run on AMPL server
		newScpTo = new ScpTo(CurrOptions);
		newScpTo.sendfile(CurrOptions.FilePathSaveData, CurrOptions.UploadPath
				+ "data.dat");
		newScpTo.sendfile(CurrOptions.FilePathRunAMPL, CurrOptions.UploadPath
				+ "AMPL.run");

		// this block runs bash-script on AMPL server
		newExecSSH = new ExecSSH(CurrOptions);
		newExecSSH.mainExec();

		// this block downloads logs and results of AMPL
		newScpFrom = new ScpFrom(CurrOptions);
		newScpFrom.receivefile(CurrOptions.FilePathLogAMPL,
				CurrOptions.UploadPath + "log.tmp");
		newScpFrom.receivefile(CurrOptions.FilePathResAMPL,
				CurrOptions.UploadPath + "shortrez.out");
	}

}
