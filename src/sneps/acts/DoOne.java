package sneps.acts;

import java.util.Random;
import sneps.network.ActNode;
import sneps.network.classes.setClasses.NodeSet;
import sneps.snip.Runner;

public class DoOne extends ActNode {
	private static DoOne actuator;
	
	public DoOne() {
		super();
	}

	public void act() {
		Random rand = new Random();
		NodeSet possibleActs = this.getDownCableSet().getDownCable("obj").getNodeSet();
		int actIndex = rand.nextInt(possibleActs.size());
		ActNode act = (ActNode) possibleActs.getNode(actIndex);
		act.restartAgenda();
		Runner.addToActStack(act);
	}
	
	
}
