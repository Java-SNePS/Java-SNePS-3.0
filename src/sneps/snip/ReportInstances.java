package sneps.snip;

import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import sneps.snip.channels.Channel;
import sneps.snip.matching.Substitutions;

public class ReportInstances implements Iterable<Report> {
	Hashtable<Substitutions, Set<Report>> instances;

	public ReportInstances() {
		instances = new Hashtable<Substitutions, Set<Report>>();
	}

	public void addReport(Report report) {
		Substitutions reportSubs = report.getSubstitutions();
		Set<Report> reportsSet = instances.remove(reportSubs);
		reportsSet.add(report);
		instances.put(reportSubs, reportsSet);
	}

	public Set<Report> getReportBySubstitutions(Substitutions subs) {
		return instances.get(subs);
	}

	public Iterator<Report> iterator() {
		Set<Report> allMergedReports = new HashSet<Report>();
		Collection<Set<Report>> collectionOfSets = instances.values();
		for (Set<Report> set : collectionOfSets)
			allMergedReports.addAll(set);
		return allMergedReports.iterator();
	}
}
