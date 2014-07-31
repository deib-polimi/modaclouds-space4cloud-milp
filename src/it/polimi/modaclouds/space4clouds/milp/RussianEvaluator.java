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

import it.polimi.modaclouds.space4clouds.milp.Solver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class RussianEvaluator {

	protected Solver solver;

	protected File resourceEnvExt = null, solution = null,
			multiCloudExt = null;

	protected boolean computed = false;

	protected int cost = -1;

	protected long evaluationTime = -1L;

	private static String URL = "jdbc:mysql://resourcemodel.cloudapp.net:3306/";
	private static String DBNAME = "cloud";
	private static String DRIVER = "com.mysql.jdbc.Driver";
	private static String USERNAME = "moda";
	private static String PASSWORD = "modaclouds";
	
	private static String SSH_HOST = "";
	private static String SSH_PASSWORD = "";
	private static String SSH_USER_NAME = "";
	
	public RussianEvaluator(File usageModelExtFile, File constraintFile) {
		solver = new Solver(MainTest.PROJECT_PATH, MainTest.WORKING_DIRECTORY,
				MainTest.RESOURCE_MODEL, MainTest.USAGE_MODEL, MainTest.ALLOCATION_MODEL, MainTest.REPOSITORY_MODEL,
				Paths.get(Paths.get(MainTest.RESOURCE_MODEL).getParent().toString(), "default.system").toString(),
				constraintFile.getAbsolutePath(), usageModelExtFile.getAbsolutePath(),SSH_HOST,SSH_PASSWORD,SSH_USER_NAME);
		
		solver.setDeleteTempFiles(false);
		
		solver.getOptions().SqlDBUrl = URL;
		solver.getOptions().DBName = DBNAME;
		solver.getOptions().DBDriver = DRIVER;
		solver.getOptions().DBUserName = USERNAME;
		solver.getOptions().DBPassword = PASSWORD;
	}
	
	public static void setDatabaseInformation(InputStream confFileStream) throws IOException {		
		if(confFileStream!=null){
			Properties properties = new Properties();
			properties.load(confFileStream);		
			URL=properties.getProperty("URL");
			DBNAME=properties.getProperty("DBNAME");
			DRIVER=properties.getProperty("DRIVER");
			USERNAME=properties.getProperty("USERNAME");
			PASSWORD=properties.getProperty("PASSWORD");
		}
	}
	
	public static void setDatabaseInformation(String url, String dbName, String driver, String userName, String password) throws IOException {												
			URL=url;
			DBNAME=dbName;
			DRIVER=driver;
			USERNAME=userName;
			PASSWORD=password;
	}

	public void eval() throws Exception {
		resourceEnvExt = solver.getResourceModelExt();
		solution = solver.getSolution();
		multiCloudExt = solver.getMultiCloudExt();
		cost = -1;
		evaluationTime = -1L;
		computed = true;

		if (resourceEnvExt == null || solution == null || multiCloudExt == null
				|| !solution.exists()) {
			reset();
			throw new Exception(
					"Error! It's impossible to generate the solution! Are you connected?");
		}
	}

	public int getCost() {
		parseResults();
		return cost;
	}

	public long getEvaluationTime() {
		parseResults();
		return evaluationTime;
	}

	public File getMultiCloudExt() {
		return multiCloudExt;
	}

	public File getResourceEnvExt() {
		return resourceEnvExt;
	}

	public File getSolution() {
		return solution;
	}

	private void parseResults() {
		if (!computed || solution == null || cost > -1 || evaluationTime > -1L)
			return;

		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(solution);
			doc.getDocumentElement().normalize();

			NodeList nl = doc.getElementsByTagName("SolutionMultiResult");

			// if (nl.getLength() == 0)
			// nl = doc.getElementsByTagName("SolutionResult");

			Element root = (Element) nl.item(0);
			cost = (int) Math.round(Double.parseDouble(root
					.getAttribute("cost")));
			evaluationTime = Long.parseLong(root.getAttribute("time"));

		} catch (Exception e) {
			cost = -1;
			evaluationTime = -1L;
			return;
		}
	}

	public void reset() {
		resourceEnvExt = null;
		solution = null;
		multiCloudExt = null;
		computed = false;
		cost = -1;
		evaluationTime = -1L;
	}

	public void setMinimumNumberOfProviders(int num) {
		solver.setMinimumNumberOfProviders(num);
		reset();
	}

	public void setProviders(String... provider) {
		solver.setProviders(provider);
		reset();
	}

	public void setRegions(String... region) {
		solver.setRegions(region);
		reset();
	}

	public void setStartingSolution(File f) {
		solver.setStartingSolution(f);
		reset();
	}

	public static void setSSH_HOST(String sSH_HOST) {
		SSH_HOST = sSH_HOST;
	}

	public static void setSSH_PASSWORD(String sSH_PASSWORD) {
		SSH_PASSWORD = sSH_PASSWORD;
	}

	public static void setSSH_USER_NAME(String sSH_USER_NAME) {
		SSH_USER_NAME = sSH_USER_NAME;
	}

}
