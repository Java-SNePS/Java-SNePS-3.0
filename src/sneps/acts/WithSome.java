package sneps.acts;

import java.util.ArrayList;

import sneps.network.ActNode;
import sneps.network.Agenda;
import sneps.network.Network;
import sneps.network.Node;
import sneps.network.PropositionNode;
import sneps.network.classes.CaseFrame;
import sneps.network.classes.Relation;
import sneps.network.classes.Wire;
import sneps.network.classes.setClasses.ChannelSet;
import sneps.network.classes.setClasses.NodeSet;
import sneps.snebr.Controller;
import sneps.snip.Runner;
import sneps.snip.channels.AntecedentToRuleChannel;
import sneps.snip.matching.LinearSubstitutions;

public class WithSome extends ActNode {
	public WithSome() {
		super();
	}
	public void act() {
		NodeSet guards = new NodeSet();
		ChannelSet c=new ChannelSet();
		switch(agenda) {
		case START:
			agenda = Agenda.TEST;
			
			for(Node n: getDownCableSet().getDownCable("obj").getNodeSet()) {
				guards.addAll(((Node) n).getDownCableSet().getDownCable("guard").getNodeSet());
			}
			for(Node guard: guards) {
				AntecedentToRuleChannel atrc=new AntecedentToRuleChannel(new LinearSubstitutions(), new LinearSubstitutions(),Controller.getCurrentContext().getName(), this, guard, true);
				guard.receiveRequest(atrc);
				c.addChannel(atrc);
			}
			break;
		case TEST:
			try{
				agenda = Agenda.DONE;
				NodeSet allActs = getDownCableSet().getDownCable("obj").getNodeSet();
				ActNode possibleAct=new ActNode();
				ArrayList<PropositionNode> satisfiedGaurds = processReports(c);
				for(Node act: allActs) {
					for(Node n: ((Node) act).getDownCableSet().getDownCable("guard").getNodeSet()) {
						if(satisfiedGaurds.contains(n)) {
							possibleAct=(ActNode) act;
							break;
						}
					}
					
				}
				ArrayList<Wire> array= new ArrayList<>();
				Relation r1 = Relation.obj;
				Relation r2 = Relation.action;
				array.add(new Wire(r1,possibleAct));
				array.add(new Wire(r2,new DoOne()));
				ActNode doOne = (ActNode) Network.buildMolecularNode(array, CaseFrame.act);
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
