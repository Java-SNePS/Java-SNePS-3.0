package sneps.acts;

import java.util.Random;


import sneps.network.classes.setClasses.NodeSet;
import sneps.snip.Runner;
import sneps.network.ActNode;

public class DoAll extends ControlAction {
	
	
	
	public DoAll() {
		super();
		
	}
	
	public DoAll(String identifier) {
		super(identifier);
		
	}

	public void act(ActNode actNode) {
		Random rand = new Random();
		NodeSet acts = actNode.getDownCableSet().getDownCable("do").getNodeSet();
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
