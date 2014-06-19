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
package it.polimi.modaclouds.space4clouds.milp.xmlfiles;

import it.polimi.modaclouds.space4clouds.milp.types.ClassOptions;
import it.polimi.modaclouds.space4clouds.milp.xmldatalists.RepositoryList;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

//is used to create model extension file
public class CreateConstraintXML {

	// XML document
	private Document maindoc;

	// is true, if document was loaded
	private boolean loadrez = false;

	// root element of loaded document
	private Element root;

	// constructor
	public CreateConstraintXML(ClassOptions NOptions, RepositoryList CRList) {
		loadModel();
		// System.out.println(NOptions.FilePathConstraintTemplate);
		createconstraints(NOptions, CRList);
	}

	// loads XML document in DOM parser
	public boolean loadModel() {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			maindoc = docBuilder.newDocument();
			root = maindoc.createElement("constraints");
			maindoc.appendChild(root);
			
			loadrez = true;
		} catch (Exception e) {
//			e.getMessage();
			e.printStackTrace();
			loadrez = false;
		}
		return loadrez;
	}

	public int createconstraints(ClassOptions NOptions, RepositoryList CRList) {
		if (!loadrez)
			return 1;
		// System.out.println("0");
		try {
			for (int i = 0; i < CRList.ncountClasses; i++) {

				Element Constraint = maindoc.createElement("constraint");
				root.appendChild(Constraint);

				Attr pattr = maindoc.createAttribute("id");
				pattr.setValue("q" + i);
				Constraint.setAttributeNode(pattr);

				pattr = maindoc.createAttribute("name");
				pattr.setValue("class" + i);
				Constraint.setAttributeNode(pattr);

				Element TRIDR = maindoc.createElement("targetResourceIDRef");
				TRIDR.setTextContent(CRList.ServiceIds[i]);
				Constraint.appendChild(TRIDR);

				Element metric = maindoc.createElement("metric");
				metric.setTextContent("ResponseTime");
				Constraint.appendChild(metric);

				Element aunit = maindoc.createElement("unit");
				aunit.setTextContent("ms");
				Constraint.appendChild(aunit);

				Element range = maindoc.createElement("range");
				Constraint.appendChild(range);

				Element hasMaxValue = maindoc.createElement("hasMaxValue");
				int g = (int) Math.round(NOptions.MSR * 1000);
				String a = Integer.toString(g);
				hasMaxValue.setTextContent(a);
				range.appendChild(hasMaxValue);
			}

			if (NOptions.Utilization < 1)
				for (int i = 0; i < CRList.ContainerList.ncount; i++) {

					Element Constraint = maindoc.createElement("constraint");
					root.appendChild(Constraint);

					Attr pattr = maindoc.createAttribute("id");
					pattr.setValue("a" + i);
					Constraint.setAttributeNode(pattr);

					pattr = maindoc.createAttribute("name");
					pattr.setValue("Utilization" + i);
					Constraint.setAttributeNode(pattr);

					Element TRIDR = maindoc
							.createElement("targetResourceIDRef");
					TRIDR.setTextContent(CRList.ContainerList.Id[i]);
					Constraint.appendChild(TRIDR);

					Element metric = maindoc.createElement("metric");
					metric.setTextContent("CPUUtilization");
					Constraint.appendChild(metric);

					Element aunit = maindoc.createElement("unit");
					aunit.setTextContent("%");
					Constraint.appendChild(aunit);

					Element range = maindoc.createElement("range");
					Constraint.appendChild(range);

					Element hasMaxValue = maindoc.createElement("hasMaxValue");
					int g = (int) Math.round(NOptions.Utilization * 100);
					String a = Integer.toString(g);
					hasMaxValue.setTextContent(a);
					range.appendChild(hasMaxValue);
				}
			for (int i = 0; i < CRList.ContainerList.ncount; i++)
				if (NOptions.MemoryDemand[i] > 0) {

					Element Constraint = maindoc.createElement("constraint");
					root.appendChild(Constraint);

					Attr pattr = maindoc.createAttribute("id");
					pattr.setValue("m" + i);
					Constraint.setAttributeNode(pattr);

					pattr = maindoc.createAttribute("name");
					pattr.setValue("Memory" + i);
					Constraint.setAttributeNode(pattr);

					Element TRIDR = maindoc
							.createElement("targetResourceIDRef");
					TRIDR.setTextContent(CRList.ContainerList.Id[i]);
					Constraint.appendChild(TRIDR);

					Element metric = maindoc.createElement("metric");
					metric.setTextContent("RAM");
					Constraint.appendChild(metric);

					Element aunit = maindoc.createElement("unit");
					aunit.setTextContent("MB");
					Constraint.appendChild(aunit);

					Element range = maindoc.createElement("range");
					Constraint.appendChild(range);

					Element hasMaxValue = maindoc.createElement("hasMinValue");
					String a = Integer.toString(NOptions.MemoryDemand[i]);
					hasMaxValue.setTextContent(a);
					range.appendChild(hasMaxValue);
				}
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(maindoc);
			StreamResult result = new StreamResult(new File(
					NOptions.FilePathConstraint));
			transformer.transform(source, result);

		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
		return 0;
	}
}
