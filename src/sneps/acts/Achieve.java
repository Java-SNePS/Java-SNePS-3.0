package sneps.acts;

import java.util.ArrayList;
import java.util.Arrays;

import sneps.exceptions.NodeNotFoundInNetworkException;
import sneps.exceptions.NotAPropositionNodeException;
import sneps.network.ActNode;
import sneps.network.Agenda;
import sneps.network.Network;
import sneps.network.Node;
import sneps.network.PropositionNode;
import sneps.network.VariableNode;
import sneps.network.classes.CaseFrame;
import sneps.network.classes.Relation;
import sneps.network.classes.Wire;
import sneps.network.classes.setClasses.ChannelSet;
import sneps.network.classes.setClasses.NodeSet;
import sneps.snebr.Context;
import sneps.snebr.Controller;
import sneps.snip.Runner;
import sneps.snip.channels.Channel;
import sneps.snip.channels.RuleToConsequentChannel;
import sneps.snip.matching.LinearSubstitutions;

public class Achieve extends ActNode{
	public Achieve() {
		super();
	}
	public void act() throws NotAPropositionNodeException, NodeNotFoundInNetworkException{
		NodeSet planGoals = new NodeSet();
		ChannelSet c=new ChannelSet();
		switch(agenda) {
		case START:
			PropositionNode p = (PropositionNode) this.getDownCableSet().getDownCable("obj").getNodeSet().getNode(0);
			Context context=Controller.getCurrentContext();
			Boolean asserted=context.isAsserted(p);
			if(asserted) {
				return;
			} else {
				try {
					agenda = Agenda.FIND_PLANS;
					Runner.addToActStack(this);
					VariableNode x = Network.buildVariableNode();
					Relation r1 = Relation.plan;
					Relation r2 = Relation.goal;
					ArrayList<Wire> array= new ArrayList<>();
					array.add(new Wire(r1,x));
					array.add(new Wire(r2,p));
					Node planGoal = Network.buildMolecularNode(array, CaseFrame.planGoal);
					planGoals.addNode(planGoal);
					RuleToConsequentChannel rtcc = new RuleToConsequentChannel(new LinearSubstitutions(), new LinearSubstitutions(),
							Controller.getCurrentContext().getName(), this, planGoal, true);
					planGoal.receiveRequest(rtcc);
					c.addChannel(rtcc);
					
					
				} catch(Exception e) {
					System.out.println("SOMETHING WENT WRONG!! EXCEPTION THROWN FROM ACTNODE.JAVA 8");						
				}
			}
			break;
		case FIND_PLANS:
			ArrayList<PropositionNode> temp = processReports(c);
			ArrayList<ActNode> plans=new ArrayList<ActNode>();
			for(Node t:temp) {
				plans.add((ActNode)t);
			}
			if(!plans.isEmpty()) {
				try {
					agenda = Agenda.DONE;
					ArrayList<Wire> array1=new ArrayList<Wire>();
					Relation r1 = Relation.obj;
					Relation r2 = Relation.action;
					for(int i = 0; i < plans.size(); i++) {
						array1.add(new Wire(r1,plans.get(i)));
					}
					array1.add(new Wire(r2,new DoOne()));
					ActNode doOne = (ActNode) Network.buildMolecularNode(array1, CaseFrame.act);
					doOne.restartAgenda();
					Runner.addToActStack(doOne);
				} catch(Exception e) {
					System.out.println("SOMETHING WENT WRONG!! EXCEPTION THROWN FROM ACTNODE.JAVA 9");
				}
			} else {
				System.out.println("Planning Algorithm will conclude the plans");
			}
			break;
		default:
			System.out.print("UNIDENTIFIED AGENDA FOR ACHIEVE!!");
			return;
		}
	}

}
