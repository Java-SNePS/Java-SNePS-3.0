package tests;

import org.junit.*;

import sneps.exceptions.*;
import sneps.network.Network;
import sneps.network.Node;
import sneps.network.PropositionNode;
import sneps.network.cables.DownCable;
import sneps.network.cables.DownCableSet;
import sneps.network.classes.CaseFrame;
import sneps.network.classes.PathTrace;
import sneps.network.classes.RCFP;
import sneps.network.classes.Relation;
import sneps.network.classes.RelationsRestrictedCaseFrame;
import sneps.network.classes.Semantic;
import sneps.network.classes.SemanticHierarchy;
import sneps.network.classes.Wire;
import sneps.network.classes.setClasses.NodeSet;
import sneps.network.classes.term.Base;
import sneps.network.classes.term.Closed;
import sneps.network.classes.term.Term;
import sneps.network.paths.AndPath;
import sneps.network.paths.BUnitPath;
import sneps.network.paths.BangPath;
import sneps.network.paths.CFResBUnitPath;
import sneps.network.paths.CFResFUnitPath;
import sneps.network.paths.ComposePath;
import sneps.network.paths.ConversePath;
import sneps.network.paths.DomainRestrictPath;
import sneps.network.paths.FUnitPath;
import sneps.network.paths.IrreflexiveRestrictPath;
import sneps.network.paths.KPlusPath;
import sneps.network.paths.KStarPath;
import sneps.network.paths.OrPath;
import sneps.network.paths.Path;
import sneps.network.paths.RangeRestrictPath;
import sneps.snebr.Controller;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.LinkedList;

public class NetworkTest {
     static Semantic semantic;
    final static String semanticType = "Proposition";

    

    @Before
    public void setUp() {
    	semantic = new Semantic(semanticType);
    	Semantic.createDefaultSemantics();
    }

    @After
    public void tearDown() {
        Network.clearNetwork();
        Controller.clearSNeBR();
    }

     @Test   
     public void testBuildBaseNode() throws IllegalIdentifierException, NotAPropositionNodeException, NodeNotFoundInNetworkException {
        int sizeOfNodes = Network.getNodes().size();
        int sizeOfProps = Network.getPropositionNodes().size();
        Network.buildBaseNode("n0", semantic);
        Node n0 =  Network.getNode("n0");
        assertTrue(Network.getNodeById(0) instanceof PropositionNode);
        assertEquals(n0, Network.getNodeById(0));
        assertTrue(n0.getTerm() instanceof Base);
        assertEquals(Network.getNodes().size(), sizeOfNodes + 1);
        assertEquals(Network.getPropositionNodes().size(), sizeOfProps + 1);
    }
     
     @Test
     public void testSemanticCreation() throws SemanticNotFoundInNetworkException {
    	 // A should have no super class
    	 SemanticHierarchy.createSemanticType("A");
    	 
    	 Semantic A = SemanticHierarchy.getSemantic("A");
    	 
    	 LinkedList<Object> superClassesOfA = A.getSuperClassesNames();
    	 
    	 assertEquals(superClassesOfA.size(), 0);
    	 
    	 // try creating a semantic class with a super that does not exist
    	 try {
    		 SemanticHierarchy.createSemanticType("B", "C");
    	 }
    	 catch(Exception e) {
    		 assertEquals("The super class named 'C' does not exist!", e.getMessage());
    	 }
    	 
    	 // test creation of a semantic class with a super class
    	 Semantic B = SemanticHierarchy.createSemanticType("B", "A");
    	 
    	 LinkedList<Object> superClassesOfB = B.getSuperClassesNames();
    	 
    	 assertTrue(superClassesOfB.contains("A"));
    	 assertNotNull(B);
     }
     
     @Test
     public void testGetSemanticSuperClassNames() throws SemanticNotFoundInNetworkException {
    	 // A has no super class
    	 Semantic A = SemanticHierarchy.createSemanticType("A");
    	 
    	 // A is the super class of B
    	 Semantic B = SemanticHierarchy.createSemanticType("B", "A");
    	 
    	 // B is the super class of C and D
    	 SemanticHierarchy.createSemanticType("C", "B");
    	 Semantic D = SemanticHierarchy.createSemanticType("D", "B");
    	 
    	 LinkedList<Object> superClassesOfD = D.getSuperClassesNames();
    	 LinkedList<Object> superClassesOfB = B.getSuperClassesNames();
    	 LinkedList<Object> superClassesOfA = A.getSuperClassesNames();
    	 
    	 // check on the super classes of D
    	 assertEquals(superClassesOfD.size(), 2);
    	 assertTrue(superClassesOfD.contains("A"));
    	 assertTrue(superClassesOfD.contains("B"));
    	 
    	 // check on the super classes of B
    	 assertEquals(superClassesOfB.size(), 1);
    	 assertTrue(superClassesOfB.contains("A"));
    	 
    	 // check on the super classes of A
    	 assertEquals(superClassesOfA.size(), 0);
    	 
     }
     
     @Test
     public void testBuildMolecularNode() throws NotAPropositionNodeException, NodeNotFoundInNetworkException, IllegalIdentifierException, CannotBuildNodeException, EquivalentNodeException, CaseFrameMissMatchException, SemanticNotFoundInNetworkException {
    	 ArrayList<Wire> wires = new ArrayList<Wire>();
    	 LinkedList<Relation> relations = new LinkedList<>();
    	 
    	 // populate the relation and wire lists
    	 Relation relation1 = Network.defineRelation("relation 1", semanticType);
    	 Relation relation2 = Network.defineRelation("relation 2", semanticType);
    	 Relation relation3 = Network.defineRelation("relation 3", semanticType);
    	 
    	 relations.add(relation1);
    	 relations.add(relation2);
    	 relations.add(relation3);
    	 
    	 wires.add(new Wire(relation1, Network.buildBaseNode("b1", semantic)));
    	 wires.add(new Wire(relation2, Network.buildBaseNode("b2", semantic)));
    	 wires.add(new Wire(relation3, Network.buildBaseNode("b3", semantic)));
    	 
    	 // define a case frame for the node
    	 CaseFrame caseFrame = Network.defineCaseFrame(semanticType, relations);
    	 
    	 // create the node and check it was created successfully
    	 Node closedNode = Network.buildMolecularNode(wires, caseFrame);
    	 assertEquals(Network.getMolecularNodes().keySet().size(), 1);
    	 assertTrue(closedNode instanceof PropositionNode);
    	 assertEquals(Network.getNodes().size(), 4);
    	 assertEquals(Network.getPropositionNodes().size(), 4);
    	 
    	 Term term = closedNode.getTerm();
    	 assertTrue(term instanceof Closed);
     }
     
