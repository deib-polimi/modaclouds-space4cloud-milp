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
package it.polimi.modaclouds.space4clouds.milp.xmldatalists;

//container for allocation pairs from Allocation Diagram
public class AllocationList {

	//Id of the pair in Allocation Diagram
	public String[] Id=null;
	//Id of corresponding Element in Resorce Environment Diagram
	public String[] ResourceEnvironment=null;
	//Id of corresponding element in System Diagram
	public String[] System=null;
	//amount of pairs
	public int ncount=0;
	
	//constructors
	public AllocationList(){}
	public AllocationList(int count)
	{
		ncount=count;
		Id=new String [count];
		ResourceEnvironment=new String [count];
		System=new String [count];
	}
	
	//returns number of Allocation pair by its Id
	public int getNumBySystemId(String newSystemId)
	{
		int res=0;
		while (!((res==ncount) || (System[res].equalsIgnoreCase(newSystemId))))
			res++;
		if (res==ncount) 
			return -1;
		return res;
	}
}
