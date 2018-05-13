package sneps.exceptions;

import sneps.network.classes.setClasses.PropositionSet;
import sneps.snebr.Context;
import sneps.snebr.Contradiction;
import sneps.snebr.Controller;

import java.util.ArrayList;

public class ContradictionFoundException extends Exception{

    private ArrayList<PropositionSet> contradictoryHyps;

    public ContradictionFoundException(ArrayList<PropositionSet>  contradictoryHyps)  {
        super("A contradiction has occured!");
        this.contradictoryHyps = contradictoryHyps;
    }
}