     @Test
     public void testBuildMolecularNodeAlreadyExists() throws CannotBuildNodeException, EquivalentNodeException, NotAPropositionNodeException, NodeNotFoundInNetworkException, IllegalIdentifierException, CaseFrameMissMatchException, SemanticNotFoundInNetworkException {
    	 ArrayList<Wire> wires = new ArrayList<Wire>();
    	 LinkedList<Relation> relations = new LinkedList<>();
    	 
    	 // populate the relation and wire lists
    	 Relation relation1 = Network.defineRelation("relation 1", semanticType);
    	 Relation relation2 = Network.defineRelation("relation 2", semanticType);
    	 Relation relation3 = Network.defineRelation("relation 3", semanticType);
    	 
    	 relations.add(relation1);
    	 relations.add(relation2);
    	 relations.add(relation3);
    	 
    	 wires.add(new Wire(relation1, Network.buildBaseNode("b1", semantic)));
    	 wires.add(new Wire(relation2, Network.buildBaseNode("b2", semantic)));
    	 wires.add(new Wire(relation3, Network.buildBaseNode("b3", semantic)));
    	 
    	 // define a case frame for the node
    	 CaseFrame caseFrame = Network.defineCaseFrame(semanticType, relations);
    	 
    	 // create the node and check it was created successfully
    	 Node closedNode = Network.buildMolecularNode(wires, caseFrame);
    	 String nodeID = closedNode.getIdentifier();
    	 assertEquals(Network.getMolecularNodes().keySet().size(), 1);
    	 assertTrue(closedNode instanceof PropositionNode);
    	 assertEquals(Network.getNodes().size(), 4);
    	 assertEquals(Network.getPropositionNodes().size(), 4);
    	 
    	 // try to create the same node again and check if it creates a duplicate
    	 closedNode = Network.buildMolecularNode(wires, caseFrame);
    	 String newID = closedNode.getIdentifier();
    	 assertEquals(nodeID, newID);
    	 assertEquals(Network.getMolecularNodes().keySet().size(), 1); 
     }
     
     @Test 
     public void testBuildMolecularNodeInvalidRelNodeSet() throws NotAPropositionNodeException, NodeNotFoundInNetworkException, IllegalIdentifierException, CannotBuildNodeException, EquivalentNodeException {
    	 ArrayList<Wire> wires = new ArrayList<Wire>();
    	 LinkedList<Relation> relations = new LinkedList<>();
    	 
    	 // populate the relation and wire lists
    	 Relation relation1 = Network.defineRelation("relation 1", semanticType);
    	 Relation relation2 = Network.defineRelation("relation 2", semanticType);
    	 Relation relation3 = Network.defineRelation("relation 3", semanticType);
    	 
    	 relations.add(relation1);
    	 relations.add(relation2);
    	 relations.add(relation3);
    	 
    	 wires.add(new Wire(relation1, Network.buildBaseNode("b1", semantic)));
    	 wires.add(new Wire(relation3, Network.buildBaseNode("b2", semantic)));
    	 
    	 // make one of the relations point to a node that it is not allowed to point at
    	 wires.add(new Wire(relation2, Network.buildBaseNode("b3", Semantic.individual)));
    	 
    	 // define a case frame for the node
    	 CaseFrame caseFrame = Network.defineCaseFrame(semanticType, relations);
    	 
    	 // create the node and check if it was created
    	 Node closedNode = null;
    	 try {
    		 closedNode = Network.buildMolecularNode(wires, caseFrame);
    	 }
    	 catch(Exception e) {
    		 assertNull(closedNode);
    		 assertTrue(e instanceof CannotBuildNodeException);
    		 assertEquals(e.getMessage(), "Cannot build the node .. the relation node pairs are not valid");
    		 assertEquals(Network.getNodes().size(), 3);
    		 String caseFrameID = caseFrame.getId();
    		 NodeSet molecularNodesBelongingToCaseFrame = Network.getMolecularNodes().get(caseFrameID);
    		 assertEquals(molecularNodesBelongingToCaseFrame.size(), 0);
    		 
    	 }
     }
     
     @Test 
     public void testBuildMolecularNodeNotFollowingCaseFrame() throws NotAPropositionNodeException, NodeNotFoundInNetworkException, IllegalIdentifierException, CannotBuildNodeException, EquivalentNodeException {
    	 ArrayList<Wire> wires = new ArrayList<Wire>();
    	 LinkedList<Relation> relations = new LinkedList<>();
    	 
    	 // populate the relation and wire lists
    	 Relation relation1 = Network.defineRelation("relation 1", semanticType);
    	 Relation relation2 = Network.defineRelation("relation 2", semanticType);
    	 Relation relation3 = Network.defineRelation("relation 3", semanticType);
    	 
    	 // only add two relations to the case frame relations
    	 relations.add(relation1);
    	 relations.add(relation2);
    	 
    	 wires.add(new Wire(relation1, Network.buildBaseNode("b1", semantic)));
    	 wires.add(new Wire(relation2, Network.buildBaseNode("b2", semantic)));
    	 wires.add(new Wire(relation3, Network.buildBaseNode("b3", semantic)));
    	 
    	 // define a case frame for the node
    	 CaseFrame caseFrame = Network.defineCaseFrame(semanticType, relations);
    	 
    	 // create the node and check if it was created
    	 Node closedNode = null;
    	 try {
    		 closedNode = Network.buildMolecularNode(wires, caseFrame);
    	 }
    	 catch(Exception e) {
    		 assertNull(closedNode);
    		 assertTrue(e instanceof CaseFrameMissMatchException);
    		 assertEquals(e.getMessage(), "Not following the case frame .. wrong node set size or wrong set of relations");
    		 assertEquals(Network.getNodes().size(), 3);
    		 String caseFrameID = caseFrame.getId();
    		 NodeSet molecularNodesBelongingToCaseFrame = Network.getMolecularNodes().get(caseFrameID);
    		 assertEquals(molecularNodesBelongingToCaseFrame.size(), 0);
    	 }
     }
     
     @Test
     public void testRelationCreationWithoutAdjustAndLimitValues() throws RelationDoesntExistException {
    	 String relationName = "relation name";
    	 Network.defineRelation(relationName, semanticType);
    	 
    	 // test creation of a new relation
    	 Relation relation = Network.getRelation(relationName);
    	 assertNotNull(relation);
    	 assertEquals(relation.getName(), relationName);
    	 assertEquals(relation.getType(), semanticType);
    	 assertEquals(relation.getAdjust(), "none");
    	 assertEquals(relation.getLimit(), 1);
    	 
    	 // test that the code does not create two relations with the same name
    	 // and returns the already existing relation instead
    	 Relation newRelation = Network.defineRelation(relationName, semanticType);
    	 assertEquals(relation, newRelation);
    	 assertEquals(1, Network.getRelations().keySet().size());
     }
     
     @Test
     public void testRelationCreationWithAdjustAndLimitValues() throws RelationDoesntExistException {
    	 String relationName = "relation name";
    	 int limit = 3;
    	 String adjust = "expand";
    	 Network.defineRelation(relationName, semanticType, adjust, limit);
    	 
    	 // test creation of a new relation
    	 Relation relation = Network.getRelation(relationName);
    	 assertNotNull(relation);
    	 assertEquals(relation.getName(), relationName);
    	 assertEquals(relation.getType(), semanticType);
    	 assertEquals(relation.getAdjust(), adjust);
    	 assertEquals(relation.getLimit(), limit);
    	 
    	 // test that the code does not create two relations with the same name
    	 // and returns the already existing relation instead
    	 Relation newRelation = Network.defineRelation(relationName, semanticType);
    	 assertEquals(relation, newRelation);
    	 assertEquals(1, Network.getRelations().keySet().size());
     }
     
