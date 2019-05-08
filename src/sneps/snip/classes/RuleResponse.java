package sneps.snip.classes;

import sneps.network.classes.setClasses.NodeSet;
import sneps.snip.Report;

public class RuleResponse {

	private Report report;
	private NodeSet consequents;

	public RuleResponse() {

	}

	public RuleResponse(Report report, NodeSet consequents) {
		this.report = report;
		this.consequents = consequents;
	}

	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
	}

	public NodeSet getConsequents() {
		return consequents;
	}

	public void setConsequents(NodeSet consequents) {
		this.consequents = consequents;
	}
}