import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import sneps.exceptions.NodeNotFoundInNetworkException;
import sneps.exceptions.NotAPropositionNodeException;
import sneps.network.Node;
import sneps.network.PropositionNode;
import sneps.network.VariableNode;
import sneps.network.classes.term.Base;
import sneps.network.classes.term.Variable;
import sneps.network.classes.setClasses.PropositionSet;
import sneps.network.classes.setClasses.RuleUseInfoSet;
import sneps.network.classes.setClasses.VarNodeSet;
import sneps.network.classes.setClasses.FlagNodeSet;
import sneps.snip.InferenceTypes;
import sneps.snip.classes.FlagNode;
import sneps.snip.classes.RuleUseInfo;
import sneps.snip.classes.SIndex;
import sneps.snip.matching.Binding;
import sneps.snip.matching.LinearSubstitutions;


public class SIndexTest {

	@Test
	public void newSIndex() throws NotAPropositionNodeException, NodeNotFoundInNetworkException {
		
		/**
		 * Creating Nodes
		 */
		
		Base ba = new Base("A");
		Base ba1 = new Base("B");
		Base ba2 = new Base("C");
		
		Node n = new Node(ba);
		Node n1 = new Node(ba1);
		Node n2 = new Node(ba2);
		Node n3 = new Node();
		Node n4 = new Node();
		Node n5 = new Node();
		Node n6 = new Node();
		Node n7 = new Node();

		
		LinearSubstitutions ls = new LinearSubstitutions();
		LinearSubstitutions ls1 = new LinearSubstitutions();
		LinearSubstitutions ls2 = new LinearSubstitutions();
		LinearSubstitutions ls3 = new LinearSubstitutions();
		LinearSubstitutions ls4 = new LinearSubstitutions();
		LinearSubstitutions ls5 = new LinearSubstitutions();
		
		PropositionNode pn = new PropositionNode();
		PropositionNode pn2 = new PropositionNode();
		PropositionNode pn3 = new PropositionNode();
		
		PropositionSet ps = new PropositionSet();
		
		Variable v = new Variable("x");
		Variable v1 = new Variable("y");
		Variable v2 = new Variable("z");
		Variable v3 = new Variable("a");
		Variable v4 = new Variable("b");
		Variable v5 = new Variable("c");
		Variable v6 = new Variable("d");
		Variable v7 = new Variable("e");
		
		VariableNode vn = new VariableNode(v);
		VariableNode vn1 = new VariableNode(v1);
		VariableNode vn2 = new VariableNode(v2);
		VariableNode vn3 = new VariableNode(v3);
		VariableNode vn4 = new VariableNode(v4);
		VariableNode vn5 = new VariableNode(v5);
		VariableNode vn6 = new VariableNode(v6);
		VariableNode vn7 = new VariableNode(v7);
		
		VarNodeSet vns = new VarNodeSet();
		VarNodeSet vns1 = new VarNodeSet();
		VarNodeSet vns2 = new VarNodeSet();
		VarNodeSet vns3 = new VarNodeSet();
		VarNodeSet vns4 = new VarNodeSet();
		VarNodeSet vns5 = new VarNodeSet();
		VarNodeSet vns6 = new VarNodeSet();
		VarNodeSet vns7 = new VarNodeSet();
		
		
		vns.addVarNode(vn);
		vns1.addVarNode(vn1);
		vns2.addVarNode(vn2);
		vns3.addVarNode(vn3);
		vns4.addVarNode(vn4);
		vns5.addVarNode(vn5);
		vns6.addVarNode(vn6);
		vns7.addVarNode(vn7);
		
		//RUI set SIndex
		SIndex index = new SIndex((byte) 0, vns);
		
		//Singleton SIndex
		SIndex index1 = new SIndex((byte) 1, vns);
		 
		Binding b = new Binding(vn, n);
		Binding b1 = new Binding(vn1, n1);
		Binding b2 = new Binding(vn2, n);
		Binding b3 = new Binding(vn3, n);
		Binding b4 = new Binding(vn4, n);
		Binding b5 = new Binding(vn5, n1);
		Binding b6 = new Binding(vn6, n);
		Binding b7 = new Binding(vn7, n);
		Binding b8 = new Binding(vn, n1);
		Binding b9 = new Binding(vn, n2);
		
		ls.putIn(b);
		ls1.putIn(b1);
		ls4.putIn(b8);
		ls5.putIn(b9);
		
		FlagNode fn = new FlagNode(n, ps, 1);
		FlagNode fn1 = new FlagNode(n1, ps, 1);
		FlagNode fn2 = new FlagNode(n2, ps, 1);
		FlagNode fn3 = new FlagNode(n3, ps, 1);
		FlagNode fn4 = new FlagNode(n4, ps, 1);
		FlagNode fn5 = new FlagNode(n5, ps, 1);
		FlagNode fn6 = new FlagNode(n6, ps, 1);
		FlagNode fn7 = new FlagNode(n7, ps, 1);
		
		FlagNodeSet fns = new FlagNodeSet();
		FlagNodeSet fns1 = new FlagNodeSet();
		FlagNodeSet fns2 = new FlagNodeSet();
		FlagNodeSet fns3 = new FlagNodeSet();
		FlagNodeSet fns4 = new FlagNodeSet();
		FlagNodeSet fns5 = new FlagNodeSet();
		FlagNodeSet fns6 = new FlagNodeSet();
		FlagNodeSet fns7 = new FlagNodeSet();
		
		
		fns.insert(fn);
		fns1.insert(fn1);
		fns2.insert(fn1);
		fns3.insert(fn1);
		fns4.insert(fn1);
		fns5.insert(fn1);
		fns6.insert(fn1);
		fns7.insert(fn1);
	
		
		/**
		 * Creating new Ruis and inserting them in the map
		 */
		
		RuleUseInfo rui = new RuleUseInfo(ls, 1, 0, fns, InferenceTypes.BACKWARD);
		index.insertRUI(rui);
		index1.insertRUI(rui);
		
		
		RuleUseInfo rui1 = new RuleUseInfo(ls1, 1, 0, fns1, InferenceTypes.BACKWARD);
		
		
		RuleUseInfo rui2 = new RuleUseInfo(ls1, 1, 0, fns2, InferenceTypes.BACKWARD);
		
		
		RuleUseInfo rui3 = new RuleUseInfo(ls1, 1, 0, fns2, InferenceTypes.BACKWARD);
		
		
		RuleUseInfo rui4 = new RuleUseInfo(ls3, 0, 1, fns3, InferenceTypes.BACKWARD);
		
		
		RuleUseInfo rui5 = new RuleUseInfo(ls2, 2, 0, fns4, InferenceTypes.BACKWARD);
		index.insertRUI(rui5);
		index1.insertRUI(rui5);
		
		
		RuleUseInfo rui6 = new RuleUseInfo(ls, 3, 0, fns5, InferenceTypes.BACKWARD);
		index.insertRUI(rui6);
		index1.insertRUI(rui6);
		
		RuleUseInfo rui7 = new RuleUseInfo(ls4, 4, 0, fns5, InferenceTypes.BACKWARD);
		index.insertRUI(rui7);
		index1.insertRUI(rui7);
		
		RuleUseInfo rui8 = new RuleUseInfo(ls5, 5, 0, fns5, InferenceTypes.BACKWARD);
		index.insertRUI(rui8);
		index1.insertRUI(rui8);
		
		
		ArrayList<Integer> arr = new ArrayList<Integer>();
		arr.add(0);
		ArrayList<Integer> arr1 = new ArrayList<Integer>();
		arr1.add(1);
		ArrayList<Integer> arr2 = new ArrayList<Integer>();
		arr2.add(2);
		
		
		assertEquals(3, index.getSize());
		assertEquals(3, ((RuleUseInfoSet) (index.map.get(arr))).size());
		assertEquals(1, ((RuleUseInfoSet) (index.map.get(arr1))).size());
		assertEquals(1, ((RuleUseInfoSet) (index.map.get(arr2))).size());
		
		assertEquals(3, index1.getSize());
		assertEquals(1, ((RuleUseInfoSet) (index1.map.get(arr))).size());
	}


}