     @Test
     public void testCaseFrameCreation() {
    	 LinkedList<Relation> relations = new LinkedList<Relation>();
    	 relations.add(Network.defineRelation("relation 1", semanticType));
    	 relations.add(Network.defineRelation("relation 2", semanticType));
    	 relations.add(Network.defineRelation("relation 3", semanticType));
    	 
    	 CaseFrame caseFrame = Network.defineCaseFrame(semanticType, relations);
    	 
    	 // make sure that it does not create duplicate case frames
    	 Network.defineCaseFrame(semanticType, relations);
    	 assertEquals(1, Network.getCaseFrames().keySet().size());
    	 
    	 
    	 assertEquals(caseFrame.getRelations().size(), 3);
    	 assertEquals(caseFrame.getSemanticClass(), semanticType);
     }
     
     @Test
     public void testRelationsRestrictedCaseFrameCreation() {
    	 LinkedList<RCFP> RCFPs = new LinkedList<RCFP>();
    	 RCFPs.add(new RCFP(Network.defineRelation("relation1", semanticType, "none", 1), "reduce", 5));
    	 RCFPs.add(new RCFP(Network.defineRelation("relation2", semanticType, "none", 1), "expand", 3));
    	 
    	 RelationsRestrictedCaseFrame RRCF = Network.defineCaseFrameWithConstraints(semanticType, RCFPs);
    	 
    	 // make sure that it does not create duplicate case frames
    	 Network.defineCaseFrameWithConstraints(semanticType, RCFPs);
    	 assertEquals(1, Network.getCaseFrames().keySet().size());
    	 
    	 assertEquals(RRCF.getRelations().size(), 2);
    	 assertEquals(RRCF.getSemanticClass(), semanticType);
     }
     
     @Test
     public void testDownCableSetCreation() throws NotAPropositionNodeException, NodeNotFoundInNetworkException, IllegalIdentifierException {
    	 // create the relations and add them to a linked list.
    	 // this linked list will be used to create the case frame
    	 Relation relation1 = new Relation("relation1", "Proposition");
    	 Relation relation2 = new Relation("relation2", "Proposition");
    	 LinkedList<Relation> relationList = new LinkedList<Relation>();
    	 relationList.add(relation1);
    	 relationList.add(relation2);
    	 
    	 // build two base nodes and put each one in a different node set
    	 // each node set will be used to create a down cable
    	 Node node1 = Network.buildBaseNode("n1", semantic);
    	 Node node2 = Network.buildBaseNode("n2", semantic);
    	 NodeSet nodeSet1 = new NodeSet();
    	 NodeSet nodeSet2 = new NodeSet();
    	 nodeSet1.addNode(node1);
    	 nodeSet2.addNode(node2);
    	 
    	 // create the case frame and down cables
    	 CaseFrame caseFrame = Network.defineCaseFrame(semanticType, relationList);
    	 
    	 DownCable cable1 = new DownCable(relation1, nodeSet1);
    	 DownCable cable2 = new DownCable(relation2, nodeSet2);
    	 LinkedList<DownCable> cableList = new LinkedList<DownCable>();
    	 cableList.add(cable1);
    	 cableList.add(cable2);

    	 // create the down cable set
    	 DownCableSet cableSet = new DownCableSet(cableList, caseFrame);
    	 
    	 cable1 = cableSet.getDownCable("relation1");
    	 cable2 = cableSet.getDownCable("relation2");
    	 
    	 assertFalse((cable1.getNodeSet().equals(cable2.getNodeSet())));
    	 
     }
     
     @Test
     public void testRemoveIsolatedMolecularNode() throws NotAPropositionNodeException, NodeNotFoundInNetworkException, IllegalIdentifierException, CannotBuildNodeException, EquivalentNodeException, CaseFrameMissMatchException, NodeCannotBeRemovedException, NodeNotFoundInPropSetException, SemanticNotFoundInNetworkException {
    	 ArrayList<Wire> wires = new ArrayList<Wire>();
    	 LinkedList<Relation> relations = new LinkedList<>();
    	 
    	 // populate the relation and wire lists
    	 Relation relation1 = Network.defineRelation("relation 1", semanticType);
    	 Relation relation2 = Network.defineRelation("relation 2", semanticType);
    	 Relation relation3 = Network.defineRelation("relation 3", semanticType);
    	 
    	 relations.add(relation1);
    	 relations.add(relation2);
    	 relations.add(relation3);
    	 
    	 wires.add(new Wire(relation1, Network.buildBaseNode("b1", semantic)));
    	 wires.add(new Wire(relation2, Network.buildBaseNode("b2", semantic)));
    	 wires.add(new Wire(relation3, Network.buildBaseNode("b3", semantic)));
    	 
    	 // define a case frame for the node
    	 CaseFrame caseFrame = Network.defineCaseFrame(semanticType, relations);
    	 
    	 // create the molecular node
    	 Node closedNode = Network.buildMolecularNode(wires, caseFrame);
    	 assertEquals(Network.getNodes().size(), 4);
    	 
    	 // test removal of the node
    	 Network.removeNode(closedNode);
    	 
    	 assertEquals(Network.getNodes().size(), 0);
    	 
     }

     
     @Test
     public void testRemoveNonIsolatedNode() throws NotAPropositionNodeException, NodeNotFoundInNetworkException, IllegalIdentifierException, SemanticNotFoundInNetworkException, CannotBuildNodeException, EquivalentNodeException, CaseFrameMissMatchException {
    	 Relation relation = Network.defineRelation("relation1", "Proposition");
    	 
    	 LinkedList<Relation> relList = new LinkedList<Relation>();
    	 relList.add(relation);
    	 
    	 CaseFrame caseFrame = Network.defineCaseFrame("Proposition", relList);	
    	 
    	 Node nonIsolatedNode = Network.buildBaseNode("b1", SemanticHierarchy.getSemantic("Proposition"));
    	 
    	 ArrayList<Wire> wires = new ArrayList<Wire>();
    	 wires.add(new Wire(relation, nonIsolatedNode));
    	 
    	 Network.buildMolecularNode(wires, caseFrame);
    	 
    	 try {
    		 Network.removeNode(nonIsolatedNode);
    	 }
    	 catch (Exception e) {
    		 assertEquals(e.getMessage(), "Cannot remove the node named 'b1' because it is not isolated");
    		 assertEquals(Network.getNodes().size(), 2);
    	 }
     }
     
