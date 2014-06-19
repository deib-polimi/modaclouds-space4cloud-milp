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

import it.polimi.modaclouds.space4clouds.milp.types.ClassOptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;

import javax.swing.JOptionPane;

/*
 * loads configuration XML file
 * sets options which contains in this file
 */
public class Config {
	
	// receives main configuration from opened XML document
	public static ClassOptions SetConfiguration(String projectPath, String workingDirectory,
			String resourceModel, String usageModel, String allocationModel, String repositoryModel, String systemModel,
			String constraintFile, String usageModelExtFile) {
		
		if (!projectPath.substring(projectPath.length() - 1).equals(File.separator))
			projectPath += File.separator;
		if (!workingDirectory.substring(workingDirectory.length() - 1).equals(File.separator))
			workingDirectory += File.separator;
		
		ClassOptions NewOptions = new ClassOptions();
		
		NewOptions.ModelPath = projectPath;
		
		NewOptions.TestPrefix = workingDirectory;
				
		NewOptions.FilePathUsage = usageModel;
		NewOptions.FilePathRepository = repositoryModel;
		NewOptions.FilePathAllocation = allocationModel;
		NewOptions.FilePathResenv = resourceModel;
		NewOptions.FilePathSystem = systemModel;
		
		NewOptions.FilePathUsageModelExt = usageModelExtFile;
		
		NewOptions.MAR=200;//max value of arrival rate (without noise)
		NewOptions.MMAR=0.1;//max value of minimum proportion of arrival rate per provider (without noise)
		NewOptions.MAP=1;//minimum number of providers
		NewOptions.MSR=0.4;//system response time (in seconds)
		NewOptions.Utilization=0.6;//Utilization
		
		//define memory constraints (5 - number of containers (should be more or equal to real number of containers))
		// 0 - the most usual memory constraint (with 0 it is not including in Constraint file)
		NewOptions.initmemorydemand(5,0);
		//Memory constraint for single container
		//NewOptions.MemoryDemand[1]=1500;
		
		NewOptions.SaveDirectory=NewOptions.ModelPath+NewOptions.TestPrefix;
		NewOptions.FilePathConstraint = constraintFile;
		
		NewOptions.GeneratedResourceModelExt = Paths.get(NewOptions.SaveDirectory, "generated-rme.xml").toString();
		NewOptions.GeneratedSolution = Paths.get(NewOptions.SaveDirectory, "generated-solution.xml").toString();
		NewOptions.GeneratedMultiCloudExt = Paths.get(NewOptions.SaveDirectory, "generated-mce.xml").toString();
		NewOptions.FilePathAdditionalInformation = Paths.get(NewOptions.SaveDirectory, "generated-addinf.xml").toString();
		
		NewOptions.SolverTimeLimit="720";
		
		NewOptions.SqlDBUrl="jdbc:mysql://109.231.122.191:3306/";
		NewOptions.DBName = "cloud";
		NewOptions.DBUserName="moda";
		NewOptions.DBPassword="modaclouds";
		
		NewOptions.SSHhost="ch14r4.dei.polimi.it";
		NewOptions.SSHUserName="";
		NewOptions.SSHPassword="";
		
		initSshData(NewOptions);
		
		NewOptions.Set = true;
		
		return NewOptions;
	}
	
	private static void initSshData(ClassOptions NewOptions) {
		Properties conf = new Properties();
		File f = Paths.get("sshdata.conf").toFile();
		boolean askData = false;
		
		System.out.println("SSH configuration file: " + f.getAbsolutePath());
		
		if (f.exists()) {
			try {
				conf.load(new FileInputStream(f.toString()));
				
				NewOptions.SSHUserName = conf.getProperty("SSHUserName");
				NewOptions.SSHPassword = conf.getProperty("SSHPassword");
				
			} catch (IOException e) {
				e.printStackTrace();
				askData = true;
			}
		} else {
			askData = true;
		}
		
		if (askData) {
			String username = null;
			
			do {
				username = (String) JOptionPane.showInputDialog(null,
					"Insert the username for the SSH server:",
					"SSH Data configuration (1/2)",
					JOptionPane.PLAIN_MESSAGE, null, null, "");
			
			} while (username == null || username.length() == 0);
			
			String password = null;
			
			do {
				password = (String) JOptionPane.showInputDialog(null,
					"Insert the password for the SSH server:",
					"SSH Data configuration (2/2)",
					JOptionPane.PLAIN_MESSAGE, null, null, "");
			
			} while (password == null || password.length() == 0);
			
			conf.setProperty("SSHUserName", username);
			conf.setProperty("SSHPassword", password);
			
			try {
				f.createNewFile();
				conf.store(new FileOutputStream(f.toString()), null);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			NewOptions.SSHUserName = username;
			NewOptions.SSHPassword = password;
		}
	}
	
}
