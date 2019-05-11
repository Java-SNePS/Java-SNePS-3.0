package sneps.snip;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Set;

import sneps.exceptions.NodeNotFoundInNetworkException;
import sneps.exceptions.NotAPropositionNodeException;
import sneps.network.Network;
import sneps.network.PropositionNode;
import sneps.network.classes.setClasses.PropositionSet;
import sneps.snebr.Context;
import sneps.snebr.Support;
import sneps.snip.matching.Substitutions;

public class Report {
	private Substitutions substitution;
	private PropositionSet support;
	private boolean sign;
	private InferenceTypes inferenceType;

	public Report(Substitutions substitution, PropositionSet suppt, boolean sign, InferenceTypes inference) {
		this.substitution = substitution;
		this.support = suppt;
		this.sign = sign;
		this.inferenceType = inference;
	}

	/***
	 * Method should be used if the Report has a support of type Support and not a
	 * PropositionSet
	 * 
	 * @param reportContext
	 * @return
	 * @throws NotAPropositionNodeException
	 * @throws NodeNotFoundInNetworkException
	 */
	/*
	 * public boolean anySupportAssertedInContext(Context reportContext) throws
	 * NotAPropositionNodeException, NodeNotFoundInNetworkException {
	 * Collection<PropositionSet> reportSupportsSet =
	 * support.getAssumptionBasedSupport().values(); PropositionSet
	 * contextHypothesisSet = reportContext.getHypothesisSet(); for (PropositionSet
	 * assumptionHyps : reportSupportsSet) if
	 * (assumptionHyps.isSubSet(contextHypothesisSet)) return true; return false; }
	 */

	public boolean anySupportAssertedInContext(Context reportContext)
			throws NotAPropositionNodeException, NodeNotFoundInNetworkException {
		int[] reportSupportsSet = support.getProps();
		int currentPropNodeId;
		for (int i = 0; i < reportSupportsSet.length; i++) {
			currentPropNodeId = reportSupportsSet[i];
			PropositionNode retrievedNode = (PropositionNode) Network.getNodeById(currentPropNodeId);
			if (retrievedNode.assertedInContext(reportContext))
				return true;
		}
		return false;
	}

	public Substitutions getSubstitutions() {
		return substitution;
	}

	public PropositionSet getSupport() {
		return support;
	}

	@Override
	public boolean equals(Object report) {
		Report castedReport = (Report) report;
		return this.substitution.equals(castedReport.substitution) && this.sign == castedReport.sign;
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

	public InferenceTypes getInferenceType() {
		return inferenceType;
	}

	public void setInferenceType(InferenceTypes inferenceType) {
		this.inferenceType = inferenceType;
	}

}
