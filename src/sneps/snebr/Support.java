package sneps.snebr;

import java.io.Serializable;
import java.util.ArrayList;
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
import sneps.exceptions.IllegalIdentifierException;
import java.util.Stack;

import sneps.exceptions.NodeNotFoundInNetworkException;
import sneps.exceptions.NodeNotFoundInPropSetException;
import sneps.exceptions.NotAPropositionNodeException;
import sneps.network.Network;
import sneps.network.PropositionNode;
import sneps.network.classes.Semantic;
import sneps.network.classes.setClasses.PropositionSet;

public class Support implements Serializable{
	private int id;
	private Hashtable<String, PropositionSet> justificationSupport;
	private Hashtable<String, PropositionSet> assumptionBasedSupport;
	private ArrayList<ArrayList<Integer>> mySupportsTree;
	private boolean hasChildren;
	private boolean TreeComputed;

	public Support(int id) throws NotAPropositionNodeException, NodeNotFoundInNetworkException {
		this.id = id;
		assumptionBasedSupport = new Hashtable<String, PropositionSet>();
		justificationSupport = new Hashtable<String, PropositionSet>();
		mySupportsTree = new ArrayList<ArrayList<Integer>>();
		PropositionSet intialSet = new PropositionSet(id);
		assumptionBasedSupport.put(Integer.toString(id), intialSet);
		ArrayList<Integer> intialTree = new ArrayList<Integer>();
		intialTree.add(id);
		mySupportsTree.add(intialTree);
		TreeComputed = true;
	}

	@Override
	public String toString() {
		return "Support [justificationSupport=" + justificationSupport.values() + ", assumptionBasedSupport="
				+ assumptionBasedSupport.values() + "]";
	}

	public Hashtable<String, PropositionSet> getJustificationSupport() {
		return justificationSupport;
	}

	private boolean HasChildren() {
		return hasChildren;
	}

	private void setHasChildren(boolean flag) {
		hasChildren = flag;
	}

	private boolean isTreeComputed() {
		return TreeComputed;
	}

	private void setTreeComputed(boolean flag) {
		TreeComputed = flag;
	}

	public void addJustificationBasedSupport(PropositionSet propSet)
			throws NodeNotFoundInPropSetException, NotAPropositionNodeException, NodeNotFoundInNetworkException {
		if (!HasChildren()) {
			assumptionBasedSupport = new Hashtable<String, PropositionSet>();
		}
		String hash = propSet.getHash();
		if (!justificationSupport.containsKey(hash)) {
			justificationSupport.put(hash, propSet);
			setHasChildren(true);
			setTreeComputed(false);
			int[] nodes = PropositionSet.getPropsSafely(propSet);
			PropositionSet setSofar = new PropositionSet();
			int idx = 1;
			PropositionNode node = (PropositionNode) Network.getNodeById(nodes[0]);
			if(node.getId() == this.getId()){
				node = (PropositionNode) Network.getNodeById(nodes[1]);
				idx++;
			}
			Hashtable<String, PropositionSet> NodeAssumptions = node.getAssumptionBasedSupport();
			Iterator<PropositionSet> it = NodeAssumptions.values().iterator();

			while (it.hasNext()) {
				setSofar = it.next();
				computeAssumptionRec(nodes, setSofar, idx);
			}
		}
	}

	private void computeAssumptionRec(int[] nodes, PropositionSet setSofar, int idx)
			throws NodeNotFoundInNetworkException, NotAPropositionNodeException {
		if (idx == nodes.length) {
			assumptionBasedSupport.put(setSofar.getHash(), setSofar);
			return;
		}
		PropositionNode node = (PropositionNode) Network.getNodeById(nodes[idx]);
		if(node.getId() == this.getId()){
			return;
		}
		Hashtable<String, PropositionSet> NodeAssumptions = node.getAssumptionBasedSupport();
		Iterator<PropositionSet> it = NodeAssumptions.values().iterator();
		idx += 1;
		while (it.hasNext()) {
			computeAssumptionRec(nodes, setSofar.union(it.next()), idx);
		}

	}

	public void lazyEvaluationTree() {

	}
	
