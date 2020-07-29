package sneps.network;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import sneps.acts.UserDefinedAct;
import sneps.exceptions.NodeNotFoundInNetworkException;
import sneps.exceptions.NotAPropositionNodeException;
import sneps.network.classes.Semantic;
import sneps.network.classes.setClasses.ChannelSet;
import sneps.network.classes.setClasses.NodeSet;
import sneps.network.classes.setClasses.ReportSet;
import sneps.network.classes.term.Term;
import sneps.snip.Report;
import sneps.snip.channels.Channel;

public class ActNode extends Node implements Serializable{
	
	protected Agenda agenda;

	public ActNode(Semantic sem, Term term) {
		super(sem, term);
		agenda = Agenda.DONE;
	}
	
	public ActNode() {
		super();
		agenda = Agenda.DONE;
	}

	public void act() throws NotAPropositionNodeException, NodeNotFoundInNetworkException {
		//check parameter
	}

	public void processIntends() {
		// TODO Auto-generated method stub
		
	}
	
	public Agenda getAgenda() {
		// TODO Auto-generated method stub
		return agenda;
	}
	
	public void restartAgenda() {
		agenda = Agenda.START;
	}
	

	
	public ArrayList<PropositionNode> processSingleChannelReports1(Channel currentChannel,NodeSet guards) {
		ArrayList<PropositionNode> satisfiedGaurds = new ArrayList<>();
		ReportSet channelreports = currentChannel.getReportsBuffer();
		ArrayList<ReportSet> guardreports=new ArrayList<ReportSet>();
		for(Node guard:guards) {
			guardreports.add(((PropositionNode)guard).getKnownInstances());
		}
		for(int i=0;i<guardreports.size();i++) {
			for(Report gr:guardreports.get(i)) {
				for(Report cr:channelreports) {
					if(gr.equals(cr)) {
						satisfiedGaurds.add((PropositionNode)guards.getNode(i));
					}
				}
			}
		}
		return satisfiedGaurds;
	}
	
	public PropositionNode processSingleChannelReports(Channel currentChannel){
		ReportSet channelreports = currentChannel.getReportsBuffer();
		PropositionNode p = null;
		for(Report r:channelreports) {
			if(r==null) {
				break;
			}
			else {
				p=(PropositionNode) currentChannel.getReporter();
			}
		}
		return p;
	}
	
	public ArrayList<PropositionNode> processReports1(ChannelSet c,NodeSet g) {
		ArrayList<PropositionNode> sg=new ArrayList<PropositionNode>();
		ArrayList<PropositionNode> f=new ArrayList<PropositionNode>();
		for (Channel a : c) {
			sg= processSingleChannelReports1(a,g);
			for(PropositionNode b:sg) {
				f.add(b);
			}
		}
		return f;
	}
	public ArrayList<PropositionNode> processReports(ChannelSet c){
		ArrayList<PropositionNode> f=new ArrayList<PropositionNode>();
		PropositionNode p;
		for (Channel a : c) {
			p=processSingleChannelReports(a);
			if(p!=null) {
				f.add(p);
			}
			
	}
		return f;
	}
	
}
