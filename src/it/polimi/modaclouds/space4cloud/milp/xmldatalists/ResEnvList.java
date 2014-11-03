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

//contains information from Resource Environment Diagram
public class ResEnvList {

	public String[] Id=null;// id of container
	public String[] Name=null;// name of container
	
	//amount of containers
	public int ncount=0;
	
	//constructors
	public ResEnvList(){}
	public ResEnvList(int count)
	{
		ncount=count;
		Name=new String [count];
		Id=new String [count];
	}
}
