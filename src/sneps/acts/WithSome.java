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
import sneps.snebr.Controller;
import sneps.snip.Runner;
import sneps.snip.channels.ActToPropositionChannel;
import sneps.snip.matching.LinearSubstitutions;

public class WithSome extends ControlAction {
	public WithSome() {
		super();
		
	}
	
    
	public WithSome(String identifier) {
		super(identifier);
		
	}
	//vars relation, i should check it again
	public void act(ActNode actNode) {
		//NodeSet guards = new NodeSet();
		//ChannelSet c=new ChannelSet();
		switch(actNode.getAgenda()) {
		case START:
			actNode.setAgenda(Agenda.IDENTIFY_OBJECTS);
			Runner.addToActStack(actNode);
			Node n=actNode.getDownCableSet().getDownCable("suchthat").getNodeSet().getNode(0);
			ActToPropositionChannel actTopropChannel=new ActToPropositionChannel(new LinearSubstitutions(), 
					new LinearSubstitutions(),Controller.getCurrentContextName(), actNode, n, true);
			n.receiveRequest(actTopropChannel);
			actNode.getPlansChannel().addChannel(actTopropChannel);
			
		
			break;
		case IDENTIFY_OBJECTS:
			try{
				ArrayList<Node> constants = actNode.processReports();
				actNode.setAgenda(Agenda.DONE);
			
				
				
			
				
				ArrayList<Wire> arr;
				Relation r1 = Relation.obj;
				Relation r2 = Relation.action;
				Relation r3=Relation.doo;
				
				Node doo=actNode.getDownCableSet().getDownCable("do").getNodeSet().getNode(0);
				Node action=doo.getDownCableSet().getDownCable("action").getNodeSet().getNode(0);
				
				ArrayList<ActNode> possibleActs=new ArrayList<>();
				
				Wire wireAction=new Wire(r2,action);
				for(int i=0;i<constants.size();i++) {
					arr=new ArrayList<>();
					arr.add(new Wire(r1,constants.get(i)));
					arr.add(wireAction);
					ActNode act=(ActNode)Network.buildMolecularNode(arr, CaseFrame.act);
					possibleActs.add(act);
				}
				ArrayList<Wire> arrDoOne=new ArrayList<>();
				for(int i=0;i<possibleActs.size();i++) {
					arrDoOne.add(new Wire(r3,possibleActs.get(i)));
				}
				ActionNode doOneAction=(ActionNode)Network.buildActionNode("doOneWithSome","doone");
				arrDoOne.add(new Wire(r2,doOneAction));
				ActNode doOne = (ActNode) Network.buildMolecularNode(arrDoOne, CaseFrame.doAllAct);
				doOne.restartAgenda();
				Runner.addToActStack(doOne);
			
			} catch(Exception e) {
				System.out.println("SOMETHING WENT WRONG!! EXCEPTION THROWN FROM ACTNODE.JAVA 10");
			}
			break;
		default:
			System.out.print("UNIDENTIFIED AGENDA FOR WithSome!!");
			return;	
		}
		
	}

}
