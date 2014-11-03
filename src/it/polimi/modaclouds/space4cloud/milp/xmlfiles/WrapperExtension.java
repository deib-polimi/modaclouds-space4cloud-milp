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

//as it is required to create several extension files (for each provider)
//this class is used as wrapper for all of them
public class WrapperExtension {

	// array of classes, each of which is creator for extension file of its own
	// provider
	public ExtensionXML[] ExtensionsArray = null;
	// amount of providers
	public int countproviders = 0;

	// constructor
	public WrapperExtension(int countprov, String SaveDirect,
			 int counttimesn, int countcontainersn) {
		ExtensionsArray = new ExtensionXML[countprov];
		countproviders = countprov;
		for (int i = 0; i < countproviders; i++) {
			ExtensionsArray[i] = new ExtensionXML(i,
					counttimesn, countcontainersn);
			ExtensionsArray[i].SaveDirectory = SaveDirect;
		}
	}

	// this function prints all extensions
	public void printExtensions() {
		for (int i = 0; i < countproviders; i++)
			ExtensionsArray[i].createExtensions();
	}
}
