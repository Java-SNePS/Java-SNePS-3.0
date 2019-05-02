package sneps.snip;

import java.util.Collection;

import sneps.network.classes.setClasses.PropositionSet;
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
	private Collection<PropositionSet> supports;
	private boolean sign;

	public Report(Substitutions substitution, Collection<PropositionSet> set, boolean sign) {
		this.substitution = substitution;
		this.supports = set;
		this.sign = sign;
	}

	public Substitutions getSubstitutions() {
		return substitution;
	}

	public Collection<PropositionSet> getSupports() {
		return supports;
	}

	@Override
	public boolean equals(Object report) {
		Report castedReport = (Report) report;
		return this.substitution.equals(castedReport.substitution) 
				&& this.sign == castedReport.sign;
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
		return "Sign: " + sign + "\nSubstitution: " + substitution + "\nSupport: " + supports.toString();
	}

}
