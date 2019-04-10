package sneps.snip;

import java.util.Set;

import sneps.snebr.Support;
import sneps.snip.matching.Substitutions;

/**
 * @className Report.java
 * 
 * @author Amgad Ashraf
 * 
 * @version 3.00 31/5/2018
 */
public class Report {
	private Substitutions substitution;
	private Set<Support> supports;
	private boolean sign;
	private String contextName;

	public Report(Substitutions substitution, Set<Support> set, boolean sign, String contextID) {
		this.substitution = substitution;
		this.supports = set;
		this.sign = sign;
		this.contextName = contextID;
	}

	public Substitutions getSubstitutions() {
		return substitution;
	}

	public Set<Support> getSupports() {
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