     @Test
     public void testSemanticInheritance() throws NotAPropositionNodeException, NodeNotFoundInNetworkException, IllegalIdentifierException, SemanticNotFoundInNetworkException, CannotBuildNodeException, EquivalentNodeException, CaseFrameMissMatchException {
    	 // a relation can point to a certain semantic type or any semantic type that inherits from it
    	 // this method tests that this is working correctly when the method validRelNodePairs is called
    	 
    	 // create the relations and add them to a linked list.
    	 // this linked list will be used to create the case frame
    	 Relation relation1 = new Relation("relation1", "Proposition");
    	 Relation relation2 = new Relation("relation2", "Proposition");
    	 LinkedList<Relation> relationList = new LinkedList<Relation>();
    	 relationList.add(relation1);
    	 relationList.add(relation2);
    	 
    	 // define a case frame with the two relations
    	 CaseFrame caseFrame = Network.defineCaseFrame("Proposition", relationList);
    	 
    	 // create a new semantic class that extends Proposition
    	 SemanticHierarchy.createSemanticType("child", "Proposition");
    	 
    	 // create two base nodes, one with proposition as its semantic type, and another with child as its semantic type
    	 Network.buildBaseNode("prop", SemanticHierarchy.getSemantic("Proposition"));
    	 Network.buildBaseNode("child", SemanticHierarchy.getSemantic("child"));
    	 
    	 // populate the list of wires with the node's downcableset
    	 ArrayList<Wire> wires = new ArrayList<Wire>();
    	 wires.add(new Wire(relation1, Network.getNode("prop")));
    	 wires.add(new Wire(relation2, Network.getNode("child")));
    	
    	 // try creating the node and check if created successfully
    	 Network.buildMolecularNode(wires, caseFrame);
    	 
    	 assertEquals(Network.getMolecularNodes().size(), 1);
     }

     
    @Test
    public void testRemoveIsolatedBaseNode() throws NotAPropositionNodeException, NodeNotFoundInNetworkException, IllegalIdentifierException, SemanticNotFoundInNetworkException, NodeCannotBeRemovedException, NodeNotFoundInPropSetException {
    	// create an isolated base node
    	Network.buildBaseNode("node", SemanticHierarchy.getSemantic("Proposition"));
    	assertEquals(Network.getNodes().size(), 1);
    	
    	Network.removeNode(Network.getNode("node"));
    	assertEquals(Network.getNodes().size(), 0);
    	
    	try {
    		Network.getNode("node");
    	}
    	catch(Exception e) {
    		assertEquals(e.getMessage(), "There is no node named 'node' in the network");
    	}
    }
    
    
    @Test
    public void testBangPath() throws NotAPropositionNodeException, NodeNotFoundInNetworkException, IllegalIdentifierException, SemanticNotFoundInNetworkException, DuplicatePropositionException, ContradictionFoundException, ContextNameDoesntExistException {
    	Node node = Network.buildBaseNode("node", SemanticHierarchy.getSemantic("Proposition"));
    	
    	BangPath bPath = new BangPath();
    	
    	// follow the bang path form a non-asserted node
    	LinkedList<Object[]> nodes = bPath.follow(node, new PathTrace(), Controller.getCurrentContext());
    	assertEquals(nodes.size(), 0);
    	
    	// assert the node
    	Controller.addPropToCurrentContext(node.getId());
    	
    	// follow the bang path from an asserted node
    	nodes = bPath.follow(node, new PathTrace(), Controller.getCurrentContext());
    	assertEquals(nodes.size(), 1);
    	assertEquals( ((Node) nodes.get(0)[0]).getId() , node.getId());
    	
    	// follow the converse of the bPath
    	nodes = bPath.follow(node, new PathTrace(), Controller.getCurrentContext());
    	assertEquals(nodes.size(), 1);
    	assertEquals( ((Node) nodes.get(0)[0]).getId() , node.getId());
    	
    	// create a new node that has a semantic type that is a child of proposition
    	SemanticHierarchy.createSemanticType("semantic", "Proposition");
    	node = Network.buildBaseNode("node2", SemanticHierarchy.getSemantic("semantic"));
    	
    	// assert the node
    	Controller.addPropToCurrentContext(node.getId());
    	
    	// follow the band path from the asserted node
    	nodes = bPath.follow(node, new PathTrace(), Controller.getCurrentContext());
    	assertEquals(nodes.size(), 1);
    	assertEquals( ((Node) nodes.get(0)[0]).getId() , node.getId());
    	
    	// follow the converse of the bPath
    	nodes = bPath.follow(node, new PathTrace(), Controller.getCurrentContext());
    	assertEquals(nodes.size(), 1);
    	assertEquals( ((Node) nodes.get(0)[0]).getId() , node.getId());
    }
    
    @Test
    public void testFUnitPath() throws NotAPropositionNodeException, NodeNotFoundInNetworkException, IllegalIdentifierException, CannotBuildNodeException, EquivalentNodeException, CaseFrameMissMatchException, SemanticNotFoundInNetworkException {
    	Object[] testNetwork = buildNodesForUnitPathTesting();
    	Relation relation = (Relation) testNetwork[0];
    	Node base = (Node) testNetwork[2];
    	Node parent = (Node) testNetwork[3];
    	
    	
    	// create the path and follow it
    	FUnitPath path = new FUnitPath(relation);
    	LinkedList<Object[]> nodes = path.follow(parent, new PathTrace(), Controller.getCurrentContext());
    	
    	assertEquals(nodes.size(), 1);
    	assertEquals(( (Node) nodes.get(0)[0]).getId(), base.getId());
    	
    	nodes = path.followConverse(parent, new PathTrace(), Controller.getCurrentContext());
    	assertEquals(nodes.size(),0);
    	
    	// follow the converse of the path
    	nodes = path.followConverse(base, new PathTrace(), Controller.getCurrentContext());
    	
    	assertEquals(nodes.size(), 1);
    	assertEquals(( (Node) nodes.get(0)[0]).getId(), parent.getId());
    	
    	nodes = path.follow(base, new PathTrace(), Controller.getCurrentContext());
    	assertEquals(nodes.size(), 0);
    }
    
    @Test
    public void testBUnitPath() throws NotAPropositionNodeException, NodeNotFoundInNetworkException, IllegalIdentifierException, CannotBuildNodeException, EquivalentNodeException, CaseFrameMissMatchException, SemanticNotFoundInNetworkException {
    	Object[] testNetwork = buildNodesForUnitPathTesting();
    	Relation relation = (Relation) testNetwork[0];
    	Node base = (Node) testNetwork[2];
    	Node parent = (Node) testNetwork[3];
    	
    	
    	// create path and follow it
    	BUnitPath path = new BUnitPath(relation);
    	LinkedList<Object[]> nodes = path.follow(base, new PathTrace(), Controller.getCurrentContext());
    	assertEquals(nodes.size(), 1);
    	assertEquals(( (Node) nodes.get(0)[0]).getId(), parent.getId());
    	
    	nodes = path.follow(parent, new PathTrace(), Controller.getCurrentContext());
    	assertEquals(nodes.size(), 0);
    	
    	// follow the converse of the path
    	nodes = path.followConverse(parent, new PathTrace(), Controller.getCurrentContext());
    	
    	assertEquals(nodes.size(), 1);
    	assertEquals(( (Node) nodes.get(0)[0]).getId(), base.getId());
    	
    	nodes = path.followConverse(base, new PathTrace(), Controller.getCurrentContext());
    	assertEquals(nodes.size(),0);
    }
    