	public ArrayList<ArrayList<Integer>> getMySupportsTree() throws NotAPropositionNodeException, NodeNotFoundInNetworkException{
		if(isTreeComputed())
			return mySupportsTree;
		
		mySupportsTree = new ArrayList<ArrayList<Integer>>();
		Iterator<PropositionSet> it = justificationSupport.values().iterator();
		ArrayList<Integer> setSoFar = new ArrayList<Integer>();
		setSoFar.add(getId());
		while(it.hasNext()){
			PropositionSet propSet = it.next();
			int[] nodes = PropositionSet.getPropsSafely(propSet);
			for(int i = 0; i < nodes.length ; i++){
				 computeTreeRec(setSoFar, (PropositionNode) Network.getNodeById(nodes[i]));
				 setSoFar = new ArrayList<Integer>();
				 setSoFar.add(getId());
			}
		}
		
		mySupportsTree = reverse(mySupportsTree);
		setTreeComputed(true);
		return mySupportsTree;
	}
	
	private ArrayList<ArrayList<Integer>> reverse(ArrayList<ArrayList<Integer>> mySupportsTree) {
		Stack<Integer> tmp = new Stack<Integer>();
		ArrayList<ArrayList<Integer>> returnedList = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> smallList = new ArrayList<Integer>();
		for(int i = 0; i < mySupportsTree.size(); i++){
			ArrayList<Integer> inst = mySupportsTree.get(i);
			for (int j = 0; j < inst.size(); j++) {
				tmp.push(inst.get(j));
			}
			for (int j = 0; j < inst.size(); j++) {
				smallList.add(tmp.pop());
			}
			returnedList.add(smallList);
			smallList = new ArrayList<Integer>();
		}
		return returnedList;
	}

	private ArrayList<Integer> computeTreeRec(ArrayList<Integer> setSoFar, PropositionNode node) throws NotAPropositionNodeException, NodeNotFoundInNetworkException {
			if(!node.getBasicSupport().HasChildren()){
				ArrayList<Integer> temp = new ArrayList<Integer>();
				temp.addAll(setSoFar);
				setSoFar.add(node.getId());	
				mySupportsTree.add(setSoFar);
				return temp;
			}
			if(setSoFar.contains(node.getId())){
				return setSoFar;
			}
			ArrayList<Integer> temp = new ArrayList<Integer>();
			temp.addAll(setSoFar);
			setSoFar.add(node.getId());	
			Iterator<PropositionSet> it = node.getJustificationSupport().values().iterator();
			while(it.hasNext()){
				PropositionSet propSet = it.next();
				int[] nodes = PropositionSet.getPropsSafely(propSet);
				for(int i = 0; i < nodes.length ; i++){
					setSoFar = computeTreeRec(setSoFar, (PropositionNode) Network.getNodeById(nodes[i]));
					
				}
			}
			
			return temp;
	}

	public Hashtable<String, PropositionSet> getAssumptionBasedSupport() {
		return assumptionBasedSupport;
	}

	public boolean removeNodeFromSupports(PropositionNode propNode) {
		// TODO Auto-generated method stub
		return false;
	}

	public int getId() {
		return id;
	}

