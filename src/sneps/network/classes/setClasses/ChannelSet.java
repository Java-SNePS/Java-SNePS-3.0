package sneps.network.classes.setClasses;

import java.util.Iterator;

import sneps.snip.channels.Channel;

import java.io.Serializable;
import java.util.HashSet;

/**
 * @className ChannelSet.java
 * 
 * @author Amgad Ashraf
 * 
 * @version 3.00 31/5/2018
 */
public class ChannelSet implements Iterable<Channel>, Serializable {
	private static final long serialVersionUID = -7019589602404627084L;
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
