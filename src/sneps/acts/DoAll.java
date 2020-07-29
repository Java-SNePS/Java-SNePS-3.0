package sneps.acts;

import java.util.Random;
import sneps.network.classes.setClasses.NodeSet;
import sneps.snip.Runner;
import sneps.network.ActNode;

public class DoAll extends ActNode {
	
	private static DoAll actuator;
	
	public DoAll() {
		super();
	}

	public void act() {
		Random rand = new Random();
		NodeSet acts = this.getDownCableSet().getDownCable("obj").getNodeSet();
		NodeSet actsCopy = new NodeSet();
		actsCopy.addAll(acts);
		while(!actsCopy.isEmpty()) {
			int nextActIndex = rand.nextInt(actsCopy.size());
			ActNode nextAct = (ActNode) actsCopy.getNode(nextActIndex);
			nextAct.restartAgenda();
			Runner.addToActStack(nextAct);
			actsCopy.removeNode(nextAct);
		}
	}	
}
