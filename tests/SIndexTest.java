import org.junit.Test;
import static org.junit.Assert.assertEquals;

import sneps.network.Node;
import sneps.network.VariableNode;
import sneps.network.classes.term.Variable;
import sneps.setClasses.NodeSet;
import sneps.setClasses.VarNodeSet;
import sneps.setClasses.FlagNodeSet;
import sneps.snip.classes.RuleUseInfo;
import sneps.snip.classes.SIndex;
import sneps.snip.matching.Binding;
import sneps.snip.matching.LinearSubstitutions;


public class SIndexTest {

	@Test
	public void newSIndex() {
		NodeSet ns= new NodeSet();
		VarNodeSet vns = new VarNodeSet();
		VarNodeSet vns2 = new VarNodeSet();
		SIndex index = new SIndex("Hello1",vns,(byte) 1);
		SIndex index2 = new SIndex("Hello2",vns2,(byte) 1);
		LinearSubstitutions ls = new LinearSubstitutions();
		LinearSubstitutions ls1 = new LinearSubstitutions();
		LinearSubstitutions ls2 = new LinearSubstitutions();
		LinearSubstitutions ls3 = new LinearSubstitutions();
		Node n = new Node();
		Node n1 = new Node();
		
		Variable v = new Variable("Lol1");
		Variable v1 = new Variable("Lol2");
		Variable v2 = new Variable("Hel");
		Variable v3 = new Variable("Hell");
		Variable v4 = new Variable("KKK");
		Variable v5 = new Variable("Hosso");
		Variable v6 = new Variable("Buldo");
		Variable v7 = new Variable("ATA");
		
		VariableNode vn = new VariableNode(v);
		VariableNode vn1 = new VariableNode(v1);
		VariableNode vn2 = new VariableNode(v2);
		VariableNode vn3 = new VariableNode(v3);
		VariableNode vn4 = new VariableNode(v4);
		VariableNode vn5 = new VariableNode(v5);
		VariableNode vn6 = new VariableNode(v6);
		VariableNode vn7 = new VariableNode(v7);
		
		Binding b = new Binding(vn, n);
		Binding b1 = new Binding(vn1, n1);
		Binding b2 = new Binding(vn2, n);
		Binding b3 = new Binding(vn3,n);
		Binding b4 = new Binding(vn4, n);
		Binding b5 = new Binding(vn5, n1);
		Binding b6 = new Binding(vn6, n);
		Binding b7 = new Binding(vn7,n);
		
		FlagNodeSet fns = new FlagNodeSet();
		FlagNodeSet fns1 = new FlagNodeSet();
		FlagNodeSet fns2 = new FlagNodeSet();
		FlagNodeSet fns3 = new FlagNodeSet();
		FlagNodeSet fns4 = new FlagNodeSet();
		FlagNodeSet fns5 = new FlagNodeSet();
		FlagNodeSet fns6 = new FlagNodeSet();
		FlagNodeSet fns7 = new FlagNodeSet();
		
		ls.putIn(b);
		ls1.putIn(b1);
		ls2.putIn(b2);
		ls3.putIn(b);
		ls.putIn(b3);
		
		/**
		 * Creating new Ruis and inserting them in the map
		 */
		
		RuleUseInfo rui = new RuleUseInfo(ls, 1, 0, fns);
		index.insertRUI(rui);
		
		
		RuleUseInfo rui1 = new RuleUseInfo(ls1, 1, 0, fns1);
		index.insertRUI(rui1);
		
		
		RuleUseInfo rui2 = new RuleUseInfo(ls1, 1, 0, fns2);
		index.insertRUI(rui2);
		
		
		RuleUseInfo rui3 = new RuleUseInfo(ls1, 1, 0, fns2);
		index.insertRUI(rui3);
		
		
		RuleUseInfo rui4 = new RuleUseInfo(ls3, 0, 1, fns3);
		index.insertRUI(rui4);
		
		
		RuleUseInfo rui5 = new RuleUseInfo(ls2, 2, 0, fns4);
		index.insertRUI(rui5);
		
		RuleUseInfo rui6 = new RuleUseInfo(ls, 2, 0, fns4);
		index2.insertRUI(rui6);
		
		assertEquals(1, index.getSize());
		assertEquals(1,index2.getSize());
	}
	
	
	@Test
	public void newSIndex2() {
		NodeSet ns= new NodeSet();
		VarNodeSet vns = new VarNodeSet();
		SIndex index = new SIndex("Hello1",vns,(byte) 1);
		LinearSubstitutions ls = new LinearSubstitutions();
		LinearSubstitutions ls1 = new LinearSubstitutions();
		LinearSubstitutions ls2 = new LinearSubstitutions();
		LinearSubstitutions ls3 = new LinearSubstitutions();
		Node n = new Node();
		Node n1 = new Node();
		
		Variable v = new Variable("Lol1");
		Variable v1 = new Variable("Lol2");
		Variable v2 = new Variable("Hel");
		Variable v3 = new Variable("Hell");
		
		VariableNode vn = new VariableNode(v);
		VariableNode vn1 = new VariableNode(v1);
		VariableNode vn2 = new VariableNode(v2);
		VariableNode vn3 = new VariableNode(v3);
		
		Binding b = new Binding(vn, n);
		Binding b1 = new Binding(vn1, n1);
		Binding b2 = new Binding(vn2, n);
		Binding b3 = new Binding(vn3,n);
		
		FlagNodeSet fns = new FlagNodeSet();
		
		ls.putIn(b);
		ls1.putIn(b1);
		ls2.putIn(b2);
		ls3.putIn(b);
		ls.putIn(b3);
		
		/**
		 * Creating new Ruis and inserting them in the map
		 */
		
		RuleUseInfo rui = new RuleUseInfo(ls, 1, 0, fns);
		index.insertRUI(rui);
		
		assertEquals(1, index.getSize());
	}
}
