package sneps.network.classes.setClasses;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;

import sneps.snip.Report;

public class ReportSet implements Iterable<Report>, Serializable {
	private static final long serialVersionUID = 508832573392879123L;
	private HashSet<Report> reports;

	public ReportSet() {
		reports = new HashSet<Report>();
	}
	
	public void addReport(Report rport) {
		reports.add(rport);
	}
	
	@Override
	public Iterator<Report> iterator() {
		return reports.iterator();
	}

	public boolean contains(Report report) {
		return reports.contains(report);
	}

	public void clear() {
		reports = new HashSet<Report>();
	}
	
	public boolean isEmpty() {
		return reports.isEmpty();
	}

	

}
