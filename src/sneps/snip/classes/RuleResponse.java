package sneps.snip.classes;

import java.util.ArrayList;
import java.util.Collection;

import sneps.network.classes.setClasses.ChannelSet;
import sneps.network.classes.setClasses.ReportSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import sneps.snip.Report;
import sneps.snip.channels.Channel;

public class RuleResponse {

	private Report report;
	private Collection<Channel> consequentChannels;

	public RuleResponse() {
		consequentChannels = new HashSet<Channel>();
	}

	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
	}

	public void addReport(Report report) {
		this.report = report;
	}

	public Collection<Channel> getConsequentChannels() {
		return consequentChannels;
	}

	public void setConsequentChannels(Set<Channel> consequentChannels) {
		this.consequentChannels = consequentChannels;
	}

	public void addChannel(Channel c) {
		this.consequentChannels.add(c);
	}

	public void addAllChannels(Set<Channel> channels) {
		for(Channel c : channels) {
			this.consequentChannels.add(c);
		}
	}

	public void clear() {
		consequentChannels.clear();
	}

}
