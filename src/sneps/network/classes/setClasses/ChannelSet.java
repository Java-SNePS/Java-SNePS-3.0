package sneps.network.classes.setClasses;

import java.util.Iterator;

import sneps.snip.channels.Channel;

import java.io.Serializable;
import java.util.HashSet;

public class ChannelSet implements Iterable<Channel>, Serializable {
	private HashSet<Channel> channels;

	public ChannelSet() {
		channels = new HashSet<Channel>();
	}

	@Override
	public Iterator<Channel> iterator() {
		return channels.iterator();
	}

	public void addChannel(Channel newChannel) {
		channels.add(newChannel);
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

}
