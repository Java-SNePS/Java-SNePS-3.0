package sneps.snip.classes;

import sneps.exceptions.NodeNotFoundInNetworkException;
import sneps.exceptions.NotAPropositionNodeException;
import sneps.network.classes.setClasses.FlagNodeSet;
import sneps.network.classes.setClasses.PropositionSet;
import sneps.snip.matching.Substitutions;

/**
 * @className RuleUseInfo.java
 * 
 * @ClassDescription The RuleUseInfo is a data structure used to save instances of antecedents of rule nodes.
 * 
 * @author Amgad Ashraf
 *
 * @version 3.00 31/5/2018
 */
public class RuleUseInfo {
	private Substitutions sub;
	private int pos;
	private int neg;
	private FlagNodeSet fns;

	public RuleUseInfo(Substitutions s, int p, int n, FlagNodeSet f) {
		sub = s;
		pos = p;
		neg = n;
		fns = f;
	}
	
	/**
	 * Return the number of positive substitutions
	 * 
	 * @return int
	 */
	public int getPosCount() {
		return pos;
	}

	/**
	 * Return the number of negative substitutions
	 * 
	 * @return int
	 */
	public int getNegCount() {
		return neg;
	}

	/**
	 * Returns a flag node set with which antecedents are negative only
	 * 
	 * @return FlagNodeSet
	 */
	public FlagNodeSet getNegSubs() {
		FlagNodeSet res = new FlagNodeSet();
		for (FlagNode fn : fns) {
			if (fn.getFlag() == 2)
				res.insert(fn);
		}
		return res;
	}

	/**
	 * Returns a flag node set with which antecedents are positive only
	 * 
	 * @return FlagNodeSet
	 */
	public FlagNodeSet getPosSubs() {
		FlagNodeSet res = new FlagNodeSet();
		for (FlagNode fn : fns) {
			if (fn.getFlag() == 1)
				res.insert(fn);
		}
		return res;
	}
	
	/**
	 * Return the flag node set of the rule use info
	 * 
	 * @return FlagNodeSet
	 */
	public FlagNodeSet getFlagNodeSet() {
		return fns;
	}
	
	/**
	 * Return the substitutions list
	 * 
	 * @return Substitutions
	 */
	public Substitutions getSubstitutions() {
		return sub;
	}

	/**
	 * Check if this and r have no binding conflicts
	 * 
	 * @param r
	 *            rule use info
	 * @return true or false
	 */
	public boolean isVarsCompatible(RuleUseInfo r) {
		return sub.isCompatible(r.getSubstitutions());
	}

	/**
	 * Check if this flagged node set and r's flagged node set are joint
	 * 
	 * @param r
	 *            rule use info
	 * @return true or false
	 */
	public boolean isJoint(RuleUseInfo r) {
		// for (int i = 0; i < fns.cardinality(); i++) {
		// for (int j = 0; j < r.getFlagNodeSet().cardinality(); j++) {
		// if (fns.getFlagNode(i)
		// .getNode()
		// .getIdentifier()
		// .equals(r.getFlagNodeSet().getFlagNode(j).getNode()
		// .getIdentifier()))
		// return true;
		// }
		// }
		for (FlagNode fn1 : fns) {
			for (FlagNode fn2 : r.getFlagNodeSet()) {
				System.out.println("---->> " + fn1.getNode() + " " + fn1.getNode());
				if (fn1.getNode() == fn2.getNode())
					return true;
			}
		}
		return false;
	}

	/**
	 * Check if this flagged node set and r's flagged node set are disjoint
	 * 
	 * @param r
	 *            rule use info
	 * @return true or false
	 */
	public boolean isDisjoint(RuleUseInfo r) {
		return !isJoint(r);
	}
	
	/**
	 * combine rui with this rule use info
	 * 
	 * @param rui
	 *            RuleUseInfo
	 * @return RuleUseInfo
	 */
	public RuleUseInfo combine(RuleUseInfo rui) {
		if (this.isDisjoint(rui) && this.isVarsCompatible(rui)) {
			return new RuleUseInfo(this.getSubstitutions().union(rui.getSubstitutions()), 
					this.pos + rui.getPosCount(), this.neg + rui.getNegCount(), 
					this.fns.union(rui.getFlagNodeSet()));
		}
		
		return null;
	}
	
	/**
	 * Gets the union of supports of each flag node in this RUI's fns
	 * @return PropositionSet
	 */

	public PropositionSet getSupports() {
		if (fns.isNew())
			return new PropositionSet();
		
		if (fns.cardinality() == 1)
			return fns.iterator().next().getSupports();

		PropositionSet res = new PropositionSet();
		for(FlagNode fn : fns){
			try {
				res = res.union(fn.getSupports());
			} catch (NotAPropositionNodeException
					| NodeNotFoundInNetworkException e) {
			}
		}
		
		return res;
	}

}