package sneps.network.classes.term;

import sneps.network.cables.DownCableSet;

public class Molecular extends Term {
	protected DownCableSet downCableSet;

	/**
	 * The constructor of this class.
	 * 
	 * @param identifier
	 * 			the name of the node that will be created.
	 * @param downCableSet
	 * 			the down cable set of the node  that will be
	 * 			created.
	 */
	public Molecular(String identifier, DownCableSet downCableSet){
		super(identifier);
		this.downCableSet = downCableSet;
	}

	public Molecular(String closedName, DownCableSet dCableSet) {
		super(closedName);
		downCableSet = dCableSet;
	}

	public DownCableSet getDownCableSet() {
		return this.getDownCableSet();
	}
}
