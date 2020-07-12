package sneps.snip.classes;

import sneps.network.classes.setClasses.FlagNodeSet;
import sneps.snip.InferenceTypes;
import sneps.snip.matching.Substitutions;

/**
 * @ClassDescription The RuleUseInfo is a data structure used to save instances of
 * antecedents of rule nodes.
 *
 */
public class RuleUseInfo {
	private Substitutions sub;
	private int pos;
	private int neg;
	private FlagNodeSet fns;

	/**
	 * Type of Report this RUI is produced from.
	 */
	private InferenceTypes type;

	public RuleUseInfo() {

	}

	public RuleUseInfo(Substitutions s, int p, int n, FlagNodeSet f, InferenceTypes t) {
		sub = s;
		pos = p;
		neg = n;
		fns = f;
		type = t;
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

	public InferenceTypes getType() {
		return type;
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
		for (FlagNode fn1 : fns) {
			for (FlagNode fn2 : r.getFlagNodeSet()) {
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
			InferenceTypes resultingType;
			if(this.getType().equals(InferenceTypes.FORWARD) ||
					rui.getType().equals(InferenceTypes.FORWARD))
				resultingType = InferenceTypes.FORWARD;
			else
				resultingType = InferenceTypes.BACKWARD;

			return new RuleUseInfo(this.getSubstitutions().union(rui.getSubstitutions()),
					this.pos + rui.getPosCount(), this.neg + rui.getNegCount(),
					this.fns.union(rui.getFlagNodeSet()), resultingType);
		}

		return null;
	}

	public String toString() {
		return "Sub: " + sub.toString() + " Pos: " + pos + " Neg: " + neg +
				" Fns: " + fns.toString() + "Type: " + type;
	}

}
