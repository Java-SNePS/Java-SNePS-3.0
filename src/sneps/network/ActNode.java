package sneps.network;

import java.io.Serializable;

import sneps.snip.matching.Binding;
import sneps.snip.matching.Substitutions;
import sneps.snip.channels.ActToPropositionChannel;
import sneps.acts.ControlAction;
import sneps.network.classes.Wire;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Vector;

import sneps.acts.UserDefinedAct;
import sneps.exceptions.NodeNotFoundInNetworkException;
import sneps.exceptions.NotAPropositionNodeException;
import sneps.network.classes.CaseFrame;
import sneps.network.classes.Relation;
import sneps.network.classes.Semantic;
import sneps.network.classes.setClasses.ChannelSet;
import sneps.network.classes.setClasses.NodeSet;
import sneps.network.classes.setClasses.ReportSet;
import sneps.network.classes.term.Term;
import sneps.snebr.Controller;
import sneps.snip.Report;
import sneps.snip.Runner;
import sneps.snip.channels.Channel;
import sneps.snip.channels.MatchChannel;
import sneps.snip.matching.LinearSubstitutions;
import sneps.acts.Achieve;
import sneps.acts.DoAll;
import sneps.acts.DoOne;
import sneps.acts.Believe;

public class ActNode extends Node implements Serializable {

	protected Agenda agenda;
	


	ChannelSet preconditionsChannel;
	ChannelSet assertingPreconditionsChannel;
	ChannelSet effectsChannel;
	ChannelSet plansChannel;





	public ActNode(Semantic sem, Term term) {
		super(sem, term);
		agenda = Agenda.START;
		preconditionsChannel = new ChannelSet();
		assertingPreconditionsChannel = new ChannelSet();
		effectsChannel = new ChannelSet();
		plansChannel = new ChannelSet();
	}

	public ActNode() {
		super();
		agenda = Agenda.DONE;
		preconditionsChannel = new ChannelSet();
		assertingPreconditionsChannel = new ChannelSet();
		effectsChannel = new ChannelSet();
		plansChannel = new ChannelSet();
	}

	public void act() {

	}

