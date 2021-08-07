package sneps.acts;




import sneps.network.ActNode;
import sneps.network.classes.setClasses.NodeSet;
import sneps.snip.Runner;

public class SNSequence extends ControlAction {
	
	
	
	public SNSequence() {
		super();
	}
	
	public SNSequence(String identifier) {
		super(identifier);
		setPrimitive(false);
	}

	@Override
	public void act(ActNode actNode) {
		
		NodeSet acts = actNode.getDownCableSet().getDownCable("do").getNodeSet();
		NodeSet actsCopy = new NodeSet();
		actsCopy.addAll(acts);
		//int nextActIndex = 0;
		while(!actsCopy.isEmpty()) {
			//ActNode nextAct = (ActNode) actsCopy.getNode(nextActIndex);
			ActNode nextAct = (ActNode) actsCopy.getNode(actsCopy.size()-1);
			nextAct.restartAgenda();
			Runner.addToActStack(nextAct);
			actsCopy.removeNode(nextAct);
		}
	}
	

		
	
	

}
