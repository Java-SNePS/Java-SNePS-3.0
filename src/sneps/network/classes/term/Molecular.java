package sneps.network.classes.term;

import sneps.network.cables.DownCableSet;

public class Molecular extends Term {
	protected DownCableSet downCableSet;

	public Molecular(String idenitifier) {
		super(idenitifier);
	}

	public Molecular(String closedName, DownCableSet dCableSet) {
		super(closedName);
		downCableSet = dCableSet;
	}

	public DownCableSet getDownCableSet() {
		return this.getDownCableSet();
	}
}
