	package sneps.acts;






import sneps.network.ActionNode;
import sneps.exceptions.NodeNotFoundInNetworkException;
import sneps.exceptions.NodeNotFoundInPropSetException;
import sneps.exceptions.NotAPropositionNodeException;
import sneps.network.ActNode;
import sneps.network.PropositionNode;
import sneps.snebr.Controller;

public class Disbelieve extends ActionNode {
	
	
	public Disbelieve() {
		super();
	}
	
	public Disbelieve(String identifier) {
		super(identifier);
		setPrimitive(true);
	}


	@Override
	public void act(ActNode actNode)  {
		PropositionNode p = (PropositionNode) actNode.getDownCableSet().getDownCable("obj").getNodeSet().getNode(0);
		try {
			Controller.removePropositionFromAllContexts(p);

		} catch (NodeNotFoundInPropSetException | NotAPropositionNodeException | NodeNotFoundInNetworkException e) {
			// TODO Auto-generated catch block
			System.out.println("SOMETHING WENT WRONG!! PROPOSITION NOT FOUND");
		}
		
		
		
	}

}
