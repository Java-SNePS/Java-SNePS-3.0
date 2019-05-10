package sneps.snip;

import java.util.Collection;

import sneps.exceptions.NodeNotFoundInNetworkException;
import sneps.exceptions.NotAPropositionNodeException;
import sneps.network.classes.setClasses.PropositionSet;
import sneps.snebr.Context;
import sneps.snebr.Support;
import sneps.snip.matching.Substitutions;

public class Report {
	private Substitutions substitution;
	private Support support;
	private boolean sign;

	public Report(Substitutions substitution, Support suppt, boolean sign) {
		this.substitution = substitution;
		this.support = suppt;
		this.sign = sign;
	}

	public boolean anySupportAssertedInContext(Context reportContext)
			throws NotAPropositionNodeException, NodeNotFoundInNetworkException {
		Collection<PropositionSet> reportSupportsSet = support.getAssumptionBasedSupport().values();
		PropositionSet contextHypothesisSet = reportContext.getHypothesisSet();
		for (PropositionSet assumptionHyps : reportSupportsSet)
			if (assumptionHyps.isSubSet(contextHypothesisSet))
				return true;
		return false;
	}

	public Substitutions getSubstitutions() {
		return substitution;
	}

	public Support getSupport() {
		return support;
	}

	@Override
	public boolean equals(Object report) {
		Report castedReport = (Report) report;
		return this.substitution.equals(castedReport.substitution) && 
				this.sign == castedReport.sign;
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

	public void toggleSign() {
		this.sign = !this.sign;
	}

	public String toString() {
		return "Sign: " + sign + "\nSubstitution: " + substitution + "\nSupport: " + support;
	}

}