package sneps.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import sneps.network.Node;
import sneps.network.VariableNode;
import sneps.snip.matching.Binding;

public class SNIPTests {
	private Binding binding; 

	@Test
	public void test() {
		//Binding class tests
		Node n1 = new Node();
		VariableNode vn1 = new VariableNode();
		binding = new Binding(vn1, n1);
		
		Node n2 = new Node();
		VariableNode vn2 = new VariableNode();
		Binding binding1 = new Binding(vn1, n1);
		//IsEqual tests
		assertTrue("First Binding is not equal to binding1", binding.isEqual(binding1));
		
		binding1.setNode(n2);
		binding1.setVariable(vn2);
		
		assertFalse("Second Binding is not equal to binding1", binding.isEqual(binding1));
		//Clone Tests
		binding1 = binding.clone();
		assertTrue("Clone binding1 is not equal to binding", binding.isEqual(binding1));
		
		//fail("Not yet implemented");
	}

}
