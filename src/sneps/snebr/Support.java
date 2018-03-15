package sneps.snebr;

import java.util.Hashtable;

import sneps.network.classes.setClasses.PropositionSet;

public class Support {
	
	private Hashtable<String, PropositionSet> assumptionBasedSupport;
	
	public Support() {
	}

	public void addAssumptionBasedSupport(PropositionSet propSet) {
		String hash = this.hashFunc(propSet);
		if(!assumptionBasedSupport.containsKey(hash))
			assumptionBasedSupport.put(hash, propSet);
	}
	
	public String hashFunc(PropositionSet propSet){
		String hash = null;
		
		while(propSet.iterator().hasNext())
			hash += propSet.iterator().next().getId() + ", ";
		
		return hash;
		
	}

	public Hashtable<String, PropositionSet> getAssumptionBasedSupport() {
		return assumptionBasedSupport;
	}
	


}
