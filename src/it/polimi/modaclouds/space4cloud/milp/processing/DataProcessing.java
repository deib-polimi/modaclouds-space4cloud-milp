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
package it.polimi.modaclouds.space4cloud.milp.processing;

import it.polimi.modaclouds.space4cloud.milp.datafiles.Bash;
import it.polimi.modaclouds.space4cloud.milp.datafiles.FileRun;
import it.polimi.modaclouds.space4cloud.milp.datafiles.Model;
import it.polimi.modaclouds.space4cloud.milp.ssh.SshConnector;
import it.polimi.modaclouds.space4cloud.milp.xmlfiles.ResultXML;

//main execution class (wrapper)
//consists of 4 different parts: parser, converter of parser results into data.dat
//SSH connector and parser for AMPL results
public class DataProcessing {

	// parser for PCM model and SQL database
	public Parser newparser = null;

	// converter for PCM and SQL parser results into data.dat
	public PrintData newprintdata = null;

	// parser for AMPL results
	public ResultXML newresultxml = null;

	// constructor
	public DataProcessing() {
		run();
	}

	// main execution function
	public void run() {
		// constructor for PCM and SQL parser
		// all parsing functions are called by it
		newparser = new Parser();
		
		PrintData.print(newparser);

		FileRun.print();

		Model.print();
		
		Bash.print();
		
		// constructor for SSH connector
		// creates connection to AMPL server
		// uploads data.dat and AMPL.run
		// runs bash-script on AMPL server
		// downloads log and results of AMPL
		SshConnector.run();

		// parses AMPL log and results and saves into readable format
		ResultXML.print(newparser);
	}
}