	public static void main(String[] args)
			throws NotAPropositionNodeException, NodeNotFoundInPropSetException, NodeNotFoundInNetworkException, IllegalIdentifierException {
		Semantic sem = new Semantic("PropositionNode");
		Network net = new Network();
		net.buildBaseNode("s", sem);// 0
		net.buildBaseNode("p", sem);// 1
		net.buildBaseNode("q", sem);// 2
		net.buildBaseNode("r", sem);// 3
		net.buildBaseNode("m", sem);// 4
		net.buildBaseNode("n", sem);// 5
		net.buildBaseNode("v", sem);// 6
		net.buildBaseNode("z", sem);// 7
		net.buildBaseNode("a", sem);// 8
		net.buildBaseNode("b", sem);// 9
		net.buildBaseNode("c", sem);// 10
		net.buildBaseNode("d", sem);// 11
		net.buildBaseNode("e", sem);// 12
		net.buildBaseNode("f", sem);// 13
		net.buildBaseNode("g", sem);// 14
		net.buildBaseNode("h", sem);// 15
		net.buildBaseNode("i", sem);// 16
		net.buildBaseNode("j", sem);// 17
		net.buildBaseNode("k", sem);// 18
		net.buildBaseNode("l", sem);// 19
		net.buildBaseNode("o", sem);// 20
		net.buildBaseNode("t", sem);// 21

		PropositionNode n0 = (PropositionNode) net.getNode("s");
		PropositionNode n1 = (PropositionNode) net.getNode("p");
		PropositionNode n2 = (PropositionNode) net.getNode("q");
		PropositionNode n3 = (PropositionNode) net.getNode("r");
		PropositionNode n4 = (PropositionNode) net.getNode("m");
		PropositionNode n5 = (PropositionNode) net.getNode("n");
		PropositionNode n6 = (PropositionNode) net.getNode("v");
		PropositionNode n7 = (PropositionNode) net.getNode("z");
		PropositionNode n8 = (PropositionNode) net.getNode("a");
		PropositionNode n9 = (PropositionNode) net.getNode("b");
		PropositionNode n10 = (PropositionNode) net.getNode("c");
		PropositionNode n11 = (PropositionNode) net.getNode("d");
		PropositionNode n12 = (PropositionNode) net.getNode("e");
		PropositionNode n13 = (PropositionNode) net.getNode("f");
		PropositionNode n14 = (PropositionNode) net.getNode("g");
		PropositionNode n15 = (PropositionNode) net.getNode("h");
		PropositionNode n16 = (PropositionNode) net.getNode("i");
		PropositionNode n17 = (PropositionNode) net.getNode("j");
		PropositionNode n18 = (PropositionNode) net.getNode("k");
		PropositionNode n19 = (PropositionNode) net.getNode("l");
		PropositionNode n20 = (PropositionNode) net.getNode("o");
		PropositionNode n21 = (PropositionNode) net.getNode("t");

		int[] pqr = new int[4];
		pqr[0] = 1;
		pqr[1] = 2;
		pqr[2] = 3;
		pqr[3] = 0;
		

		int[] mn = new int[2];
		mn[0] = 5;
		mn[1] = 4;

		int[] vz = new int[3];
		vz[0] = 6;
		vz[1] = 7;
		vz[2] = 0;

		int[] ab = new int[2];
		ab[0] = 9;
		ab[1] = 8;

		int[] cd = new int[2];
		cd[0] = 10;
		cd[1] = 11;

		int[] ef = new int[2];
		ef[0] = 12;
		ef[1] = 13;

		int[] gh = new int[2];
		gh[0] = 14;
		gh[1] = 15;

		int[] ij = new int[2];
		ij[0] = 16;
		ij[1] = 17;

		int[] kl = new int[3];
		kl[0] = 18;
		kl[1] = 19;
		kl[2] = 5;

		int[] ot = new int[2];
		ot[0] = 20;
		ot[1] = 21;

		PropositionSet s1 = new PropositionSet(pqr);
		PropositionSet s2 = new PropositionSet(mn);
		PropositionSet s3 = new PropositionSet(vz);
		PropositionSet s4 = new PropositionSet(ab);
		PropositionSet s5 = new PropositionSet(cd);
		PropositionSet s6 = new PropositionSet(ef);
		PropositionSet s7 = new PropositionSet(gh);
		PropositionSet s8 = new PropositionSet(ij);
		PropositionSet s9 = new PropositionSet(kl);
		PropositionSet s10 = new PropositionSet(ot);

		n4.addJustificationBasedSupport(s7);
		n4.addJustificationBasedSupport(s8);
		n5.addJustificationBasedSupport(s9);
		n1.addJustificationBasedSupport(s2);
		n1.addJustificationBasedSupport(s3);
		n2.addJustificationBasedSupport(s4);
		n3.addJustificationBasedSupport(s5);
		n3.addJustificationBasedSupport(s6);
		n0.addJustificationBasedSupport(s1);
		n0.addJustificationBasedSupport(s10);
		
		//Hashtable<String, PropositionSet> justificationSupport = n0.getAssumptionBasedSupport();
		ArrayList<ArrayList<Integer>> tree = n0.getBasicSupport().getMySupportsTree();
		
		
			System.out.println(tree.toString());
		
		

	}

}
