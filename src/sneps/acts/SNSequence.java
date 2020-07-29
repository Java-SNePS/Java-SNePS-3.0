package sneps.acts;

import java.util.Stack;
import sneps.network.ActNode;
import sneps.network.cables.DownCable;
import sneps.snip.Runner;

public class SNSequence extends ActNode {
	
	
	
	public SNSequence() {
		super();
	}
	@Override
	public void act() {
		Stack<ActNode> acts = new Stack<>();
		int i = 1;
		DownCable next = this.getDownCableSet().getDownCable("obj" + i);
		ActNode act;
		while(next != null) {
			act = (ActNode) next.getNodeSet().getNode(0);
			act.restartAgenda();
			acts.push(act);
			next = this.getDownCableSet().getDownCable("obj" + ++i);
		}
		while(!acts.isEmpty()) {
			Runner.addToActStack(acts.pop());
		}
	}
	

		
	
	

}
