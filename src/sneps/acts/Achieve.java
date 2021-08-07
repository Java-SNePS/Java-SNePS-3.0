package sneps.acts;

import java.util.ArrayList;


import sneps.network.ActionNode;



import sneps.network.ActNode;
import sneps.network.Agenda;
import sneps.network.Network;
import sneps.network.Node;
import sneps.network.PropositionNode;
import sneps.network.VariableNode;
import sneps.network.classes.CaseFrame;
import sneps.network.classes.Relation;
import sneps.network.classes.Wire;
import sneps.network.classes.setClasses.NodeSet;
import sneps.snebr.Context;
import sneps.snebr.Controller;
import sneps.snip.Runner;
import sneps.snip.channels.ActToPropositionChannel;
import sneps.snip.matching.LinearSubstitutions;

public class Achieve extends ControlAction{
	public Achieve() {
		super();
		
	}
	
	public Achieve(String identifier) {
		super(identifier);
		
	}
	public void act(ActNode actNode) {
		NodeSet planGoals = new NodeSet();
		switch(actNode.getAgenda()) {
		case START:
			PropositionNode p = (PropositionNode) actNode.getDownCableSet().getDownCable("obj").getNodeSet().getNode(0);
			Context context=Controller.getCurrentContext();
			Boolean asserted=false;
			try {
			 asserted=context.isAsserted(p);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			if(asserted) {
				return;
			} else {
				try {
					actNode.setAgenda(Agenda.FIND_PLANS);
					Runner.addToActStack(actNode);//changed from this to actNode that on the actqueue
					VariableNode x = Network.buildVariableNode();
					Relation r1 = Relation.plan;
					Relation r2 = Relation.goal;
					ArrayList<Wire> array= new ArrayList<>();
					array.add(new Wire(r1,x));
					array.add(new Wire(r2,p));
					Node planGoal = Network.buildMolecularNode(array, CaseFrame.planGoal);
					planGoals.addNode(planGoal);
					//RuleToConsequentChannel rtcc = new RuleToConsequentChannel(new LinearSubstitutions(), new LinearSubstitutions(),
						//	Controller.getCurrentContext().getName(), actNode, planGoal, true);
					ActToPropositionChannel actToProp=new ActToPropositionChannel(new LinearSubstitutions(), new LinearSubstitutions(), 
							Controller.getCurrentContextName(), actNode, planGoal, true);
					planGoal.receiveRequest(actToProp);
					actNode.getPlansChannel().addChannel(actToProp);
					
					
				} catch(Exception e) {
					System.out.println("SOMETHING WENT WRONG!! EXCEPTION THROWN FROM ACTNODE.JAVA 8");						
				}
			}
			break;
		case FIND_PLANS:
			ArrayList<Node> plans = actNode.processReports();
			/*
			ArrayList<ActNode> plans=new ArrayList<ActNode>();
			for(Node t:temp) {
				plans.add((ActNode)t);
			}
			*/
			if(!plans.isEmpty()) {
				try {
					actNode.setAgenda(Agenda.DONE);
					ArrayList<Wire> array1=new ArrayList<Wire>();
					Relation r1 = Relation.doo;
					Relation r2 = Relation.action;
					for(int i = 0; i < plans.size(); i++) {
						array1.add(new Wire(r1,plans.get(i)));
					}
					ActionNode doOneAction=(ActionNode)Network.buildActionNode("doOneOfPlans","doone");
					array1.add(new Wire(r2,doOneAction));
					ActNode doOne = (ActNode) Network.buildMolecularNode(array1, CaseFrame.doAllAct);
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
