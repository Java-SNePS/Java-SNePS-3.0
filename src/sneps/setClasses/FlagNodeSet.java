package sneps.setClasses;

import java.util.HashSet;
import java.util.Iterator;

import sneps.snip.classes.FlagNode;

public class FlagNodeSet implements Iterable<FlagNode> {
	private HashSet<FlagNode> flagNodes;

	@Override
	public Iterator<FlagNode> iterator() {
		return flagNodes.iterator();
	}

	public void putIn(FlagNode fn) {
		flagNodes.add(fn);
	}

}
