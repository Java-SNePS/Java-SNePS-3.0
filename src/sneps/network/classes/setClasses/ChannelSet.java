package sneps.network.classes.setClasses;

import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;

import sneps.network.classes.Channel;

public class ChannelSet implements Iterable<Channel> {
	private Set<Channel> channels;

	public ChannelSet() {
		channels = new HashSet<Channel>();
	}

	@Override
	public Iterator<Channel> iterator() {
		return channels.iterator();
	}

}
