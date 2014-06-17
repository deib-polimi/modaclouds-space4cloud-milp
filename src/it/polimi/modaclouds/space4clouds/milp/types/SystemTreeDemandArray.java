package it.polimi.modaclouds.space4clouds.milp.types;

//this class is used to calculate Maximum Response times of actions in Repository diagram
//process of calculation is simple
//there is hierarchical tree of actions (sub-actions are next level for the parent action)
// if  have actions on the same level,  sum demands and amounts of this actions
// if  have Resource Demand action,  set its own Demand and amount in corresponding array elements and 0 for all other actions
// if  action has probability-defined subactions, select way with minimum proportion of Maximum Response time
// if  action is loop action then it increase in "loop specification"-times Demands and amounts of subactions
public class SystemTreeDemandArray {
	
	public double[] Demand=null; //Summarized demand of action on the execution way with lowest proportion of Maximum Response time (between other actions on the same way (for more details see chapter 3 of the thesis))
	public int[] RDcount=null; //Amount calls for this action on the execution way with lowest proportion of Maximum Response time
	public int count=0; //length of the list (amount of actions)
	
	// constructor
	public SystemTreeDemandArray(int ncount) {
		count = ncount;
		Demand = new double[count];
		RDcount = new int[count];
		for (int i = 0; i < count; i++) {
			Demand[i] = 0;
			RDcount[i] = 0;
		}
	}

	// if have actions on the same level, sum demands and amounts of this
	// actions
	public void SumArrays(SystemTreeDemandArray NewArray) {
		for (int i = 0; i < count; i++) {
			Demand[i] += NewArray.Demand[i];
			RDcount[i] += NewArray.RDcount[i];
		}
	}

	// if action is loop action then it increase in "loop specification"-times
	// Demands and amounts of subactions
	public void SetLoop(int Loop) {
		for (int i = 0; i < count; i++) {
			Demand[i] *= Loop;
			RDcount[i] *= Loop;
		}
	}

	// if have Resource Demand action, set its own Demand and amount in
	// corresponding array elements and 0 for all other actions
	public void SetRDAction(int RDAIndex, double DemandRDA) {
		for (int i = 0; i < count; i++) {
			Demand[i] = DemandRDA;
		}
		RDcount[RDAIndex] = 1;
	}

	// if action has probability-defined subactions, select way with minimum
	// proportion of Maximum Response time
	public void SelectMin(SystemTreeDemandArray NewArray) {
		for (int j = 0; j < count; j++) {
			if (NewArray.RDcount[j] > 0) {
				if (RDcount[j] > 0) {
					double tempa = RDcount[j] / Demand[j] - NewArray.RDcount[j]
							/ NewArray.Demand[j];
					if (tempa > 0) {
						RDcount[j] = NewArray.RDcount[j];
						Demand[j] = NewArray.Demand[j];
					}
				} else {
					RDcount[j] = NewArray.RDcount[j];
					Demand[j] = NewArray.Demand[j];
				}
			} else {
				if (RDcount[j] == 0) {
					if (Demand[j] < NewArray.Demand[j]) {
						RDcount[j] = NewArray.RDcount[j];
						Demand[j] = NewArray.Demand[j];
					}
				}
			}
		}
	}

	public void printitall() {
		for (int j = 0; j < count; j++) {
			System.out.print(RDcount[j] + "  ");
		}
		System.out.println();
		for (int j = 0; j < count; j++) {
			System.out.print(Demand[j] + "  ");
		}
		System.out.println();
		System.out.println();
	}
}