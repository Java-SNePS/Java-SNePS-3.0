package sneps.snip.classes;

import sneps.setClasses.FlagNodeSet;
import sneps.setClasses.NodeSet;
import sneps.snip.matching.Substitutions;

public class RuleUseInfo {
	private Substitutions sub;
	private int pos;
	private int neg;
	private FlagNodeSet fns;

	public RuleUseInfo(Substitutions substitutions, int i, int j,
			FlagNodeSet fns) {
		// TODO Auto-generated constructor stub
	}

	public RuleUseInfo combine(RuleUseInfo tRui) {
		// TODO Auto-generated method stub
		return null;
	}

	
	
	public Substitutions getSubstitutions() {
		return sub;
	}
	public int getPos() {
		return pos;
	}
	public int getNeg() {
		return neg;
	}

	public FlagNodeSet getFlagNodeSet() {
		return fns;
	}

}
