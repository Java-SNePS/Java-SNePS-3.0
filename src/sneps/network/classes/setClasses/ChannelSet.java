package sneps.network.classes.setClasses;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import sneps.snip.channels.AntecedentToRuleChannel;
import sneps.snip.channels.Channel;
import sneps.snip.channels.ChannelIdentifier;
import sneps.snip.channels.ChannelTypes;
import sneps.snip.channels.RuleToConsequentChannel;
import sneps.snip.matching.Substitutions;

public class ChannelSet implements Iterable<Channel> {
	private Hashtable<ChannelTypes, Hashtable<ChannelIdentifier, Channel>> channels;

	public ChannelSet() {
		channels = new Hashtable<ChannelTypes, Hashtable<ChannelIdentifier, Channel>>();
	}

	public Channel addChannel(Channel channel) {
		int channelRequesterId = channel.getRequester().getId();
		int channelReporterId = channel.getReporter().getId();
		String channelContextName = channel.getContextName();
		Substitutions filterSubs = channel.getFilter().getSubstitutions();
		Substitutions switchSubs = channel.getSwitch().getSubstitutions();
		ChannelTypes channelType = getChannelType(channel);
		Hashtable<ChannelIdentifier, Channel> targetSet = channels.remove(channelType);
		ChannelIdentifier channelId = new ChannelIdentifier(channelRequesterId, channelReporterId, channelContextName,
				filterSubs, switchSubs);
		Channel added = targetSet.put(channelId, channel);
		channels.put(channelType, targetSet);
		return added;
	}

	public Channel removeChannel(Channel channel) {
		int channelRequesterId = channel.getRequester().getId();
		int channelReporterId = channel.getReporter().getId();
		String channelContextName = channel.getContextName();
		Substitutions filterSubs = channel.getFilter().getSubstitutions();
		Substitutions switchSubs = channel.getSwitch().getSubstitutions();
		ChannelTypes channelType = getChannelType(channel);
		Hashtable<ChannelIdentifier, Channel> targetSet = channels.remove(channelType);
		ChannelIdentifier channelId = new ChannelIdentifier(channelRequesterId, channelReporterId, channelContextName,
				filterSubs, switchSubs);
		Channel removed = targetSet.remove(channelId);
		channels.put(channelType, targetSet);
		return removed;
	}

	public ChannelTypes getChannelType(Channel channel) {
		ChannelTypes channelType;
		if (channel instanceof AntecedentToRuleChannel)
			channelType = ChannelTypes.RuleAnt;
		else if (channel instanceof RuleToConsequentChannel)
			channelType = ChannelTypes.RuleCons;
		else
			channelType = ChannelTypes.MATCHED;
		return channelType;
	}

	@Override
	public Iterator<Channel> iterator() {
		/*
		 * add ruletoantecedent channels fel akher 3alashan a serve el reports el gaya
		 * menhom ba3d ma aserve el reports el tanya men channels tanya -- RuleNode
		 * proceessReport
		 */
		Collection<Channel> toBeAddedLater = new ArrayList<Channel>();
		Collection<Channel> allMergedChannels = new ArrayList<Channel>();
		Collection<Hashtable<ChannelIdentifier, Channel>> collectionOfSets = channels.values();
		for (Hashtable<ChannelIdentifier, Channel> set : collectionOfSets) {
			for (Channel channel : set.values()) {
				boolean ruleAntChannel = channel instanceof AntecedentToRuleChannel;
				if (ruleAntChannel)
					toBeAddedLater.add(channel);
				else
					allMergedChannels.add(channel);
			}
		}
		for (Channel ruleAntChannel : toBeAddedLater) {
			allMergedChannels.add(ruleAntChannel);
		}
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
		Collection<Channel> allMergedChannels = new ArrayList<Channel>();
		Collection<Hashtable<ChannelIdentifier, Channel>> collectionOfSets = channels.values();
		for (Hashtable<ChannelIdentifier, Channel> set : collectionOfSets)
			allMergedChannels.addAll(set.values());
		for (Channel channel : allMergedChannels) {
			if (channel.isRequestProcessed() == processedRequest)
				processedRequestsChannels.addChannel(channel);
		}
		return processedRequestsChannels;
	}

	public Collection<Channel> getChannels() {
		Collection<Channel> allMergedChannels = new ArrayList<Channel>();
		Collection<Hashtable<ChannelIdentifier, Channel>> collectionOfSets = channels.values();
		for (Hashtable<ChannelIdentifier, Channel> set : collectionOfSets)
			allMergedChannels.addAll(set.values());
		return allMergedChannels;
	}

	public Collection<Channel> getAntRuleChannels() {
		Hashtable<ChannelIdentifier, Channel> channelsHash = channels.get(ChannelTypes.RuleAnt);
		return channelsHash.values();
	}

	public Collection<Channel> getRuleConsChannels() {
		Hashtable<ChannelIdentifier, Channel> channelsHash = channels.get(ChannelTypes.RuleCons);
		return channelsHash.values();
	}

	public Collection<Channel> getMatchChannels() {
		Hashtable<ChannelIdentifier, Channel> channelsHash = channels.get(ChannelTypes.MATCHED);
		return channelsHash.values();
	}

	public boolean contains(Channel newChannel) {
		return getChannel(newChannel) != null;
	}

	public Channel getChannel(Channel newChannel) {
		int channelRequesterId = newChannel.getRequester().getId();
		int channelReporterId = newChannel.getReporter().getId();
		String channelContextName = newChannel.getContextName();
		Substitutions channelSubs = newChannel.getFilter().getSubstitutions();
		Substitutions switchSubs = newChannel.getSwitch().getSubstitutions();
		ChannelIdentifier channelId = new ChannelIdentifier(channelRequesterId, channelReporterId, channelContextName,
				channelSubs, switchSubs);
		ChannelTypes channelType = getChannelType(newChannel);
		Hashtable<ChannelIdentifier, Channel> set = channels.get(channelType);
		return set.get(channelId);
	}

}
