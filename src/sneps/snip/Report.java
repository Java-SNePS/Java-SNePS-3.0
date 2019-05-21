package sneps.snip;

import sneps.exceptions.NodeNotFoundInNetworkException;
import sneps.exceptions.NotAPropositionNodeException;
import sneps.network.Network;
import sneps.network.PropositionNode;
import sneps.network.classes.setClasses.PropositionSet;
import sneps.snebr.Context;
import sneps.snip.matching.Substitutions;

public class Report {
	private Substitutions substitution;
	
	/**
	 * Contains the id of the proposition node denoting the actual instance 
	 * represented by this Report
	 */
	private PropositionSet support;
	private boolean sign;
	private InferenceTypes inferenceType;

	public Report(Substitutions substitution, PropositionSet suppt, boolean sign, InferenceTypes inference) {
        this.substitution = substitution;
        this.support = suppt;
        this.sign = sign;
        this.inferenceType = inference;
    }

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
    
    public Report combine(Report r) {
    	if(substitution.isCompatible(r.getSubstitutions())) {
    		PropositionSet combinedSupport = new PropositionSet();
    		try {
				combinedSupport = support.union(r.support);
			} catch (NotAPropositionNodeException | 
					NodeNotFoundInNetworkException e1) {
				e1.printStackTrace();
			}
    		
    		InferenceTypes resultingType;
    		if(inferenceType.equals(InferenceTypes.FORWARD) || 
					r.getInferenceType().equals(InferenceTypes.FORWARD))
				resultingType = InferenceTypes.FORWARD;
			else
				resultingType = InferenceTypes.BACKWARD;
    		
    		return new Report(substitution.union(r.getSubstitutions()), 
					combinedSupport, sign, resultingType);
    	}
    	
    	return null;
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
		return "Sign: " + sign + "\nSubstitution: " + substitution + "\nSupport: " 
	                 + support + "\nType: " + inferenceType;
	}
	
	public InferenceTypes getInferenceType() {
        return inferenceType;
    }
	
    public void setInferenceType(InferenceTypes inferenceType) {
        this.inferenceType = inferenceType;
    }

}