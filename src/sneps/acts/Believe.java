package sneps.acts;

import java.util.HashSet;

import java.util.Set;

import sneps.network.ActionNode;
import sneps.exceptions.ContextNameDoesntExistException;
import sneps.exceptions.ContradictionFoundException;
import sneps.exceptions.DuplicatePropositionException;
import sneps.exceptions.NodeNotFoundInNetworkException;
import sneps.exceptions.NotAPropositionNodeException;
import sneps.network.ActNode;
import sneps.network.PropositionNode;
import sneps.snebr.Controller;
import sneps.snebr.Support;
import sneps.snip.Report;
import sneps.snip.matching.LinearSubstitutions;



public class Believe extends ActionNode {

	
	
	public Believe() {
		super();
		setPrimitive(true);
	}
///////
	public Believe(String identifier) {
		super(identifier);
		setPrimitive(true);
	}
//////
	public void act(ActNode actNode)  {
		PropositionNode p = (PropositionNode) actNode.getDownCableSet().getDownCable("obj").getNodeSet().getNode(0);
		try {
			//Controller.addPropToContext(Controller.getCurrentContext().getName(), p.getId());
			//Controller.addPropToContext("Test context",p.getId());
			Controller.addPropToContext(Controller.getCurrentContextName(), p.getId());
			//p.setBasicSupport();
		} catch (NotAPropositionNodeException e) {
			// TODO Auto-generated catch block
			System.out.println("SOMETHING WENT WRONG!!, TRYING TO ASSERT NOT A PROPOSITION");
		} catch (DuplicatePropositionException e) {
			// TODO Auto-generated catch block
			System.out.println("SOMETHING WENT WRONG!!, PROPOSITION ALREADY ASSERTED");
		} catch (NodeNotFoundInNetworkException e) {
			// TODO Auto-generated catch block
			System.out.println("SOMETHING WENT WRONG!!");
		} catch (ContradictionFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("SOMETHING WENT WRONG!!, CONTRADICTING PROPOSITION IN BELIEF SPACE");
		} catch (ContextNameDoesntExistException e) {
			// TODO Auto-generated catch block
			System.out.println("SOMETHING WENT WRONG!!");
		}
		
		Set<Support> ss = new HashSet<>();
		ss.add(p.getBasicSupport());
		Report r = new Report(new LinearSubstitutions(), ss, true, Controller.getCurrentContext().getName());
		p.broadcastReport(r);
		
	}
}
