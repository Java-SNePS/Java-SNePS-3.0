package sneps.snip;

import sneps.setClasses.PropositionSet;
import sneps.snip.matching.Substitutions;

public class Report {
	private Substitutions substitution;
	private PropositionSet supports;
	private boolean sign;
	private String contextName;

	public Report(Substitutions substitution, PropositionSet set, boolean sign, String contextID) {
		this.substitution = substitution;
		this.supports = set;
		this.sign = sign;
		this.contextName = contextID;
	}

	public Substitutions getSubstitutions() {
		return substitution;
	}

	public PropositionSet getSupports() {
		return supports;
	}

	@Override
	public boolean equals(Object report) {
		Report castedReport = (Report) report;
		return this.substitution.equals(castedReport.substitution) && this.sign == castedReport.sign
				&& this.contextName == castedReport.contextName;
	}

	public boolean getSign() {
		return sign;
	}

	public boolean isPositive() {
		return sign;
	}

	public boolean isNegative() {
		return !sign;
	}

	public String toString() {
		return "ContextID : " + contextName + "\nSign: " + sign + "\nSubstitution: " + substitution + "\nSupport: " + supports.toString();
	}

	public String getContextName() {
		return contextName;
	}
}
