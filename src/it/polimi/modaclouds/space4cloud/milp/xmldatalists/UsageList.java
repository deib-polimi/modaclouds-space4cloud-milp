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
package it.polimi.modaclouds.space4cloud.milp.xmldatalists;

//container for the information from UsageModel Diagram
//classes are formed as Internal Actions
public class UsageList {
	
	//probability distribution between classes (calculates according to amount of calls of classes)
	public double[] probdistr=null;
	//array of Ids of first services in Repository Diagram from each class
	public String[] ClassRepositoryId=null;
	//amount of classes
	public int ncount;
	
	//constructors
	public UsageList(){}
	public UsageList(int count)
	{
		ncount=count;
		probdistr=new double [count];
		ClassRepositoryId=new String [count];
	}
}
