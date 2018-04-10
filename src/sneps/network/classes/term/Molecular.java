package sneps.network.classes.term;

import java.io.Serializable;

import sneps.network.cables.DownCableSet;

public class Molecular extends Term implements Serializable{
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

	public DownCableSet getDownCableSet() {
		return this.downCableSet;
	}
}
