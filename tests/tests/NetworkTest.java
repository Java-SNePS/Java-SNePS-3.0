package tests;

import org.junit.*;

import sneps.exceptions.*;
import sneps.network.Network;
import sneps.network.Node;
import sneps.network.PropositionNode;
import sneps.network.VariableNode;
import sneps.network.cables.DownCable;
import sneps.network.cables.DownCableSet;
import sneps.network.classes.CaseFrame;
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
     public void testBuildMolecularNode() throws NotAPropositionNodeException, NodeNotFoundInNetworkException, IllegalIdentifierException, CannotBuildNodeException, EquivalentNodeException, CaseFrameMissMatchException {
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
     public void testBuildMolecularNodeAlreadyExists() throws CannotBuildNodeException, EquivalentNodeException, NotAPropositionNodeException, NodeNotFoundInNetworkException, IllegalIdentifierException, CaseFrameMissMatchException {
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
     public void testRelationRestrictedCaseFrameCreation() {
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
     public void testValidRelNodePairs() throws NotAPropositionNodeException, NodeNotFoundInNetworkException, IllegalIdentifierException, SemanticNotFoundInNetworkException, CannotBuildNodeException, EquivalentNodeException, CaseFrameMissMatchException {
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
}