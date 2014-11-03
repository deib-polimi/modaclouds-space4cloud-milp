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
package it.polimi.modaclouds.space4cloud.milp.types;

//container for VM types information from SQL database
//(without sorting by provider)
//all information as in the database
public class SqlBaseParsElement {

	public int[] ProviderId = null;
	public String[] Provider = null;
	public String[] ServiceName = null;// for example, EC2
	public double[] processingRate = null;
	public double[] numberOfReplicas = null;
	public double[] cost = null;
	public double[] MemorySize = null;
	public String[] TypeName = null;
	public int ncount;// length of list
	
	public String[] Region = null;

	// constructor
	public SqlBaseParsElement(int count) {
		processingRate = new double[count];
		numberOfReplicas = new double[count];
		cost = new double[count];
		MemorySize = new double[count];
		Provider = new String[count];
		ServiceName = new String[count];
		TypeName = new String[count];
		ProviderId = new int[count];
		ncount = count;
		
		Region = new String[count];
	}
}
