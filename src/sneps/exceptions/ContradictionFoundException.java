package sneps.exceptions;

import sneps.network.classes.setClasses.PropositionSet;

import java.util.ArrayList;

public class ContradictionFoundException extends Exception{

    private ArrayList<PropositionSet> contradictoryHyps;

    public ContradictionFoundException(ArrayList<PropositionSet>  contradictoryHyps)  {
        super("A contradiction has occured!");
        this.contradictoryHyps = contradictoryHyps;
    }

	public ArrayList<PropositionSet> getContradictoryHyps() {
		return contradictoryHyps;
	}
    
}
