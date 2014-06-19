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

//contains information from System Diagram
public class SystemList {
	
	//id of element in System Diagram
	public String[] Id=null;
	//id of corresponding element from Repository Diagram
	public String[] RepositoryId=null;
	//name of element in System Diagram
	public String[] Name=null;
	//amount of elements in System Diagram
	public int ncount=0;

	//constructors
	public SystemList(){}
	public SystemList(int count)
	{
		ncount=count;
		Id=new String [count];
		RepositoryId=new String [count];
		Name=new String [count];
	}
	
	//returns number of element from System Diagram according to the Id of corresponding element from Repository Diagram
	public int getNumByRepositoryId(String newRepositoryId)
	{
		int res=0;
		while (!((res==ncount) || (RepositoryId[res].equalsIgnoreCase(newRepositoryId))))
			res++;
		if (res==ncount) 
			return -1;
		return res;
	}
}
