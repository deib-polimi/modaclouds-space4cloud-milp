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

/**
 * Contains the informations in a solution.xml file, used as a starting point for the computation.
 *
 */
public class SolutionList {
	public static class AmountVM {
		public int provider = -1;
		public int hour = -1;
		public int tier = -1;
		public int resource = -1;
		public int allocation = -1;
	}
	
	public static class X {
		public int provider = -1;
		public int taken = 0;
	}
	
	public static class W {
		public int resource = -1;
		public int provider = -1;
		public int tier = -1;
		public int taken = 0;
	}
	
	public AmountVM[] amounts = null;
	
	public X[] xs = null;
	
	public W[] ws = null;
	
	public SolutionList(int providers, int tiers) {
		amounts = new AmountVM[tiers * providers * 24];
		for (int i = 0; i < amounts.length; ++i)
			amounts[i] = new AmountVM();
		
		ws = new W[tiers * providers];
		for (int i = 0; i < ws.length; ++i)
			ws[i] = new W();
		
		xs = new X[providers];
		for (int i = 0; i < xs.length; ++i)
			xs[i] = new X();
		
		
	}
}
