package sneps.network.classes.setClasses;

import java.util.HashSet;
import java.util.Iterator;

import sneps.snip.Report;


public class ReportSet implements Iterable<Report> {
	private HashSet<Report> reports;

	public ReportSet() {
		reports = new HashSet<Report>();
	}
	
	public void addReport(Report rport){
		reports.add(rport);
	}
	
	@Override
	public Iterator<Report> iterator(){
		return reports.iterator();
	}

	public boolean contains(Report report) {
		return reports.contains(report);
	}

	public void clear() {
		reports = new HashSet<Report>();
	}

	

}
