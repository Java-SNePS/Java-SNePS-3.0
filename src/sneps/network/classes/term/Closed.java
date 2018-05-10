package sneps.network.classes.term;

import java.io.Serializable;

import sneps.network.cables.DownCableSet;

public class Closed extends Molecular implements Serializable{

	/**
	 * The constructor of this class.
	 * 
	 * @param identifier
	 * 			the name of the node that will be created.
	 * @param downCableSet
	 * 			the down cable set of the node that will be
	 * 			created.
	 */
	public Closed(String identifier, DownCableSet downCableSet){
		super(identifier, downCableSet);
	}
  
}
