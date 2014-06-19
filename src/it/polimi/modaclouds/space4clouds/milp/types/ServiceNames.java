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
package it.polimi.modaclouds.space4clouds.milp.types;

//lists of services with Ids and SEFF names (from Repository Diagram)
public class ServiceNames {
	public String[] Id = null;// Id
	public String[] SEFFName = null;// SEFF names
	public int count = 0; // amount of services

	// constructor
	public ServiceNames(int ncount) {
		Id = new String[ncount];
		SEFFName = new String[ncount];
		count = ncount;
	}
}
