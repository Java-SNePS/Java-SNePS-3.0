package sneps.network.classes.setClasses;

import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import sneps.snip.channels.AntecedentToRuleChannel;
import sneps.snip.channels.Channel;
import sneps.snip.channels.ChannelTypes;
import sneps.snip.channels.RuleToConsequentChannel;

public class ChannelSet implements Iterable<Channel> {
	private Hashtable<ChannelTypes, Set<Channel>> channels;

	public ChannelSet() {
		channels = new Hashtable<ChannelTypes, Set<Channel>>();
	}

	public void addChannel(Channel channel) {
		ChannelTypes channelType;
		if (channel instanceof AntecedentToRuleChannel)
			channelType = ChannelTypes.RuleAnt;
		else if (channel instanceof RuleToConsequentChannel)
			channelType = ChannelTypes.RuleCons;
		else
			channelType = ChannelTypes.MATCHED;
		Set<Channel> targetSet = channels.get(channelType);
		targetSet.add(channel);
		channels.put(channelType, targetSet);
	}

	@Override
	public Iterator<Channel> iterator() {
		Set<Channel> allMergedChannels = new HashSet<Channel>();
		Collection<Set<Channel>> collectionOfSets = channels.values();
		for (Set<Channel> set : collectionOfSets)
			allMergedChannels.addAll(set);
		return allMergedChannels.iterator();
	}

	/***
	 * Method acting as a filter for quick HashSet filtering applied on channels
	 * based on request processing status.
	 *
	 * @param processedRequest boolean expressing filter criteria
	 * @return newly created ChannelSet
	 */
	public ChannelSet getFilteredRequestChannels(boolean processedRequest) {
		ChannelSet processedRequestsChannels = new ChannelSet();
		Set<Channel> allMergedChannels = new HashSet<Channel>();
		Collection<Set<Channel>> collectionOfSets = channels.values();
		for (Set<Channel> set : collectionOfSets)
			allMergedChannels.addAll(set);
		for (Channel channel : allMergedChannels) {
			if (channel.isRequestProcessed() == processedRequest)
				processedRequestsChannels.addChannel(channel);
		}
		return processedRequestsChannels;
	}

	public Set<Channel> getChannels() {
		Set<Channel> allMergedChannels = new HashSet<Channel>();
		Collection<Set<Channel>> collectionOfSets = channels.values();
		for (Set<Channel> set : collectionOfSets)
			allMergedChannels.addAll(set);
		return allMergedChannels;
	}

	public Set<Channel> getAntRuleChannels() {
		return channels.get(ChannelTypes.RuleAnt);
	}

	public Set<Channel> getRuleConsChannels() {
		return channels.get(ChannelTypes.RuleCons);
	}

	public Set<Channel> getMatchChannels() {
		return channels.get(ChannelTypes.MATCHED);
	}
}
