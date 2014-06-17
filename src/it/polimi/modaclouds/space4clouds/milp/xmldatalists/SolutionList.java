package it.polimi.modaclouds.space4clouds.milp.xmldatalists;

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