    @Test
    public void testCFResFUnitPath() throws NotAPropositionNodeException, NodeNotFoundInNetworkException, IllegalIdentifierException, CannotBuildNodeException, EquivalentNodeException, CaseFrameMissMatchException, SemanticNotFoundInNetworkException {
    	Object[] testNetwork = buildNodesForCFResUnitPathTesting();
    	Relation relation = (Relation) testNetwork[0];
    	CaseFrame testCF = (CaseFrame) testNetwork[1];
    	Node base = (Node) testNetwork[2];
    	Node parent = (Node) testNetwork[3];
    	CaseFrame otherCF = (CaseFrame) testNetwork[4];
    	
    	// create the path and follow it
    	CFResFUnitPath path = new CFResFUnitPath(relation, testCF);
    	LinkedList<Object[]> nodes = path.follow(parent, new PathTrace(), Controller.getCurrentContext());
    	
    	assertEquals(nodes.size(), 1);
    	assertEquals(( (Node) nodes.get(0)[0]).getId(), base.getId());
    	
    	nodes = path.followConverse(parent, new PathTrace(), Controller.getCurrentContext());
    	assertEquals(nodes.size(),0);
    	
    	// follow the converse of the path
    	nodes = path.followConverse(base, new PathTrace(), Controller.getCurrentContext());
    	
    	assertEquals(nodes.size(), 1);
    	assertEquals(( (Node) nodes.get(0)[0]).getId(), parent.getId());
    	
    	nodes = path.follow(base, new PathTrace(), Controller.getCurrentContext());
    	assertEquals(nodes.size(), 0);
    	
    	// follow a path with a different case frame
    	path = new CFResFUnitPath(relation, otherCF);
    	nodes = path.follow(parent, new PathTrace(), Controller.getCurrentContext());
    	assertEquals(nodes.size(), 0);
    	
    	nodes = path.follow(base, new PathTrace(), Controller.getCurrentContext());
    	assertEquals(nodes.size(), 0);
    	
    	nodes = path.followConverse(base, new PathTrace(), Controller.getCurrentContext());
    	assertEquals(nodes.size(),0);
    	
    	nodes = path.followConverse(parent, new PathTrace(), Controller.getCurrentContext());
    	assertEquals(nodes.size(),0);
    }
    
    @Test
    public void testCFResBUnitPath() throws NotAPropositionNodeException, NodeNotFoundInNetworkException, IllegalIdentifierException, CannotBuildNodeException, EquivalentNodeException, CaseFrameMissMatchException, SemanticNotFoundInNetworkException {
    	Object[] testNetwork = buildNodesForCFResUnitPathTesting();
    	Relation relation = (Relation) testNetwork[0];
    	CaseFrame testCF = (CaseFrame) testNetwork[1];
    	Node base = (Node) testNetwork[2];
    	Node parent = (Node) testNetwork[3];
    	CaseFrame otherCF = (CaseFrame) testNetwork[4];
    	
    	// create the path and follow it
    	CFResBUnitPath path = new CFResBUnitPath(relation, testCF);
    	LinkedList<Object[]> nodes = path.follow(base, new PathTrace(), Controller.getCurrentContext());
    	
    	assertEquals(nodes.size(), 1);
    	assertEquals(( (Node) nodes.get(0)[0]).getId(), parent.getId());
    	
    	nodes = path.followConverse(base, new PathTrace(), Controller.getCurrentContext());
    	assertEquals(nodes.size(),0);
    	
    	// follow the converse of the path
    	nodes = path.followConverse(parent, new PathTrace(), Controller.getCurrentContext());
    	
    	assertEquals(nodes.size(), 1);
    	assertEquals(( (Node) nodes.get(0)[0]).getId(), base.getId());
    	
    	nodes = path.follow(parent, new PathTrace(), Controller.getCurrentContext());
    	assertEquals(nodes.size(), 0);
    	
    	// follow a path with a different case frame
    	path = new CFResBUnitPath(relation, otherCF);
    	nodes = path.follow(base, new PathTrace(), Controller.getCurrentContext());
    	assertEquals(nodes.size(), 0);
    	
    	nodes = path.follow(parent, new PathTrace(), Controller.getCurrentContext());
    	assertEquals(nodes.size(), 0);
    	
    	nodes = path.followConverse(parent, new PathTrace(), Controller.getCurrentContext());
    	assertEquals(nodes.size(),0);
    	
    	nodes = path.followConverse(base, new PathTrace(), Controller.getCurrentContext());
    	assertEquals(nodes.size(),0);
    }
    
    @Test
    public void testOrPath() throws NotAPropositionNodeException, NodeNotFoundInNetworkException, IllegalIdentifierException, CannotBuildNodeException, EquivalentNodeException, CaseFrameMissMatchException, SemanticNotFoundInNetworkException {
    	// create the relations
    	Relation relation1 = Network.defineRelation("rel1", semanticType);
    	Relation relation2 = Network.defineRelation("rel2", semanticType);
    	LinkedList<Relation> relationSet = new LinkedList<Relation>();
    	relationSet.add(relation1);
    	relationSet.add(relation2);
    	
    	// define case frame
    	CaseFrame caseFrame = Network.defineCaseFrame(semanticType, relationSet);
    	
    	// build the base nodes
    	Node base1 = Network.buildBaseNode("b1", semantic);
    	Node base2 = Network.buildBaseNode("b2", semantic);
    	
    	// create wires
    	ArrayList<Wire> wires = new ArrayList<Wire>();
    	wires.add(new Wire(relation1, base1));
    	wires.add(new Wire(relation2, base2));
    	
    	// build the molecular node
    	Node parent = Network.buildMolecularNode(wires, caseFrame);
    	
    	// build the paths and use them to build an or path
    	FUnitPath path1 = new FUnitPath(relation1);
    	FUnitPath path2 = new FUnitPath(relation2);
    	LinkedList<Path> paths = new LinkedList<Path>();
    	paths.add(path1);
    	paths.add(path2);
    	OrPath orPath = new OrPath(paths);
    	
    	// follow the path from the parent
    	LinkedList<Object[]> nodes = orPath.follow(parent, new PathTrace(), Controller.getCurrentContext());
    	assertEquals(nodes.size(), 2);
    	assertTrue( ( ((Node)nodes.get(0)[0]).getId() == (base1.getId()) || ((Node)nodes.get(0)[0]).getId() == (base2.getId()) ) 
    			 && ( ((Node)nodes.get(1)[0]).getId() == (base1.getId()) || ((Node)nodes.get(1)[0]).getId() == (base2.getId()) ) );
    	
    	// follow the converse from base 1
    	nodes = orPath.followConverse(base1, new PathTrace(), Controller.getCurrentContext());
    	assertEquals(nodes.size(), 1);
    	assertEquals(((Node)nodes.get(0)[0]).getId(), parent.getId());
    	
    	// follow the converse from base 2
    	nodes = orPath.followConverse(base2, new PathTrace(), Controller.getCurrentContext());
    	assertEquals(nodes.size(), 1);
    	assertEquals(((Node)nodes.get(0)[0]).getId(), parent.getId());
    }
    
