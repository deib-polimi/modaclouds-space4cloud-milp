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
package it.polimi.modaclouds.space4cloud.milp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

	private static final Logger logger = LoggerFactory.getLogger(Main.class);

	@Parameter(names = { "-h", "--help" }, description = "Shows this help", help = true)
	private boolean help = false;

	@Parameter(names = "-configuration", description = "The path to the configuration file", required = true)
	private String configuration = null;

	@Parameter(names = "-solution", description = "The path to the solution file")
	private String solution = null;

	@Parameter(names = "-providers", description = "The providers that will be considered")
	private List<String> providers = new ArrayList<>();

	public static final String APP_TITLE = "\nMILP\n";

	public static void doMain(String configuration, String solution, String[] providers) {
		if (configuration == null || !new File(configuration).exists()) {
			logger.error("The configuration file doesn't exist! Exiting...");
			return;
		}

		Solver.removeTempFiles = false;

		try {
			Solver s = null;

			if (solution != null)
				s = new Solver(configuration, solution);
			else
				s = new Solver(configuration);

			if (providers.length > 0)
				s.setProviders(providers);

			File resourceEnvExtFile = s.getResourceModelExt();
			File initialSolution = s.getSolution();
			File initialMce = s.getMultiCloudExt();

			logger.debug("Generated resource model extension: "
					+ resourceEnvExtFile.getAbsolutePath());
			logger.debug("Generated solution: "
					+ initialSolution.getAbsolutePath());
			logger.debug("Generated multi cloud extension: "
					+ initialMce.getAbsolutePath());
		} catch (Exception e) {
			logger.error("Error while computing the solution!", e);
		}
	}

	public static void main(String[] args) {
//		args = "-configuration /Users/ft/Development/workspace-s4c-runtime/modaclouds-models/MiCforJSS-2tier/Configuration/aaa.properties -providers Amazon".split(" ");

		Main m = new Main();
		JCommander jc = new JCommander(m, args);

		System.out.println(APP_TITLE);

		if (m.help) {
			jc.setProgramName("milp");
			jc.usage();
			System.exit(0);
		}

		String[] providers = new String[m.providers.size()];
		for (int i = 0; i < providers.length; ++i)
			providers[i] = m.providers.get(i);

		doMain(m.configuration, m.solution, providers);
	}

}
