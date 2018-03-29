package sneps.tests;

import static org.junit.Assert.*;

import java.util.LinkedList;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.org.apache.xpath.internal.operations.Mod;

import sneps.network.*;
import sneps.network.cables.DownCable;
import sneps.network.cables.DownCableSet;
import sneps.network.classes.*;
import sneps.network.classes.setClasses.NodeSet;
import sneps.network.classes.term.*;


public class NetworkTests extends TestCase{
	private Network network;

	@Before
	public void setUp() throws Exception {
		network = new Network();
		
		Node n1 = new Node(new Semantic("Dog"), new Base("Base"));
		Node n2 = new Node(new Semantic("Fido"), new Base("Base"));
		Node n3 = new Node();
		NodeSet nodeSet = new NodeSet();
		
		Relation member = new Relation("member", "Entity", "reduce", 1);
		member.setQuantifier();
		Relation r1 = new Relation("class", "Entity", "none", 1);
		r1.setQuantifier();
		
		LinkedList<Relation> lnkCF = new LinkedList<Relation>();
		lnkCF.add(r1);
		
		DownCable dc1 = new DownCable(r1, nodeSet);
		LinkedList<DownCable> lnkDC = new LinkedList<DownCable>();
		lnkDC.add(dc1);
		
		DownCableSet downSet = new  DownCableSet(lnkDC,	new CaseFrame("member", lnkCF));
		Molecular t1 = new Molecular("isMember", downSet);
		
		
		
		/*VariableNode var1 = Network.buildVariableNode();
		VariableNode var2 = Network.buildVariableNode();
		
		// defining a new relation with the name: member
		Relation r1 = Network
				.defineRelation("husband", "Individual", "none", 1);

		// defining a new relation with the name: class
		Relation r2 = Network.defineRelation("wife", "Individual", "none", 1);

		// building a relation case frame properties structure for relation r1
		RCFP rp1 = Network.defineRelationPropertiesForCF(r1, "none", 1);

		// building a relation case frame properties structure for relation r1
		RCFP rp2 = Network.defineRelationPropertiesForCF(r2, "none", 1);

		// creating the linked list of properties to be used in the caseFrame
		// cf1
		// to do do I need to make a method for this in Network
		LinkedList<RCFP> relProperties = new LinkedList<RCFP>();

		// adding the elements to the list
		relProperties.add(rp1);
		relProperties.add(rp2);

		// defining a new case frame
		//CaseFrame cf1 = Network.defineCaseFrame("Proposition", relProperties);

		// the relation-node pair to be used in building Molecular Node m1
*/		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		
/*
		System.out.println(Network.getNexMolName());
		Relation member = new Relation("member", "Entity", "reduce", 1);
		member.setQuantifier();
		Relation cl = new Relation("class", "Entity", "none", 1);
		cl.setQuantifier();
		VariableNode node1 = new VariableNode(new Variable("x"));
		VariableNode node2 = new VariableNode(new Variable("y"));
		Node node3 = new Node(new Semantic("Dog"), new Base("Base"));
		Node node4 = new Node(new Semantic("Cat"), new Base("Base"));
		VariableNode node5 = new VariableNode(new Variable("z"));
		NodeSet ns = new NodeSet();
		ns.addNode(node1);
		ns.addNode(node2);
		NodeSet ns1 = new NodeSet();
		ns1.addNode(node3);
		DownCable dc = new DownCable(member, ns);
		DownCable dc1 = new DownCable(cl, ns1);
		LinkedList<DownCable> dList = new LinkedList<DownCable>();
		dList.add(dc);
		dList.add(dc1);
		RCFP prop = new RCFP(member, "none", 2);
		RCFP prop2 = new RCFP(cl, "none", 2);
		LinkedList<RCFP> propList = new LinkedList<RCFP>();
		propList.add(prop);
		propList.add(prop2);
		//CaseFrame cf = new CaseFrame("Individual", propList);
		//DownCableSet dcs = new DownCableSet(dList, cf);
		Object[][] relNode = new Object[4][2];
		relNode[0][0] = member;
		relNode[0][1] = node1;
		relNode[1][0] = member;
		relNode[1][1] = node2;
		relNode[2][0] = cl;
		relNode[2][1] = node3;
		relNode[3][0] = cl;
		relNode[3][1] = node4;
		//System.out.println("checking" + dcs.size());
		//Open p = new Open("M1", dcs);
		//Entity e = new Entity();
		Node pNode = new Node(p);
		Set<Node> nodes;
		nodes.put(pNode.getIdentifier(), pNode);
		Set<CaseFrame> caseFrames;
		caseFrames.put(cf.getId(), cf);
		Set<Node> molecularNodes;
		molecularNodes.put(cf.getId(), new NodeSet());
		molecularNodes.get(cf.getId()).addNode(pNode);
		System.out.println("start");
		Node m = Network.buildMolecularNode(relNode, cf);
		System.out.println(m.getSyntacticType());
		System.out.println(m.getSemanticType());
*/
		
		
		fail("Not yet implemented");
	}

}