    @Test
    public void testAndPath() throws NotAPropositionNodeException, NodeNotFoundInNetworkException, IllegalIdentifierException, CannotBuildNodeException, EquivalentNodeException, CaseFrameMissMatchException, SemanticNotFoundInNetworkException {
    	// create the relations
    	Relation relation1 = Network.defineRelation("rel1", semanticType);
    	Relation relation2 = Network.defineRelation("rel2", semanticType);
    	LinkedList<Relation> relationSet = new LinkedList<Relation>();
    	relationSet.add(relation1);
    	relationSet.add(relation2);
    	
    	// define case frame
    	CaseFrame caseFrame = Network.defineCaseFrame(semanticType, relationSet);
    	
    	// build the base node
    	Node base = Network.buildBaseNode("b1", semantic);
    	
    	// create wires
    	ArrayList<Wire> wires = new ArrayList<Wire>();
    	wires.add(new Wire(relation1, base));
    	wires.add(new Wire(relation2, base));
    	
    	// build the molecular node
    	Node parent = Network.buildMolecularNode(wires, caseFrame);
    	
    	// build the paths and use them to build an and path
    	FUnitPath path1 = new FUnitPath(relation1);
    	FUnitPath path2 = new FUnitPath(relation2);
    	LinkedList<Path> paths = new LinkedList<Path>();
    	paths.add(path1);
    	paths.add(path2);
    	AndPath andPath = new AndPath(paths);
    	
    	// follow the path from the parent
    	LinkedList<Object[]> nodes = andPath.follow(parent, new PathTrace(), Controller.getCurrentContext());
    	assertEquals(nodes.size(), 1);
    	assertEquals(base.getId(), ( (Node) nodes.get(0)[0]).getId());
    	
    	// follow the path from the base
    	nodes = andPath.follow(base, new PathTrace(), Controller.getCurrentContext());
    	assertEquals(nodes.size(), 0);
    	
    	// follow the converse of the path from the parent
    	nodes = andPath.followConverse(parent, new PathTrace(), Controller.getCurrentContext());
    	assertEquals(nodes.size(), 0);
    	
    	// follow the converse of the path from the base
    	nodes = andPath.followConverse(base, new PathTrace(), Controller.getCurrentContext());
    	assertEquals(nodes.size(), 1);
    	assertEquals(parent.getId(), ( (Node) nodes.get(0)[0]).getId());
    	
    	// create a new base node
    	Node base2 = Network.buildBaseNode("b2", semantic);
    	
    	// create new wire list
    	wires.clear();
    	wires.add(new Wire(relation1, base));
    	wires.add(new Wire(relation2, base2));
    	
    	// create new parent
    	parent = Network.buildMolecularNode(wires, caseFrame);
    	
    	// follow the path from the parent
    	nodes = andPath.follow(parent, new PathTrace(), Controller.getCurrentContext());
    	assertEquals(nodes.size(), 0);
    }
    
    @Test
    public void testConversePath() throws NotAPropositionNodeException, NodeNotFoundInNetworkException, IllegalIdentifierException, CannotBuildNodeException, EquivalentNodeException, CaseFrameMissMatchException, SemanticNotFoundInNetworkException {
    	Object[] testNetwork = buildNodesForUnitPathTesting();
    	Relation relation = (Relation) testNetwork[0];
    	Node base = (Node) testNetwork[2];
    	Node parent = (Node) testNetwork[3];
    	
    	
    	// create path and follow it
    	BUnitPath BUPath = new BUnitPath(relation);
    	ConversePath path = new ConversePath(BUPath);
    	LinkedList<Object[]> nodes = path.follow(parent, new PathTrace(), Controller.getCurrentContext());
    	
    	assertEquals(nodes.size(), 1);
    	assertEquals(( (Node) nodes.get(0)[0]).getId(), base.getId());
    	
    	nodes = path.followConverse(parent, new PathTrace(), Controller.getCurrentContext());
    	assertEquals(nodes.size(),0);
    	
    	// follow the converse of the path
    	nodes = path.followConverse(base, new PathTrace(), Controller.getCurrentContext());
    	
    	assertEquals(nodes.size(), 1);
    	assertEquals(( (Node) nodes.get(0)[0]).getId(), parent.getId());
    	
    	nodes = path.follow(base, new PathTrace(), Controller.getCurrentContext());
    	assertEquals(nodes.size(), 0);
    }
    
    @Test
    public void testIrreflexiveRestrictPath() throws NotAPropositionNodeException, NodeNotFoundInNetworkException, IllegalIdentifierException, CannotBuildNodeException, EquivalentNodeException, CaseFrameMissMatchException, SemanticNotFoundInNetworkException {
    	boolean parentFound = false, grandparentFound = false, baseFound = false;
    	Object[] network = buildNetworkForKPlusAndKStarTesting();
    	Relation relation = (Relation) network[0];
    	Node base = (Node) network[2];
    	Node parent = (Node) network[3];
    	Node grandparent = (Node) network[4];
    	
    	// create the path and use it to create a KStarPath
    	FUnitPath FU = new FUnitPath(relation);
    	KStarPath KStarPath = new KStarPath(FU);
    	IrreflexiveRestrictPath path = new IrreflexiveRestrictPath(KStarPath);
    	
    	// follow the path from the grandparent
    	LinkedList<Object[]> nodes = path.follow(grandparent, new PathTrace(), Controller.getCurrentContext());
    	assertEquals(nodes.size(), 2);
    	
    	for(int i = 0; i < 2; i++) {
    		Node current = (Node) nodes.get(i)[0];
    		if(current.getId() == base.getId())
    			baseFound = true;
    		if(current.getId() == parent.getId())
    			parentFound = true;
    		if(current.getId() == grandparent.getId())
    			grandparentFound = true;
    	}
    	
    	assertTrue(baseFound && parentFound && !grandparentFound);
    }
    
    @Test
    public void testComposePath() throws NotAPropositionNodeException, NodeNotFoundInNetworkException, IllegalIdentifierException, CannotBuildNodeException, EquivalentNodeException, CaseFrameMissMatchException, SemanticNotFoundInNetworkException {
    	// define relations
    	Relation relation1, relation2;
    	relation1 = Network.defineRelation("relation1", semanticType);
    	relation2 = Network.defineRelation("relation2", semanticType);
    	LinkedList<Relation> relations1 = new LinkedList<Relation>();
    	LinkedList<Relation> relations2 = new LinkedList<Relation>();
    	relations1.add(relation1);
    	relations2.add(relation2);
    	
    	// define case frames
    	CaseFrame cf1, cf2;
    	cf1 = Network.defineCaseFrame(semanticType, relations1);
    	cf2 = Network.defineCaseFrame(semanticType, relations2);
    	
    	// build the base node
    	Node base = Network.buildBaseNode("b1", semantic);
    	
    	// build a parent
    	ArrayList<Wire> wires = new ArrayList<Wire>();
    	wires.add(new Wire(relation1, base));
    	
    	Node parent = Network.buildMolecularNode(wires, cf1);
    	
    	// build a grandparent
    	wires.clear();
    	wires.add(new Wire(relation2, parent));
    	
    	Node grandparent = Network.buildMolecularNode(wires, cf2);
    	
    	// build the path
    	FUnitPath fPath1 = new FUnitPath(relation1);
    	FUnitPath fPath2 = new FUnitPath(relation2);
    	LinkedList<Path> paths = new LinkedList<Path>();
    	paths.add(fPath2);
    	paths.add(fPath1);
    	ComposePath path = new ComposePath(paths);
    	
    	// follow the path from the grandparent
    	LinkedList<Object[]> nodes = path.follow(grandparent, new PathTrace(), Controller.getCurrentContext());
    	assertEquals(nodes.size(), 1);
    	assertEquals(( (Node) nodes.get(0)[0]).getId(), base.getId());
    	
    	nodes = path.followConverse(grandparent, new PathTrace(), Controller.getCurrentContext());
    	assertEquals(nodes.size(), 0);
    	
    	nodes = path.follow(base, new PathTrace(), Controller.getCurrentContext());
    	assertEquals(nodes.size(), 0);
    	
    	nodes = path.followConverse(base, new PathTrace(), Controller.getCurrentContext());
    	assertEquals(nodes.size(), 1);
    	assertEquals(( (Node) nodes.get(0)[0]).getId(), grandparent.getId());
    }
    
