package sneps.snip.channels;

import sneps.network.Node;
import sneps.network.classes.setClasses.ReportSet;
import sneps.snip.Filter;
import sneps.snip.Report;
import sneps.snip.Runner;
import sneps.snip.Switch;
import sneps.snip.matching.Substitutions;

public abstract class Channel {

	private Filter filter;
	private Switch switch_;
	private String contextName;
	private Node requester;
	private Node reporter;
	private boolean valve;
	private ReportSet reportsBuffer;

	public Channel() {
		filter = new Filter();
		switch_ = new Switch();
		reportsBuffer = new ReportSet();
	}

	public Channel(Substitutions switchSubstitution, 
			Substitutions filterSubstitutions, 
			String contextID, Node requester,
			Node reporter, boolean v) {
		this.filter = new Filter(filterSubstitutions);
		this.switch_ = new Switch(switchSubstitution);
		this.contextName = contextID;
		this.requester = requester;
		this.reporter = reporter;
		this.valve = v;
		this.reporter = reporter;
		reportsBuffer = new ReportSet();
	}

	

	public boolean addReport(Report report) {
		System.out.println("Can pass " + filter.canPass(report));
		if (filter.canPass(report) && contextName == report.getContextName()) {
			System.out.println("\n\nThe Switch data:\n" + switch_);
			switch_.switchReport(report);
			reportsBuffer.addReport(report);
			Runner.addToHighQueue(requester);
			return true;
		}
		return false;
	}


	public String getContextName() {
		return contextName;
	}
	public boolean isValveOpen() {
		return valve;
	}
	public Filter getFilter() {
		return filter;
	}
	public Switch getSwitch() {
		return switch_;
	}
	public Node getRequester() {
		return requester;
	}
	public Node getReporter() {
		return reporter;
	}
	public ReportSet getReportsBuffer() {
		return reportsBuffer;
	}
	public void setValve(boolean valve) {
		this.valve = valve;
	}
	public void clearReportsBuffer() {
		reportsBuffer.clear();
	}
}
