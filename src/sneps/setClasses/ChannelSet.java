package sneps.setClasses;

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

}