    @Test
    public void testKStarPath() throws NotAPropositionNodeException, NodeNotFoundInNetworkException, IllegalIdentifierException, CannotBuildNodeException, EquivalentNodeException, CaseFrameMissMatchException, SemanticNotFoundInNetworkException {
    	boolean parentFound = false, grandparentFound = false, baseFound = false;
    	Object[] network = buildNetworkForKPlusAndKStarTesting();
    	Relation relation = (Relation) network[0];
    	Node base = (Node) network[2];
    	Node parent = (Node) network[3];
    	Node grandparent = (Node) network[4];
    	
    	// create the path and use it to create a KStarPath
    	FUnitPath FU = new FUnitPath(relation);
    	KStarPath path = new KStarPath(FU);
    	
    	// follow the path from the grandparent
    	LinkedList<Object[]> nodes = path.follow(grandparent, new PathTrace(), Controller.getCurrentContext());
    	assertEquals(nodes.size(), 3);
    	
    	for(int i = 0; i < 3; i++) {
    		Node current = (Node) nodes.get(i)[0];
    		if(current.getId() == base.getId())
    			baseFound = true;
    		if(current.getId() == parent.getId())
    			parentFound = true;
    		if(current.getId() == grandparent.getId())
    			grandparentFound = true;
    	}
    	
    	assertTrue(baseFound && parentFound && grandparentFound);
    	baseFound = parentFound = grandparentFound = false;
    	
    	nodes = path.followConverse(grandparent, new PathTrace(), Controller.getCurrentContext());
    	assertEquals(nodes.size(), 1);
    	assertEquals(((Node)nodes.get(0)[0]).getId(), grandparent.getId());
    	
    	// follow the path from the parent
    	nodes = path.follow(parent, new PathTrace(), Controller.getCurrentContext());
    	assertEquals(nodes.size(), 2);
    	
    	for(int i = 0; i < 2; i++) {
    		Node current = (Node) nodes.get(i)[0];
    		if(current.getId() == base.getId())
    			baseFound = true;
    		if(current.getId() == parent.getId())
    			parentFound = true;
    		if(current.getId() == grandparent.getId())
    			grandparentFound = true;
    	}
    	
    	assertTrue(baseFound && parentFound && !grandparentFound);
    	baseFound = parentFound = grandparentFound = false;
    	
    	nodes = path.followConverse(parent, new PathTrace(), Controller.getCurrentContext());
    	assertEquals(nodes.size(), 2);

    	for(int i = 0; i < 2; i++) {
    		Node current = (Node) nodes.get(i)[0];
    		if(current.getId() == base.getId())
    			baseFound = true;
    		if(current.getId() == parent.getId())
    			parentFound = true;
    		if(current.getId() == grandparent.getId())
    			grandparentFound = true;
    	}
    	
    	assertTrue(!baseFound && parentFound && grandparentFound);
    	baseFound = parentFound = grandparentFound = false;
    	
    	// follow the path from the base
    	nodes = path.followConverse(base, new PathTrace(), Controller.getCurrentContext());
    	assertEquals(nodes.size(), 3);

    	for(int i = 0; i < 3; i++) {
    		Node current = (Node) nodes.get(i)[0];
    		if(current.getId() == base.getId())
    			baseFound = true;
    		if(current.getId() == parent.getId())
    			parentFound = true;
    		if(current.getId() == grandparent.getId())
    			grandparentFound = true;
    	}
    	
    	assertTrue(baseFound && parentFound && grandparentFound);
    	
    	nodes = path.follow(base, new PathTrace(), Controller.getCurrentContext());
    	assertEquals(nodes.size(), 1);
    	assertEquals(((Node)nodes.get(0)[0]).getId(), base.getId());
    }
    
    @Test
    public void testKPlusPath() throws NotAPropositionNodeException, NodeNotFoundInNetworkException, IllegalIdentifierException, CannotBuildNodeException, EquivalentNodeException, CaseFrameMissMatchException, SemanticNotFoundInNetworkException {
    	boolean parentFound = false, grandparentFound = false, baseFound = false;
    	Object[] network = buildNetworkForKPlusAndKStarTesting();
    	Relation relation = (Relation) network[0];
    	Node base = (Node) network[2];
    	Node parent = (Node) network[3];
    	Node grandparent = (Node) network[4];
    	
    	// create the path and use it to create a KStarPath
    	FUnitPath FU = new FUnitPath(relation);
    	KPlusPath path = new KPlusPath(FU);
    	
    	// follow the path from the grandparent
    	LinkedList<Object[]> nodes = path.follow(grandparent, new PathTrace(), Controller.getCurrentContext());
    	assertEquals(nodes.size(), 2);
    	
    	for(int i = 0; i < 2; i++) {
    		Node current = (Node) nodes.get(i)[0];
    		if(current.getId() == base.getId())
    			baseFound = true;
    		if(current.getId() == parent.getId())
    			parentFound = true;
    		if(current.getId() == grandparent.getId())
    			grandparentFound = true;
    	}
    	
    	assertTrue(baseFound && parentFound && !grandparentFound);
    	baseFound = parentFound = grandparentFound = false;
    	
    	nodes = path.followConverse(grandparent, new PathTrace(), Controller.getCurrentContext());
    	assertEquals(nodes.size(), 0);
    	
    	// follow the path from the parent
    	nodes = path.follow(parent, new PathTrace(), Controller.getCurrentContext());
    	assertEquals(nodes.size(), 1);
    	
    	for(int i = 0; i < 1; i++) {
    		Node current = (Node) nodes.get(i)[0];
    		if(current.getId() == base.getId())
    			baseFound = true;
    		if(current.getId() == parent.getId())
    			parentFound = true;
    		if(current.getId() == grandparent.getId())
    			grandparentFound = true;
    	}
    	
    	assertTrue(baseFound && !parentFound && !grandparentFound);
    	baseFound = parentFound = grandparentFound = false;
    	
    	nodes = path.followConverse(parent, new PathTrace(), Controller.getCurrentContext());
    	assertEquals(nodes.size(), 1);

    	for(int i = 0; i < 1; i++) {
    		Node current = (Node) nodes.get(i)[0];
    		if(current.getId() == base.getId())
    			baseFound = true;
    		if(current.getId() == parent.getId())
    			parentFound = true;
    		if(current.getId() == grandparent.getId())
    			grandparentFound = true;
    	}
    	
    	assertTrue(!baseFound && !parentFound && grandparentFound);
    	baseFound = parentFound = grandparentFound = false;
    	
    	// follow the path from the base
    	nodes = path.followConverse(base, new PathTrace(), Controller.getCurrentContext());
    	assertEquals(nodes.size(), 2);

    	for(int i = 0; i < 2; i++) {
    		Node current = (Node) nodes.get(i)[0];
    		if(current.getId() == base.getId())
    			baseFound = true;
    		if(current.getId() == parent.getId())
    			parentFound = true;
    		if(current.getId() == grandparent.getId())
    			grandparentFound = true;
    	}
    	
    	assertTrue(!baseFound && parentFound && grandparentFound);
    	
    	nodes = path.follow(base, new PathTrace(), Controller.getCurrentContext());
    	assertEquals(nodes.size(), 0);
    }
    
