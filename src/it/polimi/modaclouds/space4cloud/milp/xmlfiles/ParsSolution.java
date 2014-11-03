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
package it.polimi.modaclouds.space4cloud.milp.xmlfiles;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import it.polimi.modaclouds.space4cloud.milp.types.SqlBaseParsMatrix;
import it.polimi.modaclouds.space4cloud.milp.xmldatalists.RepositoryList;
import it.polimi.modaclouds.space4cloud.milp.xmldatalists.SolutionList;

/**
 * Parses the starting solution file.
 *
 */
public class ParsSolution {
	
	public SolutionList solution = null;
	
	public ParsSolution(String file, SqlBaseParsMatrix resMatrix, RepositoryList CRList) {
		
		if (file != null && file.length() > 0 && new File(file).exists())
			init(file, resMatrix, CRList);
		
	}

	private void init(String file, SqlBaseParsMatrix resMatrix, RepositoryList CRList) {
		
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(new File(file));
			doc.getDocumentElement().normalize();
			
			NodeList tiers = doc.getElementsByTagName("Tier");
			
			solution = new SolutionList(resMatrix.Provider.length, CRList.ContainerList.Id.length);
			
			int value = 0, valuew = 0;
			
			for (int i = 0; i < tiers.getLength(); ++i) {
				Node n = tiers.item(i);
				
				if (n.getNodeType() != Node.ELEMENT_NODE)
					continue;
				
				Element tier = (Element) n;
				String provider = tier.getAttribute("providerName");
				String tierId = tier.getAttribute("id");
				String resourceName = tier.getAttribute("resourceName");
				
				int iProvider = 1;
				int iTier = 1;
				int iResource = 1;
				
				for (int w = 0; w < resMatrix.Provider.length; ++w) {
					if (resMatrix.Provider[w][0].equals(provider)) {
						iProvider = w + 1;
						w = resMatrix.Provider.length;
					}
				}
				
				for (int w = 0; w < CRList.ContainerList.Id.length; ++w) {
					if (CRList.ContainerList.Id[w].equals(tierId)) {
						iTier = w + 1;
						w = CRList.ContainerList.Id.length;
					}
				}
				
				for (int w = 0; w < resMatrix.TypeName[iProvider - 1].length; ++w) {
					if (resMatrix.TypeName[iProvider - 1][w].equals(resourceName)) {
						iResource = w + 1;
						w = resMatrix.TypeName[iProvider - 1].length;
					}
				}
				
				NodeList hourAllocations = tier.getElementsByTagName("HourAllocation");
				
				for (int j = 0; j < hourAllocations.getLength(); ++j) {
					Node m = hourAllocations.item(j);
					
					if (m.getNodeType() != Node.ELEMENT_NODE)
						continue;
					
					Element hourAllocation = (Element) m;
					int hour = Integer.parseInt(hourAllocation.getAttribute("hour"));
					int allocation = Integer.parseInt(hourAllocation.getAttribute("allocation"));
					
					solution.amounts[value].hour = hour + 1;
					solution.amounts[value].allocation = allocation;
					solution.amounts[value].provider = iProvider;
					solution.amounts[value].tier = iTier;
					solution.amounts[value++].resource = iResource;
					
				}
				
				solution.xs[iProvider - 1].provider = iProvider;
				solution.xs[iProvider - 1].taken = 1;
				
				solution.ws[valuew].provider = iProvider;
				solution.ws[valuew].tier = iTier;
				solution.ws[valuew].resource = iResource;
				solution.ws[valuew++].taken = 1;
			
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			
			solution = null;
		}
		
	}
	
}
