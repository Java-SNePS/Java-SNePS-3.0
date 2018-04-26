package sneps.snebr;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javafx.beans.InvalidationListener;
import javafx.beans.property.MapProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import sneps.exceptions.CustomException;
import sneps.exceptions.NodeNotFoundInNetworkException;
import sneps.exceptions.NodeNotFoundInPropSetException;
import sneps.exceptions.NotAPropositionNodeException;
import sneps.network.Network;
import sneps.network.Node;
import sneps.network.PropositionNode;
import sneps.network.classes.Semantic;
import sneps.network.classes.setClasses.PropositionSet;

public class Support {
	private Hashtable<String, PropositionSet> justificationSupport;
	private Hashtable<String, PropositionSet> assumptionBasedSupport;
	private boolean hasChildren;

	public Support(int id) throws NotAPropositionNodeException, NodeNotFoundInNetworkException{
		assumptionBasedSupport = new Hashtable<String, PropositionSet>();
		justificationSupport = new Hashtable<String, PropositionSet>();
		assumptionBasedSupport.put(Integer.toString(id), new PropositionSet(id));
	}
	@Override
	public String toString() {
		return "Support [justificationSupport=" + justificationSupport + ", assumptionBasedSupport="
				+ assumptionBasedSupport + "]";
	}
	public Hashtable<String, PropositionSet> getJustificationSupport() {
		return justificationSupport;
	}
	public boolean HasChildren() {
		return hasChildren;
	}

	//clean

	//union kol el combinations bta3et el justifications bta3et el  assumptions

	public void addJustificationBasedSupport(PropositionSet propSet) throws NodeNotFoundInPropSetException, NotAPropositionNodeException, NodeNotFoundInNetworkException {
		if(!hasChildren){
			assumptionBasedSupport = new Hashtable<String, PropositionSet>();
		}
		String hash = propSet.getHash();
		if(!justificationSupport.containsKey(hash)){
			justificationSupport.put(hash, propSet);
			hasChildren = true;
			int [] nodes = PropositionSet.getPropsSafely(propSet);
			PropositionSet setSofar = new PropositionSet();
			for(int i = 0; i < nodes.length ; i++){
				try {
					PropositionNode node = (PropositionNode)Network.getNodeById(nodes[i]);
					Hashtable<String, PropositionSet> NodeAssumptions = node.getAssumptionBasedSupport();
					Iterator<PropositionSet> it = NodeAssumptions.values().iterator();
					while(it.hasNext())
						setSofar = setSofar.union(it.next());
				} catch (CustomException e) {
					throw new NodeNotFoundInPropSetException("Nodes are not in the Network. 'Supports Class'");
				}
			}
			assumptionBasedSupport.put(setSofar.getHash(), setSofar);
		}
	}

	public void lazyEvaluationTree(){
		
	}

	public Hashtable<String, PropositionSet> getAssumptionBasedSupport(){
		return assumptionBasedSupport;
	}
	//toString for UI SNePSlog
	public static void main(String[] args) throws NotAPropositionNodeException, NodeNotFoundInPropSetException, NodeNotFoundInNetworkException {
		Semantic sem = new Semantic("PropositionNode");
		Network net = new Network();
		net.buildBaseNode("a", sem);
		net.buildBaseNode("b", sem);
		net.buildBaseNode("c", sem);
		net.buildBaseNode("d", sem);
		net.buildBaseNode("e", sem);
		net.buildBaseNode("f", sem);
		net.buildBaseNode("g", sem);
		net.buildBaseNode("h", sem);
		net.buildBaseNode("i", sem);

		try {
			Node n0 = net.getNode("a");
			Node n1 = net.getNode("b");
			Node n2 = net.getNode("c");
			Node n3 = net.getNode("d");
			Node n4 = net.getNode("e");
			Node n5 = net.getNode("f");
			Node n6 = net.getNode("g");
			Node n7 = net.getNode("h");
			Node n8 = net.getNode("i");
			
			PropositionNode p3 = ((PropositionNode)n3);
			PropositionNode p1 = ((PropositionNode)n1);
			
			int[] props = new int[3];
			props[0] = 2;
			props[1] = 6;
			props[2] = 1;
			
			int[] props2 = new int[2];
			props2[0] = 0;
			props2[1] = 4;
			
			int[] props3 = new int[2];
			props3[0] = 8;
			props3[1] = 7;
			
			PropositionSet s1 = new PropositionSet(props);
			PropositionSet s2 = new PropositionSet(props2);
			PropositionSet s3 = new PropositionSet(props3);
			
			p1.getBasicSupport().addJustificationBasedSupport(s2);
			p3.getBasicSupport().addJustificationBasedSupport(s1);
			p3.getBasicSupport().addJustificationBasedSupport(s3);
			
			
//			Iterator<PropositionSet> it2 = (p3.getAssumptionBasedSupport()).values().iterator();
//			while(it2.hasNext()){
//				System.out.println(it2.next().toString());
//			}
			
			System.out.println(p3.getBasicSupport().toString());

		} catch (Exception e) {
			System.out.println("Not founddddd");
		}
	}
}





