	package sneps.acts;

import java.util.HashSet;
import java.util.Set;

import sneps.exceptions.ContextNameDoesntExistException;
import sneps.exceptions.ContradictionFoundException;
import sneps.exceptions.DuplicatePropositionException;
import sneps.exceptions.NodeNotFoundInNetworkException;
import sneps.exceptions.NodeNotFoundInPropSetException;
import sneps.exceptions.NotAPropositionNodeException;
import sneps.network.ActNode;
import sneps.network.PropositionNode;
import sneps.snebr.Controller;
import sneps.snebr.Support;
import sneps.snip.Report;
import sneps.snip.matching.LinearSubstitutions;

public class Disbelieve extends ActNode {
	
	
	public Disbelieve() {
		super();
	}

	@Override
	public void act() throws NotAPropositionNodeException, NodeNotFoundInNetworkException {
		PropositionNode p = (PropositionNode) this.getDownCableSet().getDownCable("obj").getNodeSet().getNode(0);
		try {
			Controller.removePropositionFromAllContexts(p);
		} catch (NodeNotFoundInPropSetException | NotAPropositionNodeException | NodeNotFoundInNetworkException e) {
			// TODO Auto-generated catch block
			System.out.println("SOMETHING WENT WRONG!! PROPOSITION NOT FOUND");
		}
		
		
		
	}

}
