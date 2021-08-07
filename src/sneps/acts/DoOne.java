package sneps.acts;

import java.util.Random;

import sneps.network.ActNode;
import sneps.network.classes.setClasses.NodeSet;
import sneps.snip.Runner;

public class DoOne extends ControlAction {
	
	
	public DoOne() {
		super();
		
	}
	
	public DoOne(String identifier) {
		super(identifier);
		
	}

	public void act(ActNode actNode) {
		Random rand = new Random();
		NodeSet possibleActs = actNode.getDownCableSet().getDownCable("do").getNodeSet();
		int actIndex = rand.nextInt(possibleActs.size());
		ActNode act = (ActNode) possibleActs.getNode(actIndex);
		act.restartAgenda();
		Runner.addToActStack(act);
	}
	
	
}
