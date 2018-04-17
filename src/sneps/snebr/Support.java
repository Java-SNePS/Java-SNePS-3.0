package sneps.snebr;


import java.util.Hashtable;
import java.util.Iterator;

import sneps.exceptions.CustomException;
import sneps.exceptions.NodeNotFoundException;
import sneps.network.Network;
import sneps.network.Node;
import sneps.network.PropositionNode;
import sneps.network.classes.Semantic;
import sneps.network.classes.setClasses.PropositionSet;

public class Support {
	private Hashtable<String, PropositionSet> justificationSupport;
	private Hashtable<String, PropositionSet> assumptionBasedSupport;
	private boolean hasChildren;
	
	public Support(int id){
		assumptionBasedSupport = new Hashtable<String, PropositionSet>();
		justificationSupport = new Hashtable<String, PropositionSet>();
		assumptionBasedSupport.put(Integer.toString(id), new PropositionSet(id));
	}
	public Hashtable<String, PropositionSet> getJustificationSupport() {
		return justificationSupport;
	}
	public boolean HasChildren() {
		return hasChildren;
	}
	
	//clean
	
	//union kol el combinations bta3et el justifications bta3et el  assumptions 

	public void addJustificationBasedSupport(PropositionSet propSet) throws NodeNotFoundException {
		if(!hasChildren){
			assumptionBasedSupport = new Hashtable<String, PropositionSet>();
		}
		String hash = propSet.getHash();
		if(!justificationSupport.contains(hash)){
			justificationSupport.put(hash, propSet);
			hasChildren = true;
			int [] nodes = propSet.getProps();
			PropositionSet setSofar = new PropositionSet();
			for(int i = 0; i < nodes.length ; i++){
				try {
					PropositionNode node = (PropositionNode)Network.getNodeById(nodes[i]);
					Hashtable<String, PropositionSet> NodeAssumptions = node.getAssumptionBasedSupport();
					Iterator<PropositionSet> it = NodeAssumptions.values().iterator();
					while(it.hasNext())
						setSofar = setSofar.union(it.next());
				} catch (CustomException e) {
					throw new NodeNotFoundException("Nodes are not in the Network. 'Supports Class'");
				}
			}
			assumptionBasedSupport.put(setSofar.getHash(), setSofar);
		}
	}


//	private PropositionSet  getAssumptionBasedSupportDistructive(){//lazm yerg3 propset
//		ArrayList<Integer> props = new ArrayList<Integer>();
//		Iterator<PropositionSet> it = assumptionBasedSupport.values().iterator();
//		while(it.hasNext()){
//		PropositionSet allsupports = it.next();
//		int[] 
//		while(alls)
//		}
//		return assumptionBasedSupport;
//	}
	public Hashtable<String, PropositionSet> getAssumptionBasedSupport(){//lazm yerg3 propset
		return assumptionBasedSupport;
	}
	//toString for UI SNePSlog
	public static void main(String[] args) {
		Semantic sem = new Semantic("PropositionNode");
		Network net = new Network();
		net.buildBaseNode("a", sem);
		net.buildBaseNode("b", sem);
		net.buildBaseNode("c", sem);
		net.buildBaseNode("d", sem);
		net.buildBaseNode("e", sem);
		net.buildBaseNode("f", sem);
		net.buildBaseNode("g", sem);
		
		try {
			Node n1 = net.getNode("a");
			Node n2 = net.getNode("b");
			Node n3 = net.getNode("c");
			Node n4 = net.getNode("d");
			Node n5 = net.getNode("e");
			Node n6 = net.getNode("f");
			Node n7 = net.getNode("g");
			
			
			Iterator<PropositionSet> it = ((PropositionNode)n1).getBasicSupport().getAssumptionBasedSupport().values().iterator();
			System.out.println(it.next().getProps()[0]);
			
		} catch (CustomException e) {
			System.out.println("Not founddddd");
		}
	}
}





