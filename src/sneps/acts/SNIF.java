package sneps.acts;

import java.util.ArrayList;


import sneps.network.ActionNode;
import sneps.network.ActNode;
import sneps.network.Agenda;
import sneps.network.Network;
import sneps.network.Node;
import sneps.network.classes.CaseFrame;
import sneps.network.classes.Relation;
import sneps.network.classes.Wire;
import sneps.network.classes.setClasses.NodeSet;
import sneps.snebr.Controller;
import sneps.snip.Runner;
import sneps.snip.channels.ActToPropositionChannel;
import sneps.snip.matching.LinearSubstitutions;

public class SNIF extends ControlAction {
	public SNIF() {
		super();
	}
	
	public SNIF(String identifier) {
		super(identifier);
		
	}

	public void act(ActNode actNode){
		NodeSet guards = new NodeSet();
	
		switch(actNode.getAgenda()) {
		case START:
			actNode.setAgenda(Agenda.TEST);
			Runner.addToActStack(actNode);
			
			for(Node n: actNode.getDownCableSet().getDownCable("obj").getNodeSet()) {
				guards.addAll(((Node) n).getDownCableSet().getDownCable("guard").getNodeSet());
			}
			for(Node guard: guards) {
				ActToPropositionChannel atrc=new ActToPropositionChannel(new LinearSubstitutions(), 
						new LinearSubstitutions(),Controller.getCurrentContextName(), actNode, guard, true);
				guard.receiveRequest(atrc);//changed the requester from this to actNode
				actNode.getAssertingPreconditionsChannel().addChannel(atrc);
			}
			break;
		case TEST:
			try{
				ArrayList<Node> satisfiedGaurds = actNode.processReports();
				actNode.setAgenda(Agenda.DONE);
				NodeSet allGuardActs = actNode.getDownCableSet().getDownCable("obj").getNodeSet();
				ArrayList<ActNode> possibleActs = new ArrayList<>();
				for(Node guardAct: allGuardActs) {
					boolean containsAll = true;
					for(Node n: ((Node) guardAct).getDownCableSet().getDownCable("guard").getNodeSet()) {
						if(!satisfiedGaurds.contains(n)) {
							containsAll = false;
							break;
						}
					}
					if(containsAll) {
						possibleActs.add((ActNode) guardAct.getDownCableSet().getDownCable("act").getNodeSet().getNode(0));
					}
				}
				ArrayList<Wire> array= new ArrayList<>();
				Relation r1 = Relation.doo;
				Relation r2 = Relation.action;
				for(int i = 0; i < possibleActs.size(); i++) {
					array.add(new Wire(r1,possibleActs.get(i)));
				}
				ActionNode doOneAction= (ActionNode)Network.buildActionNode("doOne" ,"doone");
				array.add(new Wire(r2,doOneAction));
				ActNode doOne = (ActNode) Network.buildMolecularNode(array, CaseFrame.doAllAct);
				doOne.restartAgenda();
				Runner.addToActStack(doOne);
			} catch(Exception e) {
				e.printStackTrace();
				System.out.println("SOMETHING WENT WRONG!! EXCEPTION THROWN FROM ACTNODE.JAVA 10");
			}
			break;
		default:
			System.out.print("UNIDENTIFIED AGENDA FOR SNiF!!");
			return;
		}
	}
	

}
