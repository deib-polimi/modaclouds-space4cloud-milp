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

//default values of AMPL data
//they are not used in computation process
// and are added to data.dat file to notation reasons
public class DefaultDataCollection {

	public double ProbabilityToBeInComponent=0.1;//probabilities that classes will have ways cross components
	public double ArrRate=100;//arrival rate
	public double MaximumSR=1;//maximum service rate
	public int PartitionComponents=1;//binary variable which shows that two components are in the same container
	public double Speed=1;//Speed of VM type
	public double Cost=0.06;//Cost of VM type
	public double MaxResponseTime=0.04;//Maximum Response time for component
	public double MinArrRate=0.2;//minimum arrival rate per provider
	public double Alpha=0.1;//proportion of arrival rate on class k

}