    @Test
    public void testDomainRestrictPath() throws NotAPropositionNodeException, NodeNotFoundInNetworkException, IllegalIdentifierException, CannotBuildNodeException, EquivalentNodeException, CaseFrameMissMatchException, SemanticNotFoundInNetworkException {
    	// create relations
    	Relation relation1 = Network.defineRelation("relation1", semanticType);
    	Relation relation2 = Network.defineRelation("relation2", semanticType);
    	LinkedList<Relation> relations = new LinkedList<Relation>();
    	relations.add(relation1);
    	relations.add(relation2);
    	
    	// define case frame
    	CaseFrame caseFrame = Network.defineCaseFrame(semanticType, relations);
    	
    	// build two base nodes
    	Node base = Network.buildBaseNode("b1", semantic);
    	Node zNode = Network.buildBaseNode("zNode", semantic);
    	
    	// build the parent
    	ArrayList<Wire> wires = new ArrayList<Wire>();
    	wires.add(new Wire(relation1, base));
    	wires.add(new Wire(relation2, zNode));
    	Node parent = Network.buildMolecularNode(wires, caseFrame);
    	
    	// create the paths
    	FUnitPath p = new FUnitPath(relation1);
    	FUnitPath q = new FUnitPath(relation2);
    	DomainRestrictPath path = new DomainRestrictPath(q, zNode, p);
    	
    	// follow the path from the parent
    	LinkedList<Object[]> nodes = path.follow(parent, new PathTrace(), Controller.getCurrentContext());
    	assertEquals(nodes.size(), 1);
    	assertEquals(((Node)nodes.get(0)[0]).getId(), base.getId());
    	
    	nodes = path.followConverse(parent, new PathTrace(), Controller.getCurrentContext());
    	assertEquals(nodes.size(), 0);
    	
    	// follow the path from the base
    	nodes = path.followConverse(base, new PathTrace(), Controller.getCurrentContext());
    	assertEquals(nodes.size(), 1);
    	assertEquals(((Node)nodes.get(0)[0]).getId(), parent.getId());
    	
    	nodes = path.follow(base, new PathTrace(), Controller.getCurrentContext());
    	assertEquals(nodes.size(), 0);
    }
    
    @Test
    public void testRangeRestrictPath() throws NotAPropositionNodeException, NodeNotFoundInNetworkException, IllegalIdentifierException, CannotBuildNodeException, EquivalentNodeException, CaseFrameMissMatchException, SemanticNotFoundInNetworkException {
    	Object[] network = buildNetworkForKPlusAndKStarTesting();
    	Relation relation = (Relation) network[0];
    	Node base = (Node) network[2];
    	Node parent = (Node) network[3];
    	Node grandparent = (Node) network[4];
    	
    	// create the paths
    	FUnitPath p = new FUnitPath(relation);
    	FUnitPath q = new FUnitPath(relation);
    	RangeRestrictPath path = new RangeRestrictPath(p, q, base);
    	
    	// follow the path from the grandparent
    	LinkedList<Object[]> nodes = path.follow(grandparent, new PathTrace(), Controller.getCurrentContext());
    	assertEquals(nodes.size(), 1);
    	assertEquals(((Node)nodes.get(0)[0]).getId(), parent.getId());
    	
    	nodes = path.followConverse(grandparent, new PathTrace(), Controller.getCurrentContext());
    	assertEquals(nodes.size(), 0);
    	
    	// follow the path from the parent
    	nodes = path.followConverse(parent, new PathTrace(), Controller.getCurrentContext());
    	assertEquals(nodes.size(), 1);
    	assertEquals(((Node)nodes.get(0)[0]).getId(), grandparent.getId());
    	
    	nodes = path.follow(parent, new PathTrace(), Controller.getCurrentContext());
    	assertEquals(nodes.size(), 0);
    }
    
    private Object[] buildNetworkForKPlusAndKStarTesting() throws NotAPropositionNodeException, NodeNotFoundInNetworkException, IllegalIdentifierException, CannotBuildNodeException, EquivalentNodeException, CaseFrameMissMatchException, SemanticNotFoundInNetworkException {
    	// create relations
    	Relation relation = Network.defineRelation("rel1", semanticType);
    	LinkedList<Relation> relations = new LinkedList<Relation>();
    	relations.add(relation);
    	
    	// define the case frame
    	CaseFrame testCF = Network.defineCaseFrame(semanticType, relations);
    	
    	// build base node
    	Node base = Network.buildBaseNode("b", semantic);
    	
    	// create wires for parent
    	ArrayList<Wire> wires = new ArrayList<Wire>();
    	wires.add(new Wire(relation, base));
    	
    	// create parent node
    	Node parent = Network.buildMolecularNode(wires, testCF);
    	
    	// create wires for grandparent
    	wires.clear();
    	wires.add(new Wire(relation, parent));
    	
    	// create grandparent node
    	Node grandparent = Network.buildMolecularNode(wires, testCF);
    	
    	// return the network
    	Object[] result = {relation, testCF, base, parent, grandparent};
    	return result;
    }
    
    private Object[] buildNodesForCFResUnitPathTesting() throws NotAPropositionNodeException, NodeNotFoundInNetworkException, IllegalIdentifierException, CannotBuildNodeException, EquivalentNodeException, CaseFrameMissMatchException, SemanticNotFoundInNetworkException {
    	// create relations
    	Relation relation = Network.defineRelation("relation", semanticType);
    	LinkedList<Relation> relations = new LinkedList<Relation>();
    	relations.add(relation);
    	
    	// define case frames
    	CaseFrame testCF = Network.defineCaseFrame(semanticType, relations);
    	relations = new LinkedList<Relation>();
    	relations.add(relation);
    	relations.add(Network.defineRelation("relation2", "Proposition"));
    	CaseFrame otherCF = Network.defineCaseFrame(semanticType, relations);
    	
    	// build base node
    	Node base = Network.buildBaseNode("b", semantic);
    	
    	// create wires
    	ArrayList<Wire> wires = new ArrayList<Wire>();
    	wires.add(new Wire(relation, base));
    	
    	// create parent node
    	Node parent = Network.buildMolecularNode(wires, testCF);
    	
    	Object[] result = {relation, testCF, base, parent, otherCF};
    	return result;
    }
    
    private Object[] buildNodesForUnitPathTesting() throws NotAPropositionNodeException, NodeNotFoundInNetworkException, IllegalIdentifierException, CannotBuildNodeException, EquivalentNodeException, CaseFrameMissMatchException, SemanticNotFoundInNetworkException {
    	// create relations
    	Relation relation = Network.defineRelation("relation", semanticType);
    	LinkedList<Relation> relations = new LinkedList<Relation>();
    	relations.add(relation);
    	
    	// define case frame
    	CaseFrame testCF = Network.defineCaseFrame(semanticType, relations);
    	
    	// build base node
    	Node base = Network.buildBaseNode("b", semantic);
    	
    	// create wires
    	ArrayList<Wire> wires = new ArrayList<Wire>();
    	wires.add(new Wire(relation, base));
    	
    	// create parent node
    	Node parent = Network.buildMolecularNode(wires, testCF);
    	
    	Object[] result = {relation, testCF, base, parent};
    	return result;
    }
    
}