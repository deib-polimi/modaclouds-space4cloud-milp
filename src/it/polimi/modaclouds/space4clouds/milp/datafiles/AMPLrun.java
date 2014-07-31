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
package it.polimi.modaclouds.space4clouds.milp.datafiles;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

//this class creates local copy of AMPL.run file
//it is created and uploaded each time to have possibility to set CPLEX solver time limit
public class AMPLrun {

	// main function
	// file saves in AMPLrunFilePath
	// TimeLimit is used to set CPLEX solver time limit
	// UploadPath - directory om AMPL server (as it is required to use command
	// "cd UploadPath" in AMPL
	public void AMPLrunToFile(String AMPLrunFilePath, String TimeLimit,
			String UploadPath, String FilePathStartingSolution) {
		try {Writer wrt = new FileWriter(AMPLrunFilePath);
			PrintWriter out = new PrintWriter(wrt);
			out.println("cd "
					+ UploadPath.substring(0, UploadPath.length() - 1) + ";");
			out.println("reset;");
			out.println("option log_file 'log.tmp';");
			if (FilePathStartingSolution != null) { 
				out.println("model modelstartingsolution.mod;");
//				out.println("option presolve 0;");
			} else
				out.println("model model.mod;"); 
			out.println("data data.dat;");
			
			//TODO: what happens if the solver is not there?
			out.println("option solver '/usr/optimization/CPLEX_Studio_Preview126/cplex/bin/x86-64_linux/cplexamp';");
			out.println("option show_stats 1;");
			if (!TimeLimit.equalsIgnoreCase(""))
				out.println("option timelimit " + TimeLimit + ";");
			out.println("option cplex_options 'timing=1';");
			out.println("solve;");
			out.println("display X,PartialArrRate,AmountVM > rez.out;");
			out.println("display {v in TYPE_VM, p in PROVIDER, i in CONTAINER:W[v,p,i]>0} (W[v,p,i]), W >> rez.out;");
			//out.println("display DiffResponseTime >> rez.out;");
			out.println("display sum{t in TIME_INT, p in PROVIDER, i in CONTAINER, v in TYPE_VM} (Cost[v, p, i]*AmountVM[v, p, i, t]) > shortrez.out;");
			out.println("display {p in PROVIDER, t in TIME_INT, i in CONTAINER, v in TYPE_VM:AmountVM[v,p,i,t]>0} (AmountVM[v,p,i,t],PartialArrRate[p,t]) >> shortrez.out;");
			out.println("option log_file '';");
			out.println("close shortrez.out;");
			out.println("close rez.out;");
			out.println("close log.tmp;");
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
