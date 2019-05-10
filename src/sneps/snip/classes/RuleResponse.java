package sneps.snip.classes;

import java.util.ArrayList;

import sneps.network.classes.setClasses.NodeSet;
import sneps.snip.Report;

public class RuleResponse {
	
	private ArrayList<Report> reports;
	private NodeSet consequents;
	
	public RuleResponse() {
		this.reports = new ArrayList<Report>();
		this.consequents = new NodeSet();
	}
	
	public RuleResponse(ArrayList<Report> reports, NodeSet consequents) {
		this.reports = reports;
		this.consequents = consequents;
	}

	public ArrayList<Report> getReport() {
		return reports;
	}

	public void setReport(ArrayList<Report> reports) {
		this.reports = reports;
	}
	
	public void addReport(Report r) {
		reports.add(r);
	}

	public NodeSet getConsequents() {
		return consequents;
	}

	public void setConsequents(NodeSet consequents) {
		this.consequents = consequents;
	}
	

}
