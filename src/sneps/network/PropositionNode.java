package sneps.network;

import sneps.network.classes.setClasses.ChannelSet;
import sneps.network.classes.setClasses.ReportSet;
import sneps.network.classes.term.Term;
import sneps.snebr.Supports;
//TODO Proposition Nodes globally stored
public class PropositionNode extends Node {
	private Supports basicSupport;//TODO Change to Support
	
	protected ChannelSet outgoingChannels;
	protected ChannelSet incomingChannels;
	protected ReportSet knownInstances;
	protected ReportSet newInstances;

	public PropositionNode() {
		outgoingChannels = new ChannelSet();
		incomingChannels = new ChannelSet();
		knownInstances = new ReportSet();
	}
	public PropositionNode(Term trm) {
		this();
		setTerm(trm);
	}
	
	
	
	
	
	
	
	
	
	
	public Supports getBasicSupport() {
		return basicSupport;
	}
	public void setBasicSupport(Supports basicSupport) {
		this.basicSupport = basicSupport;
	}
	public ChannelSet getOutgoingChannels() {
		return outgoingChannels;
	}
	public void setOutgoingChannels(ChannelSet outgoingChannels) {
		this.outgoingChannels = outgoingChannels;
	}
	public ChannelSet getIncomingChannels() {
		return incomingChannels;
	}
	public void setIncomingChannels(ChannelSet incomingChannels) {
		this.incomingChannels = incomingChannels;
	}
	public ReportSet getKnownInstances() {
		return knownInstances;
	}
	public void setKnownInstances(ReportSet knownInstances) {
		this.knownInstances = knownInstances;
	}

}
