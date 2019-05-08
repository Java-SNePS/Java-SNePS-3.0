package sneps.network.classes.setClasses;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import sneps.snip.channels.Channel;

public class ChannelSet implements Iterable<Channel> {
	private Set<Channel> channels;

	public ChannelSet() {
		channels = new HashSet<Channel>();
	}

	public void addChannel(Channel channel) {
		channels.add(channel);
	}

	@Override
	public Iterator<Channel> iterator() {
		return channels.iterator();
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
		for (Channel channel : channels) {
			if (channel.isRequestProcessed() == processedRequest)
				processedRequestsChannels.addChannel(channel);
		}
		return processedRequestsChannels;
	}

	public Set<Channel> getChannels() {
		return channels;
	}
}
