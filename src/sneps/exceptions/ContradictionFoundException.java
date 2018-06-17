package sneps.exceptions;

import sneps.setClasses.NodeSet;
import sneps.setClasses.PropositionSet;

import java.util.ArrayList;

public class ContradictionFoundException extends Exception{

    private ArrayList<NodeSet> contradictoryHyps;

    public ContradictionFoundException(ArrayList<NodeSet>  contradictoryHyps)  {
        super("A contradiction has occured!");
        this.contradictoryHyps = contradictoryHyps;
    }

    public ArrayList<NodeSet> getContradictoryHyps() {
        return contradictoryHyps;
    }
}
