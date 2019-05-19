package sneps.snip.classes;

import java.util.ArrayList;
import java.util.Collection;

import sneps.network.classes.setClasses.ChannelSet;
import sneps.network.classes.setClasses.ReportSet;
import sneps.snip.Report;
import sneps.snip.channels.Channel;

public class RuleResponse {

	private Report report;
	private Collection<Channel> consequentChannels;

	public RuleResponse() {
		consequentChannels = new ArrayList<Channel>();
	}

	public Report getReport() {
		return report;
	}

	public void setReports(Report report) {
		this.report = report;
	}

	public void addReport(Report report) {
		this.report = report;
	}

	public Collection<Channel> getConsequentChannels() {
		return consequentChannels;
	}

	public void setConsequentChannels(Collection<Channel> consequentChannels) {
		this.consequentChannels = consequentChannels;
	}

	public void addChannel(Channel channel) {
		this.consequentChannels.add(channel);
	}
}