	public void processIntends() {
		Node action = getDownCableSet().getDownCable("action").getNodeSet().getNode(0);
		if (action instanceof ControlAction) {
			((ControlAction) action).act(this);
			return;
		}
	
		switch (agenda) {
		case START:
			try {
				agenda = Agenda.FIND_PRECONDITIONS;
				Runner.addToActStack(this);
				VariableNode x = Network.buildVariableNode();
				Relation r1 = Relation.precondition;
				Relation r2 = Relation.act;
				Wire precondition = new Wire(r1, x);
				Wire act = new Wire(r2, this);
				ArrayList<Wire> arrWires = new ArrayList<>();
				arrWires.add(precondition);
				arrWires.add(act);
				Node preActNode = Network.buildMolecularNode(arrWires, CaseFrame.preconditionAct);
				ActToPropositionChannel atpChannel = new ActToPropositionChannel(new LinearSubstitutions(),
						new LinearSubstitutions(), Controller.getCurrentContextName(), this, preActNode, true);
				preconditionsChannel.addChannel(atpChannel);
				preActNode.receiveRequest(atpChannel);

			} catch (Exception e) {
				e.printStackTrace();
			}

			break;
		case FIND_PRECONDITIONS:
			
			ArrayList<Node> preconditions = processReports();

			if (!preconditions.isEmpty()) {
				agenda = Agenda.TEST;
				Runner.addToActStack(this);

				for (Node precondition : preconditions) {
					ActToPropositionChannel atpChannel = new ActToPropositionChannel(new LinearSubstitutions(),
							new LinearSubstitutions(), Controller.getCurrentContextName(), this, precondition, true);
					assertingPreconditionsChannel.addChannel(atpChannel);
					precondition.receiveRequest(atpChannel);

				}

			} else {
				try {
					//finding effects 
					agenda = Agenda.FIND_EFFECTS;
					Runner.addToActStack(this);

					Relation r1 = Relation.act;
					Relation r2 = Relation.effect;

					VariableNode x = Network.buildVariableNode();
					ArrayList<Wire> arrWires = new ArrayList<>();
					Wire act = new Wire(r1, this);
					Wire effect = new Wire(r2, x);
					arrWires.add(act);
					arrWires.add(effect);
					Node actEffect = Network.buildMolecularNode(arrWires, CaseFrame.actEffect);
					

					ActToPropositionChannel atpChannel = new ActToPropositionChannel(new LinearSubstitutions(),
							new LinearSubstitutions(), Controller.getCurrentContextName(), this, actEffect, true);

					actEffect.receiveRequest(atpChannel);
					effectsChannel.addChannel(atpChannel);

				}

				catch (Exception e) {
					e.printStackTrace();
				}
			}

			break;
		case TEST:
			boolean allPreconditionsAsserted = false;
			
			ArrayList<PropositionNode> preconditions2 = processReportsPreconditions();
			ArrayList<Node> assertedPreconditions = processReports();
			
			if (assertedPreconditions.size() == preconditions2.size()) {
				allPreconditionsAsserted = true;
			}

			
			if (allPreconditionsAsserted) {
				try {
					agenda = Agenda.FIND_EFFECTS;
					Runner.addToActStack(this);

					Relation r1 = Relation.act;
					Relation r2 = Relation.effect;

					VariableNode x = Network.buildVariableNode();
					ArrayList<Wire> arrWires = new ArrayList<>();
					Wire act = new Wire(r1, this);
					Wire effect = new Wire(r2, x);
					arrWires.add(act);
					arrWires.add(effect);
					Node actEffect = Network.buildMolecularNode(arrWires, CaseFrame.actEffect);
					
					ActToPropositionChannel atpChannel = new ActToPropositionChannel(new LinearSubstitutions(),
							new LinearSubstitutions(), Controller.getCurrentContextName(), this, actEffect, true);

					actEffect.receiveRequest(atpChannel);
					effectsChannel.addChannel(atpChannel);
				} catch (Exception e) {
					e.printStackTrace();
				}

			} else {
				try {

					agenda = Agenda.START;
					this.restartChannelSets();
					Runner.addToActStack(this);

					Relation r1 = Relation.obj;
					Relation r2 = Relation.action;
					Relation r3 = Relation.doo;
					ArrayList<Wire> doAllarr = new ArrayList<>();

				
					for (PropositionNode precondition : preconditions2) {
						
						try {
							//wil try to acheive the precondition
							if (!assertedPreconditions.contains(precondition)) {
								// achieve the precondition
								ActionNode achieveAction = new Achieve("aaa");
								Network.getNodes().put("achieveAction", achieveAction);
								Network.getNodesWithIDs().add(achieveAction.getId(), achieveAction);

								ArrayList<Wire> achievearr = new ArrayList<>();

								achievearr.add(new Wire(r1, precondition));
								achievearr.add(new Wire(r2, achieveAction));

								ActNode achieve = (ActNode) Network.buildMolecularNode(achievearr, CaseFrame.act);
                                achieve.restartAgenda(); 
								
								doAllarr.add(new Wire(r3, achieve));
							}

						} catch (Exception e) {
							e.printStackTrace();
						}

					}
                    
					ActionNode doAllaction=(ActionNode)Network.buildActionNode("doAllAcheivePreconditions","doall");
				
					doAllarr.add(new Wire(r2, doAllaction));

					ActNode doAll = (ActNode) Network.buildMolecularNode(doAllarr, CaseFrame.doAllAct);

					doAll.restartAgenda();
					Runner.addToActStack(doAll);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		

			break;
		case FIND_EFFECTS:
		
			ArrayList<Node> effects = processReports();

			if (!effects.isEmpty()) {
				try {
					Relation r1 = Relation.obj;
					Relation r2 = Relation.action;
					Relation r3 = Relation.doo;
					ArrayList<Wire> doAllarr = new ArrayList<>();
				
					for (Node prop : effects) {
						ActionNode belAction = (ActionNode) Network.buildActionNode("belEffects", "believe");

						ArrayList<Wire> believearr = new ArrayList<>();

						believearr.add(new Wire(r1, prop));
						believearr.add(new Wire(r2, belAction));

						ActNode believe = (ActNode) Network.buildMolecularNode(believearr, CaseFrame.act);

						doAllarr.add(new Wire(r3, believe));
					}

					ActionNode doAllAction = (ActionNode) Network.buildActionNode("doAlleffects", "doall");
					doAllarr.add(new Wire(r2, doAllAction));

					ActNode doAll = (ActNode) Network.buildMolecularNode(doAllarr, CaseFrame.doAllAct);
					doAll.restartAgenda();
					Runner.addToActStack(doAll);

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
			agenda = Agenda.EXECUTE;
			Runner.addToActStack(this);
			break;
		case EXECUTE:
			if (((ActionNode) action).isPrimitive()) {
				agenda = Agenda.DONE;
				((ActionNode) action).act(this);
				
			} else {
				try {
					agenda = Agenda.FIND_PLANS;
					Runner.addToActStack(this);

					Relation r1 = Relation.plan;
					Relation r2 = Relation.act;
					ArrayList<Wire> planActarr = new ArrayList<>();
					VariableNode x = Network.buildVariableNode();

					planActarr.add(new Wire(r1, x));
					planActarr.add(new Wire(r2, this));

					Node planAct = Network.buildMolecularNode(planActarr, CaseFrame.planAct);

					ActToPropositionChannel atpChannel = new ActToPropositionChannel(new LinearSubstitutions(),
							new LinearSubstitutions(), Controller.getCurrentContextName(), this, planAct, true);

					planAct.receiveRequest(atpChannel);
					plansChannel.addChannel(atpChannel);
				}

				catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;
		case FIND_PLANS:
			
			ArrayList<Node> plans = processReports();
			if (!plans.isEmpty()) {
				try {
					agenda = Agenda.DONE;
					Relation r1 = Relation.action;
					Relation r2 = Relation.doo;
					ArrayList<Wire> doOnearr = new ArrayList<>();
					ActionNode doOneAction = (ActionNode) Network.buildActionNode("doOneAction", "doone");
					doOnearr.add(new Wire(r1, doOneAction));

					for (Node plan : plans) {
						doOnearr.add(new Wire(r2, plan));
					}

					ActNode doOne = (ActNode) Network.buildMolecularNode(doOnearr, CaseFrame.doAllAct);
					doOne.restartAgenda();
					Runner.addToActStack(doOne);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("Classical planning algorithm needed");

			}
		case DONE:
			break;

		default:
			break;

		}

	}

	public Agenda getAgenda() {
		// TODO Auto-generated method stub
		return agenda;
	}
	
	public void setAgenda(Agenda agenda) {
		this.agenda = agenda;
	}


	public void restartAgenda() {
		agenda = Agenda.START;
	}

	
	public ArrayList<Node> processSingleChannelReports(Channel currentChannel) {
		ArrayList<Node> result = new ArrayList<Node>();
		ReportSet channelreports = currentChannel.getReportsBuffer();

		if (agenda.equals(Agenda.FIND_PRECONDITIONS) || agenda.equals(Agenda.FIND_EFFECTS)
				|| agenda.equals(Agenda.FIND_PLANS) || (agenda.equals(Agenda.IDENTIFY_OBJECTS))) {
			for (Report r : channelreports) {
				LinearSubstitutions sub = (LinearSubstitutions) r.getSubstitutions();
				Vector<Binding> bindings = sub.getSub();
				for (int i = 0; i < bindings.size(); i++) {
					Binding B = sub.getBinding(i);
					result.add(B.getNode());
				}

			}
		} else if (agenda.equals(Agenda.TEST)) {
			if (channelreports.iterator().hasNext()) {
				result.add((PropositionNode) currentChannel.getReporter());
			}

		}
		return result;

	}

	public ArrayList<Node>  processReports(){
		ArrayList<Node> result = new ArrayList<Node>();
		switch(agenda) {
		case FIND_PRECONDITIONS:
			for (Channel currentChannel : preconditionsChannel) {
				result.addAll(processSingleChannelReports(currentChannel));
			}
			break;
		case TEST:
			for (Channel currentChannel :assertingPreconditionsChannel ) {
				result.addAll(processSingleChannelReports(currentChannel));

			}
		     break;
		     
		case FIND_EFFECTS:
			for (Channel currentChannel :effectsChannel ) {
				result.addAll(processSingleChannelReports(currentChannel));

			}
		     break;
		
		case FIND_PLANS: 
			for (Channel currentChannel :plansChannel ) {
				result.addAll(processSingleChannelReports(currentChannel));
			}
		     break;
		     
		case IDENTIFY_OBJECTS:
			for (Channel currentChannel :plansChannel ) {
				result.addAll(processSingleChannelReports(currentChannel));
			}
			break;
		}
		
		return result;
		
		
	}
	
	
	private ArrayList<PropositionNode> processReportsPreconditions(){
		ArrayList<PropositionNode> result = new ArrayList<PropositionNode>();
		
		for (Channel currentChannel : preconditionsChannel) {
			ReportSet channelreports = currentChannel.getReportsBuffer();
		
			for (Report r : channelreports) {
				LinearSubstitutions sub = (LinearSubstitutions) r.getSubstitutions();
				Vector<Binding> bindings = sub.getSub();
				for (int i = 0; i < bindings.size(); i++) {
					Binding B = sub.getBinding(i);
					result.add((PropositionNode) B.getNode());
				}
		}
		
	}
	return result;	
		
	}
	
	

	
	public void restartChannelSets() {
		preconditionsChannel=new ChannelSet();
		assertingPreconditionsChannel=new ChannelSet();
		effectsChannel=new ChannelSet();
		plansChannel=new ChannelSet();
		
		
	}
	
	public ChannelSet getAssertingPreconditionsChannel() {
		return assertingPreconditionsChannel;
	}
   
	

	public ChannelSet getPlansChannel() {
		return plansChannel;
	}
	

}
