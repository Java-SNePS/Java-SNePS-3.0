package tests;

import javafx.util.Pair;
import org.junit.*;

import static org.junit.Assert.*;

import sneps.exceptions.*;
import sneps.network.Network;
import sneps.network.Node;
import sneps.network.PropositionNode;
import sneps.network.cables.Cable;
import sneps.network.cables.DownCable;
import sneps.network.classes.Relation;
import sneps.network.classes.RelationsRestrictedCaseFrame;
import sneps.network.classes.Semantic;
import sneps.network.classes.Wire;
import sneps.network.classes.setClasses.NodeSet;
import sneps.network.classes.setClasses.PropositionSet;
import sneps.network.classes.term.Molecular;
import sneps.snebr.Context;
import sneps.snebr.Controller;
import sneps.snebr.Support;

import java.util.*;

public class ControllerTest {

    private static final String testContextName = "Test context";
    private static final String testContext2 = "Test context2";
    private PropositionNode negated, negating, negatedProp, negatingProp;
    private ArrayList<Pair> negatingProps = new ArrayList<>();

    @BeforeClass
    public static void setUp() throws IllegalIdentifierException, DuplicateContextNameException, NotAPropositionNodeException, NodeNotFoundInNetworkException {
        Controller.clearSNeBR();
        Network.clearNetwork();
    }

    @Before
    public void beforeEach() throws DuplicateContextNameException, ContradictionFoundException, NodeNotFoundInNetworkException, NotAPropositionNodeException, IllegalIdentifierException {
        Network.defineDefaults();
        for (int i = 0; i < 8889; i++)
            Network.buildBaseNode("n" + i, Semantic.proposition);
        Controller.createContext(testContextName);
    }

    @After
    public void afterEach() {
        Controller.clearSNeBR();
        negated = negating = negatedProp = negatingProp = null;
        negatingProps = new ArrayList<>();
        Network.clearNetwork();
    }


    @AfterClass
    public static void tearDown() {
        Network.clearNetwork();
    }

    @Test
    public void createContext() {
    }

    @Test
    public void createNewContextWithNoHyps() throws DuplicateContextNameException, ContradictionFoundException {
        Context expectedContext = Controller.createContext(testContext2);
        Context actualContext = Controller.getContextByName(testContext2);
        assertEquals(expectedContext, actualContext);
        Controller.removeContext(testContext2);
    }

    @Test
    public void createNewContextWithNoHypsDupThrowsException() {
        try {
            Controller.createContext(testContextName);
            fail("should throw exception");
        } catch (DuplicateContextNameException e) {

        }

        Controller.removeContext(testContextName);
    }

    @Test
    public void createContextWithHyps() throws DuplicateContextNameException, NotAPropositionNodeException, NodeNotFoundInNetworkException, ContradictionFoundException, ContextNameDoesntExistException, DuplicatePropositionException, NodeNotFoundInPropSetException {
        Context expectedContext = Controller.createContext(testContext2, new PropositionSet(new int[]{1, 3, 4, 5}));
        Context actualContext = Controller.getContextByName(testContext2);
        assertEquals(expectedContext, actualContext);
        assertArrayEquals(PropositionSet.getPropsSafely(expectedContext.getHypothesisSet()), new int[]{1, 3, 4, 5});
        Controller.removeContext(testContext2);
    }

    @Test
    public void removeContext() throws DuplicateContextNameException, ContradictionFoundException {
        Controller.createContext("Test context3");
        Controller.removeContext("Test context3");
        assertNull(Controller.getContextByName("Test context3"));
    }

    @Test
    public void addSingleHypToContext() throws NodeNotFoundInNetworkException, ContextNameDoesntExistException, DuplicatePropositionException, NotAPropositionNodeException, CustomException, ContradictionFoundException {
        Context cxt = Controller.getContextByName(testContextName);
        int length = PropositionSet.getPropsSafely(cxt.getHypothesisSet()).length;
        Context c = Controller.addPropToContext(testContextName, 4);
        assertEquals(c, Controller.getContextByName(testContextName));
        int[] props = PropositionSet.getPropsSafely(c.getHypothesisSet());
        assertEquals(props.length, length + 1);

        boolean found = false;
        for (int i = 0; i < props.length; i++) {
            if (found && props[i] == 4)
                fail("multiple insertion!");
            else if (props[i] == 4)
                found = true;
        }

        if (!found)
            fail("not inserted!");

    }

    @Test
    public void addConflictingHypToContextUtilizingCache() throws IllegalIdentifierException, NotAPropositionNodeException, CannotBuildNodeException, EquivalentNodeException, NodeNotFoundInNetworkException, NodeNotFoundInPropSetException {
        setupContradiction2();
        try {
            Controller.addPropsToContext(testContextName, new PropositionSet(new int[]{46, 48, 49}));
        } catch (ContradictionFoundException e) {
            fail();
        } catch (ContextNameDoesntExistException e) {
            fail();
        } catch (DuplicatePropositionException e) {
            fail();
        }

        try {
            Controller.addPropsToContext(testContextName, new PropositionSet(new int[]{81, 85}));
        } catch (ContextNameDoesntExistException e) {
            fail();
        } catch (ContradictionFoundException e) {
            fail();
        } catch (DuplicatePropositionException e) {
            fail();
        }

        boolean caught = false;

        try {
            Controller.addPropToContext(testContextName, 89);
        } catch (ContextNameDoesntExistException e) {
            fail();
        } catch (DuplicatePropositionException e) {
            fail();
        } catch (ContradictionFoundException e) {
            caught = true;
            e.getContradictoryHyps().contains(genNodeSetFromArrayOfIds(new int[]{46, 48, 49, 81, 85, 89}));
        }
        if (!caught)
            fail();


    }


    @Test
    public void addConflictingHypToContextWithoutUtilizingCache() throws NotAPropositionNodeException, NodeNotFoundInNetworkException, EquivalentNodeException, IllegalIdentifierException, CannotBuildNodeException, CaseFrameMissMatchException {
        setupContradiction3();
        int id = negatingProp.getId();
        try {
            Controller.addPropToContext(testContextName, 60);
        } catch (ContextNameDoesntExistException e) {
            fail();
        } catch (DuplicatePropositionException e) {
            fail();
        } catch (ContradictionFoundException e) {
            fail();
        }

        boolean caught = false;

        try {
            Controller.addPropToContext(testContextName, id);
        } catch (ContradictionFoundException e) {
            caught = true;
            e.getContradictoryHyps().contains(genNodeSetFromArrayOfIds(new int[]{60, id}));
            Controller.getMinimalNoGoods().contains(genBitSetFromArray(new int[]{60, id}));
            assertEquals(Controller.getMinimalNoGoods().size(), 3);
        } catch (ContextNameDoesntExistException e) {
            fail();
        } catch (DuplicatePropositionException e) {
            fail();
        }
        if (!caught)
            fail();
    }

    @Test
    public void addAConflictingHepRepeatedly() throws NotAPropositionNodeException, NodeNotFoundInNetworkException, EquivalentNodeException, IllegalIdentifierException, CannotBuildNodeException, ContextNameDoesntExistException, NodeNotFoundInPropSetException, DuplicatePropositionException, CannotInsertJustificationSupportException, CaseFrameMissMatchException {
        setupContradiction3();
        Controller.getMinimalNoGoods().add(genBitSetFromArray(new int[] {70, 71}));
        int id = negatingProp.getId();
        try {
            Controller.addPropToContext(testContextName, 60);
        } catch (ContextNameDoesntExistException e) {
            fail();
        } catch (DuplicatePropositionException e) {
            fail();
        } catch (ContradictionFoundException e) {
            fail();
        }

        boolean caught = false;

        try {
            Controller.addPropToContext(testContextName, id);
        } catch (ContradictionFoundException e) {
            ArrayList<NodeSet> contradictoryHyps = e.getContradictoryHyps();
            assertTrue(e.getContradictoryHyps().contains(genNodeSetFromArrayOfIds(new int[]{60, id})));
            assertTrue(Controller.getMinimalNoGoods().contains(genBitSetFromArray(new int[]{60, id})));
            assertEquals(4, Controller.getMinimalNoGoods().size());
            Controller.handleContradiction(null, true);
            try {
                Controller.addPropToContext(testContextName, 70);
            } catch (ContradictionFoundException e1) {
                assertTrue(e1.getContradictoryHyps().equals(contradictoryHyps));
                Controller.handleContradiction(null, true);
                try {
                    Controller.addPropToContext(testContextName, 71);
                } catch (ContradictionFoundException e2) {
                    contradictoryHyps = e2.getContradictoryHyps();
                    assertTrue(e2.getContradictoryHyps().contains(genNodeSetFromArrayOfIds(new int[]{60, id})));
                    assertTrue(e2.getContradictoryHyps().contains(genNodeSetFromArrayOfIds(new int[]{70, 71})));
                    PropositionNode negatingNode = createContradictoryNode((PropositionNode) Network.getNodeById(80), null);
                    Controller.handleContradiction(null, true);
                    try {
                        Controller.addPropToContext(testContextName,80);
                    } catch (ContradictionFoundException e3) {
                        assertTrue(e3.getContradictoryHyps().equals(contradictoryHyps));
                        Controller.handleContradiction(null, true);
                        try {
                            Controller.addPropToContext(testContextName, negatingNode.getId());
                        } catch (ContradictionFoundException e4) {
                            assertTrue(e4.getContradictoryHyps().contains(genNodeSetFromArrayOfIds(new int[]{60, id})));
                            assertTrue(Controller.getMinimalNoGoods().contains(genBitSetFromArray(new int[]{60, id})));
                            assertTrue(e4.getContradictoryHyps().contains(genNodeSetFromArrayOfIds(new int[]{70, 71})));
                            assertTrue(Controller.getMinimalNoGoods().contains(genBitSetFromArray(new int[]{70, 71})));
                            assertTrue(e4.getContradictoryHyps().contains(genNodeSetFromArrayOfIds(new int[]{80, negatingNode.getId()})));
                            assertTrue(Controller.getMinimalNoGoods().contains(genBitSetFromArray(new int[]{80, negatingNode.getId()})));
                            assertEquals(5, Controller.getMinimalNoGoods().size());
                            assertEquals(3, e4.getContradictoryHyps().size());
                            caught = true;

                        }
                    }
                }

            }
        } catch (ContextNameDoesntExistException e) {
            fail();
        } catch (DuplicatePropositionException e) {
            fail();
        }
        if (!caught)
            fail();
    }

    @Test
    public void addHypsToContext() throws NotAPropositionNodeException, NodeNotFoundInNetworkException, ContextNameDoesntExistException, ContradictionFoundException, DuplicatePropositionException, NodeNotFoundInPropSetException {
        Context cxt = Controller.getContextByName(testContextName);
        int length = PropositionSet.getPropsSafely(cxt.getHypothesisSet()).length;
        Context c = Controller.addPropsToContext(testContextName, new PropositionSet(new int[]{3, 4, 5, 6}));
        assertEquals(c, Controller.getContextByName(testContextName));
        int[] props = PropositionSet.getPropsSafely(c.getHypothesisSet());
        assertEquals(props.length, length + 4);
        assertArrayEquals(props, new int[]{3, 4, 5, 6});
    }

    @Test
    public void addConflictingHypsToContext() throws NotAPropositionNodeException, NodeNotFoundInNetworkException, EquivalentNodeException, IllegalIdentifierException, CannotBuildNodeException, CaseFrameMissMatchException {
        setupContradiction3();
        boolean caught = false;
        int negatingId = negatingProp.getId();
        try {
            Controller.addPropsToContext(testContextName, new PropositionSet(new int[] {60, negatingId, 62, 45, 56}));
        } catch (ContextNameDoesntExistException e) {
            fail();
        } catch (NotAPropositionNodeException e) {
            e.printStackTrace();
        } catch (NodeNotFoundInNetworkException e) {
            fail();
        } catch (ContradictionFoundException e) {
            caught = true;
            assertTrue(e.getContradictoryHyps().contains(genNodeSetFromArrayOfIds(new int[]{60, negatingId})));
            assertTrue( Controller.getMinimalNoGoods().contains(genBitSetFromArray(new int[]{60, negatingId})));
            assertEquals(Controller.getMinimalNoGoods().size(), 3);
        } catch (DuplicatePropositionException e) {
            fail();
        } catch (NodeNotFoundInPropSetException e) {
            fail();
        }
        if (!caught)
            fail();
    }

    @Test
    public void addConflictingHypsWithMultipleContradictions() throws IllegalIdentifierException, NodeNotFoundInNetworkException, CannotBuildNodeException, EquivalentNodeException, DuplicatePropositionException, NotAPropositionNodeException, CannotInsertJustificationSupportException, CaseFrameMissMatchException {
        boolean caught = false;
        try {
            PropositionSet propSet = setupContradiction4();
            assertEquals(15, Controller.getMinimalNoGoods().size());
            Controller.addPropsToContext(testContextName, propSet);
        } catch (ContextNameDoesntExistException e) {
            fail();
        } catch (ContradictionFoundException e) {

//            System.out.println(e.getContradictoryHyps());
            for (Pair p: negatingProps) {
                assertTrue(e.getContradictoryHyps().contains(genNodeSetFromArrayOfIds(new int[]{(Integer) p.getKey(), (Integer)p.getValue()})));
                assertTrue(Controller.getMinimalNoGoods().contains(genBitSetFromArray(new int[]{(int) p.getKey(), (int) p.getValue()})));
            }

            assertTrue(e.getContradictoryHyps().contains(genNodeSetFromArrayOfIds(new int []{81,85,89,46,48,49})));
            assertEquals(12, e.getContradictoryHyps().size());
            assertEquals(15, Controller.getMinimalNoGoods().size());

//            e.getContradictoryHyps().contains()
            caught = true;

        } catch (NodeNotFoundInPropSetException e) {
            fail();
        }

        if (!caught)
            fail();

    }

    @Test
    public void resolveConflictingContextWithMultipleContradictions() throws IllegalIdentifierException, NodeNotFoundInNetworkException, CannotBuildNodeException, EquivalentNodeException, NotAPropositionNodeException, DuplicatePropositionException, NodeNotFoundInPropSetException, ContextNameDoesntExistException, CannotInsertJustificationSupportException, CaseFrameMissMatchException {
        boolean caught = false;
        PropositionSet propSet = setupContradiction4();
        try {
            Controller.addPropsToContext(testContextName, propSet);
        } catch (ContextNameDoesntExistException e) {
           fail();
        } catch (ContradictionFoundException e) {
            caught = true;
        }

       if (!caught)
           fail();

        System.out.println(negatingProps);

        PropositionSet remove = new PropositionSet();

        for (int i = 50; i <= 60; i++) {
            if (i % 2 == 0)
                remove = remove.add(i);
            else
                remove = remove.add((Integer) negatingProps.get(i - 50).getValue());
        }

        remove = remove.add(81);
        Controller.handleContradiction(remove, false);

        PropositionSet expected = new PropositionSet(new int [] {8800,8802});
        for (int i = 50; i <= 60; i++) {
            if (i % 2 != 0)
                expected = expected.add(i);
            else
                expected = expected.add((Integer) negatingProps.get(i - 50).getValue());
        }

        Controller.getContextByName(testContextName).getHypothesisSet().equals(
                expected.union(
                        new PropositionSet(new int[] {85,89,46,48,49})
                )
        );
    }


    @Test
    public void addSingleHypToCurrentContext() throws DuplicatePropositionException, NotAPropositionNodeException, NodeNotFoundInNetworkException, ContextNameDoesntExistException, ContradictionFoundException {
        Context cxt = Controller.getContextByName("default");
        int length = PropositionSet.getPropsSafely(cxt.getHypothesisSet()).length;
        Context c = Controller.addPropToCurrentContext(4);
        int[] props = PropositionSet.getPropsSafely(c.getHypothesisSet());
        assertEquals(props.length, length + 1);

        boolean found = false;

        for (int j = 0; j < props.length; j++) {
            if (found && props[j] == 4)
                fail("multiple insertion!");
            else if (props[j] == 4)
                found = true;
        }

        assertTrue(found);
    }



    @Test
    public void handleConflictingHypResolve() throws IllegalIdentifierException, NotAPropositionNodeException, CannotBuildNodeException, EquivalentNodeException, NodeNotFoundInNetworkException, NodeNotFoundInPropSetException, ContextNameDoesntExistException, ContradictionFoundException, DuplicatePropositionException {
        setupContradiction2();
        Controller.addPropsToContext(testContextName, new PropositionSet(new int[]{46, 48, 49}));
        Controller.addPropsToContext(testContextName, new PropositionSet(new int[]{81, 85}));


        try {
            Controller.addPropToContext(testContextName, 89);
        } catch (ContextNameDoesntExistException e) {
            fail();
        } catch (DuplicatePropositionException e) {
            fail();
        } catch (ContradictionFoundException e) {
            Controller.handleContradiction(new PropositionSet(new int[] {46}), false);
            Controller.getContextByName(testContextName).getHypothesisSet().equals(new PropositionSet(new int[]{48,49,81,85}));

            try {
                Controller.addPropToContext(testContextName, 30);
            } catch (ContradictionFoundException e2) {
                fail();
            }
        }


    }

    @Test
    public void handleConflictingHypIgnore() throws IllegalIdentifierException, NotAPropositionNodeException, CannotBuildNodeException, EquivalentNodeException, NodeNotFoundInNetworkException, NodeNotFoundInPropSetException, ContextNameDoesntExistException, ContradictionFoundException, DuplicatePropositionException {
        setupContradiction2();
        Controller.addPropsToContext(testContextName, new PropositionSet(new int[]{46, 48, 49}));
        Controller.addPropsToContext(testContextName, new PropositionSet(new int[]{81, 85}));


        try {
            Controller.addPropToContext(testContextName, 89);
        } catch (ContextNameDoesntExistException e) {
            fail();
        } catch (DuplicatePropositionException e) {
            fail();
        } catch (ContradictionFoundException e) {
            Controller.handleContradiction(null, true);
            Controller.getContextByName(testContextName).getHypothesisSet().equals(new PropositionSet(new int[]{46,48,49,81,85}));
            boolean caught = false;
            try {
                Controller.addPropToContext(testContextName, 30);
            } catch (ContradictionFoundException e2) {
                caught = true;
            }
            if (!caught)
                fail("Should throw ContradictionFoundException after asserting a new Prop when an earlier contradiction is ignored");
        }
    }

    @Test
    public void addHypsToCurrentContext() throws NotAPropositionNodeException, CustomException, NodeNotFoundInNetworkException, ContextNameDoesntExistException, ContradictionFoundException, DuplicatePropositionException, NodeNotFoundInPropSetException {
        Context cxt = Controller.getContextByName("default");
        int length = PropositionSet.getPropsSafely(cxt.getHypothesisSet()).length;
        Context c = Controller.addPropsToCurrentContext(new PropositionSet(new int[]{3, 5, 6}));
        assertEquals(c, Controller.getContextByName("default"));
        int[] props = PropositionSet.getPropsSafely(c.getHypothesisSet());
        assertEquals(props.length, length + 3);
        assertArrayEquals(props, new int[]{3, 5, 6});
    }

    @Test
    public void removeHypsFromCotnextTest() throws NotAPropositionNodeException, NodeNotFoundInNetworkException, ContextNameDoesntExistException, NodeNotFoundInPropSetException, DuplicatePropositionException, ContradictionFoundException {
        Controller.addPropsToContext(testContextName, new PropositionSet(new int[]{1, 2, 3, 4, 5, 6, 7, 8}));

        Controller.removeHypsFromContext(new PropositionSet(new int[]{5, 6, 7}), testContextName);

        assertTrue(Controller.getContextByName(testContextName).getHypothesisSet().equals(new PropositionSet(new int[]{1, 2, 3, 4, 8})));

    }

    @Test
    public void setCurrentContext() throws DuplicateContextNameException, NotAPropositionNodeException, NodeNotFoundInNetworkException, ContextNameDoesntExistException, ContradictionFoundException, DuplicatePropositionException, NodeNotFoundInPropSetException {
        Controller.createContext("c6", new PropositionSet(new int[]{5, 7}));
        Controller.createContext("c5", new PropositionSet(new int[]{5, 7}));

        Controller.setCurrentContext("c5");
        assertEquals(Controller.getCurrentContext(), Controller.getContextByName("c5"));

    }

    @Test
    public void isAsserted() throws NotAPropositionNodeException, NodeNotFoundInNetworkException, NodeNotFoundInPropSetException, ContextNameDoesntExistException, CustomException, ContradictionFoundException, DuplicatePropositionException, CannotInsertJustificationSupportException {
        PropositionSet p = new PropositionSet(new int[]{12, 58});
        Controller.addPropsToCurrentContext(p);
        ((PropositionNode) Network.getNodeById(10)).getBasicSupport().addJustificationBasedSupport(p);
        assertTrue(Controller.getCurrentContext().isAsserted((PropositionNode) Network.getNodeById(12)));
        assertTrue(Controller.getCurrentContext().isAsserted((PropositionNode) Network.getNodeById(58)));
        assertTrue(Controller.getCurrentContext().isAsserted((PropositionNode) Network.getNodeById(10)));
        assertFalse(Controller.getCurrentContext().isAsserted((PropositionNode) Network.getNodeById(37)));
    }

    @Test
    public void isSupport() throws NotAPropositionNodeException, NodeNotFoundInNetworkException, ContextNameDoesntExistException, CustomException, NodeNotFoundInPropSetException, ContradictionFoundException, DuplicatePropositionException, CannotInsertJustificationSupportException {
        ((PropositionNode) Network.getNodeById(10)).getBasicSupport().addJustificationBasedSupport(new PropositionSet(new int[]{4, 5, 7}));
        PropositionSet p = new PropositionSet(new int[]{4, 5, 7});
        Controller.addPropsToCurrentContext(p);
        assertTrue(Controller.getCurrentContext().isSupported((PropositionNode) Network.getNodeById(10)));
        assertFalse(Controller.getCurrentContext().isAsserted((PropositionNode) Network.getNodeById(37)));
    }

    @Test
    public void allAsserted() throws NotAPropositionNodeException, NodeNotFoundInNetworkException, ContextNameDoesntExistException, CustomException, NodeNotFoundInPropSetException, DuplicatePropositionException, ContradictionFoundException, CannotInsertJustificationSupportException {
        PropositionSet p = new PropositionSet(new int[]{12, 58, 10});
        PropositionSet support = new PropositionSet(new int[]{12, 58});
        PropositionSet p1 = new PropositionSet(new int[]{12, 58, 32});
        Controller.addPropsToCurrentContext(p1);
        ((PropositionNode) Network.getNodeById(10)).getBasicSupport().addJustificationBasedSupport(support);
        assertTrue(p.isSubSet(Controller.getCurrentContext().allAsserted()));
    }

    @Test
    public void getCurrentContext() {
    }

    @Test
    public void checkForContradiction() {
    }

    @Test
    public void getContextByName() {
    }

    @Test
    public void getNames() throws NotAPropositionNodeException, NodeNotFoundInNetworkException, DuplicateContextNameException, ContradictionFoundException, ContextNameDoesntExistException, DuplicatePropositionException, NodeNotFoundInPropSetException {
        PropositionSet set = new PropositionSet(new int[]{5, 6, 7, 8});
        for (int i = 1; i < 4; i++)
            Controller.createContext("c" + i, set);
        for (int i = 1; i < 4; i++)
            assertTrue(Controller.getAllNamesOfContexts().contains("c" + i));
    }

    @Test
    public void combine() throws NotAPropositionNodeException, NodeNotFoundInNetworkException {
        ArrayList<PropositionSet> propSet = new ArrayList<>();
        propSet.add(new PropositionSet(new int[]{1, 3, 5}));
        propSet.add(new PropositionSet(new int[]{4, 6, 7}));

        ArrayList<PropositionSet> negation = new ArrayList<>();
        negation.add(new PropositionSet(new int[]{9, 23, 53}));
        negation.add(new PropositionSet(new int[]{24, 61, 72}));

        ArrayList<PropositionSet> combination = Controller.combine(propSet, negation);

        LinkedList<Integer[]> expectedCombos = new LinkedList<>();
        expectedCombos.add(new Integer[]{1, 3, 5, 9, 23, 53});
        expectedCombos.add(new Integer[]{1, 3, 5, 24, 61, 72});

        expectedCombos.add(new Integer[]{4, 6, 7, 9, 23, 53});
        expectedCombos.add(new Integer[]{4, 6, 7, 24, 61, 72});


        for (PropositionSet p : combination) {
            assertTrue(equateIntegerArrWithInt(expectedCombos.pop(), PropositionSet.getPropsSafely(p)));
        }
    }


    @Test
    public void generateNodeSetsFromBitSetsTest() throws NodeNotFoundInNetworkException, DuplicatePropositionException, NotAPropositionNodeException {
        ArrayList<BitSet> bitSetsCollection = new ArrayList<>();
        BitSet bitSet1 = new BitSet();
        bitSet1.set(1, 3);
        bitSet1.set(4);

        BitSet bitSet2 = new BitSet();
        bitSet2.set(0, 3);
        bitSet2.set(4);
        bitSet2.set(6);

        bitSetsCollection.add(bitSet1);
        bitSetsCollection.add(bitSet2);

        ArrayList<NodeSet> nodeSetArrayList = Controller.generateNodeSetsFromBitSets(bitSetsCollection);

        NodeSet nodeSet1 = genNodeSetFromArrayOfIds(new int[]{1, 2, 4});
        NodeSet nodeSet2 = genNodeSetFromArrayOfIds(new int[]{0, 1, 2, 4, 6});

        assertTrue(nodeSetArrayList.get(0).equals(nodeSet1));

        assertTrue(nodeSetArrayList.get(1).equals(nodeSet2));

    }

    @Test
    public void negationShouldExist() throws NodeNotFoundInNetworkException, NotAPropositionNodeException, IllegalIdentifierException {
        NodeSet x = new NodeSet();
        x.addNode(Network.getNodeById(4));
        Cable arg = new DownCable(Relation.arg, x);
        NodeSet y = new NodeSet();
        Node zero = Network.buildBaseNode("0", Semantic.individual);
        y.addNode(zero);
        Cable min = new DownCable(Relation.min, y);

        Cable max = new DownCable(Relation.max, y);

        assertTrue(Controller.negationExists(min, max, arg));

    }

    @Test
    public void negationShouldNotExist() throws NodeNotFoundInNetworkException, NotAPropositionNodeException, IllegalIdentifierException {
        NodeSet x = new NodeSet();
        x.addNode(Network.getNodeById(4));
        Cable arg = new DownCable(Relation.arg, x);
        NodeSet y = new NodeSet();
        Node zero = Network.buildBaseNode("0", new Semantic("Identifier"));
        y.addNode(zero);
        Node one = Network.buildBaseNode("1", new Semantic("Identifier"));

        NodeSet z = new NodeSet();
        z.addNode(one);

        Cable min = new DownCable(Relation.min, y);

        Cable max = new DownCable(Relation.max, z);

        assertFalse(Controller.negationExists(min, max, arg));
    }

    @Test
    public void getConflictingHypsFromMinimalNoGoodsTest() {

        BitSet conetextBitSet = new BitSet();
        conetextBitSet.set(300, 310);
        conetextBitSet.set(340, 346);
        ArrayList<BitSet> minimalNoGoods = Controller.getMinimalNoGoods();
        BitSet bitset1 = new BitSet();
        bitset1.set(345);
        bitset1.set(301, 303);

        BitSet bitSet2 = new BitSet();
        bitSet2.set(301);
        bitSet2.set(380);
        bitSet2.set(420);

        minimalNoGoods.add(bitset1);
        minimalNoGoods.add(bitSet2);

        ArrayList<BitSet> bitSetArrayList = Controller.getConflictingHypsFromMinimalNoGoods(conetextBitSet);

        ArrayList<BitSet> expectedBitSetCollection = new ArrayList<>();

        BitSet expectedBitSet1 = (BitSet) bitset1.clone();

        assertTrue(bitSetArrayList.size() == 1);
        assertTrue(bitSetArrayList.get(0).equals(expectedBitSet1));
    }

    @Test
    public void generateBitSetsFromPropositionSetsTest() throws NotAPropositionNodeException, NodeNotFoundInNetworkException {
        ArrayList<PropositionSet> propositionSetArrayList = new ArrayList<>();
        int[] arr1 = new int[]{1, 9, 10, 246};
        int[] arr2 = new int[]{23, 12, 34};

        propositionSetArrayList.add(new PropositionSet(arr1));
        propositionSetArrayList.add(new PropositionSet(arr2));

        ArrayList<BitSet> expectedBitSetArrayList = new ArrayList<>();
        expectedBitSetArrayList.add(genBitSetFromArray(arr1));
        expectedBitSetArrayList.add(genBitSetFromArray(arr2));

        ArrayList<BitSet> actualBitSetArrayList = Controller.generateBitSetsFromPropositionSets(propositionSetArrayList);

        assertTrue(actualBitSetArrayList.get(0).equals(expectedBitSetArrayList.get(0)));
        assertTrue(actualBitSetArrayList.get(0).equals(expectedBitSetArrayList.get(0)));
    }

    @Test
    public void getConflictingHypsCollectionForNegatingTest() throws NodeNotFoundInNetworkException, NotAPropositionNodeException, NodeNotFoundInPropSetException, IllegalIdentifierException, CannotBuildNodeException, EquivalentNodeException, DuplicatePropositionException, CannotInsertJustificationSupportException, CaseFrameMissMatchException {

        setupContradiction();
        BitSet temp = genBitSetFromArray(new int[]{40, 43, 46, 48, 49, 81, 85, 89});

        ArrayList<BitSet> minimalNoGoods = Controller.getMinimalNoGoods();

        ArrayList<NodeSet> expectedNodeSetsArrayList = Controller.getConflictingHypsCollectionForNegating(negating,
                ((Molecular) negating.getTerm()).getDownCableSet().getDownCable("arg"),
                temp);

        assertEquals(1, expectedNodeSetsArrayList.size());

        assertTrue(expectedNodeSetsArrayList.get(0).equals(genNodeSetFromArrayOfIds(new int[]{46, 48, 49, 81, 85, 89})));

        assertEquals(3, minimalNoGoods.size());
        assertTrue(minimalNoGoods.contains(genBitSetFromArray(new int[]{1, 4, 6})));
        assertTrue(minimalNoGoods.contains(genBitSetFromArray(new int[]{46, 48, 49, 81, 85, 89})));
        assertTrue(minimalNoGoods.contains(genBitSetFromArray(new int[]{46, 48, 49, 60, 64, 75, 78})));

    }

    @Test
    public void getConflictingHypsCollectionForNegatedTest() throws NodeNotFoundInNetworkException, NotAPropositionNodeException, DuplicatePropositionException, NodeNotFoundInPropSetException, CannotBuildNodeException, EquivalentNodeException, IllegalIdentifierException, CannotInsertJustificationSupportException, CaseFrameMissMatchException {

        setupContradiction();

        BitSet temp = genBitSetFromArray(new int[]{40, 43, 46, 48, 49, 81, 85, 89});

        ArrayList<BitSet> minimalNoGoods = Controller.getMinimalNoGoods();

        ArrayList<NodeSet> expectedNodeSetsArrayList = Controller.getConflictingHypsCollectionForNegated(negated,
                negated.getTerm().getUpCableSet().getUpCable("arg"),
                temp);

        assertEquals(1, expectedNodeSetsArrayList.size());

        assertTrue(expectedNodeSetsArrayList.get(0).equals(genNodeSetFromArrayOfIds(new int[]{46, 48, 49, 81, 85, 89})));

        assertEquals(3, minimalNoGoods.size());
        assertTrue(minimalNoGoods.contains(genBitSetFromArray(new int[]{1, 4, 6})));
        assertTrue(minimalNoGoods.contains(genBitSetFromArray(new int[]{46, 48, 49, 81, 85, 89})));
        assertTrue(minimalNoGoods.contains(genBitSetFromArray(new int[]{46, 48, 49, 60, 64, 75, 78})));
    }

    @Test
    public void checkForContradictionTest() throws IllegalIdentifierException, NotAPropositionNodeException, CannotBuildNodeException, EquivalentNodeException, NodeNotFoundInNetworkException, NodeNotFoundInPropSetException, DuplicatePropositionException, CannotInsertJustificationSupportException, CaseFrameMissMatchException {
        setupContradiction();
        Context c = Controller.createDummyContext("contradictoryContext", new PropositionSet(new int[]{81, 85, 89}));

        ArrayList<NodeSet> contradictoryHyps = Controller.checkForContradiction(negating, c, false);

        ArrayList<BitSet> minimalNoGoods = Controller.getMinimalNoGoods();

        assertEquals(3, minimalNoGoods.size());
        assertTrue(minimalNoGoods.contains(genBitSetFromArray(new int[]{1, 4, 6})));
        assertTrue(minimalNoGoods.contains(genBitSetFromArray(new int[]{46, 48, 49, 81, 85, 89})));
        assertTrue(minimalNoGoods.contains(genBitSetFromArray(new int[]{46, 48, 49, 60, 64, 75, 78})));
        contradictoryHyps.contains(genNodeSetFromArrayOfIds(new int[]{46, 48, 49, 81, 85, 89}));


        contradictoryHyps = Controller.checkForContradiction(negating, c, false);

        assertEquals(3, minimalNoGoods.size());
        assertTrue(minimalNoGoods.contains(genBitSetFromArray(new int[]{1, 4, 6})));
        assertTrue(minimalNoGoods.contains(genBitSetFromArray(new int[]{46, 48, 49, 81, 85, 89})));
        assertTrue(minimalNoGoods.contains(genBitSetFromArray(new int[]{46, 48, 49, 60, 64, 75, 78})));
        contradictoryHyps.contains(genNodeSetFromArrayOfIds(new int[]{46, 48, 49, 81, 85, 89}));


        c = Controller.createDummyContext("contradictoryContext", new PropositionSet(new int[]{46, 48, 49}));


        contradictoryHyps = Controller.checkForContradiction(negated, c, false);

        assertEquals(3, minimalNoGoods.size());
        assertTrue(minimalNoGoods.contains(genBitSetFromArray(new int[]{1, 4, 6})));
        assertTrue(minimalNoGoods.contains(genBitSetFromArray(new int[]{46, 48, 49, 81, 85, 89})));
        assertTrue(minimalNoGoods.contains(genBitSetFromArray(new int[]{46, 48, 49, 60, 64, 75, 78})));
        contradictoryHyps.contains(genNodeSetFromArrayOfIds(new int[]{46, 48, 49, 81, 85, 89}));


        contradictoryHyps = Controller.checkForContradiction((PropositionNode) Network.getNodeById(8030), c, false);

        assertEquals(3, minimalNoGoods.size());
        assertTrue(minimalNoGoods.contains(genBitSetFromArray(new int[]{1, 4, 6})));
        assertTrue(minimalNoGoods.contains(genBitSetFromArray(new int[]{46, 48, 49, 81, 85, 89})));
        assertTrue(minimalNoGoods.contains(genBitSetFromArray(new int[]{46, 48, 49, 60, 64, 75, 78})));
        assertNull(contradictoryHyps);

        PropositionNode negated2 = (PropositionNode) Network.getNodeById(7000);

        Node zero = Network.buildBaseNode("0", Semantic.infimum);

        ArrayList<Wire> wires = new ArrayList<>();
        wires.add(new Wire(Relation.arg, negated2));
        wires.add(new Wire(Relation.max, zero));
        wires.add(new Wire(Relation.min, zero));

        PropositionNode negating2 = (PropositionNode) Network.buildMolecularNode(wires, RelationsRestrictedCaseFrame.andOrRule);

        c = Controller.createDummyContext("contradictoryContext", new PropositionSet(new int[]{81, 7000}));

        contradictoryHyps = Controller.checkForContradiction(negating2, c, false);

        assertEquals(4, minimalNoGoods.size());
        assertTrue(minimalNoGoods.contains(genBitSetFromArray(new int[]{1, 4, 6})));
        assertTrue(minimalNoGoods.contains(genBitSetFromArray(new int[]{46, 48, 49, 81, 85, 89})));
        assertTrue(minimalNoGoods.contains(genBitSetFromArray(new int[]{46, 48, 49, 60, 64, 75, 78})));
        assertTrue(minimalNoGoods.contains(genBitSetFromArray(new int[]{7000, negating2.getId()})));
        contradictoryHyps.contains(genNodeSetFromArrayOfIds(new int[]{7000, negating2.getId()}));

        contradictoryHyps = Controller.checkForContradiction(negating2, c, false);

        assertEquals(4, minimalNoGoods.size());
        assertTrue(minimalNoGoods.contains(genBitSetFromArray(new int[]{1, 4, 6})));
        assertTrue(minimalNoGoods.contains(genBitSetFromArray(new int[]{46, 48, 49, 81, 85, 89})));
        assertTrue(minimalNoGoods.contains(genBitSetFromArray(new int[]{46, 48, 49, 60, 64, 75, 78})));
        assertTrue(minimalNoGoods.contains(genBitSetFromArray(new int[]{7000, negating2.getId()})));
        contradictoryHyps.contains(genNodeSetFromArrayOfIds(new int[]{7000, negating2.getId()}));

    }


    public BitSet genBitSetFromArray(int[] arr) {
        BitSet output = new BitSet();

        for (int i = 0; i < arr.length; i++)
            output.set(arr[i]);
        return output;
    }

    public NodeSet genNodeSetFromArrayOfIds(int[] arr) throws NodeNotFoundInNetworkException {
        NodeSet x = new NodeSet();

        for (int i = 0; i < arr.length; i++)
            x.addNode(Network.getNodeById(arr[i]));
        return x;
    }


    @Test
    public void equate() {
        assertTrue(equateIntegerArrWithInt(new Integer[]{1, 2, 3}, new int[]{1, 2, 3}));
    }

    public boolean equateIntegerArrWithInt(Integer[] arrInteger, int[] arrInt) {
        if (arrInteger.length != arrInt.length) {
            return false;
        } else {
            for (int i = 0; i < arrInt.length; i++) {
                if (arrInt[i] != arrInteger[i])
                    return false;
            }
        }
        return true;
    }

    public void setupContradiction() throws NotAPropositionNodeException, NodeNotFoundInNetworkException, EquivalentNodeException, CannotBuildNodeException, NodeNotFoundInPropSetException, IllegalIdentifierException, DuplicatePropositionException, CannotInsertJustificationSupportException, CaseFrameMissMatchException {
        negated = (PropositionNode) Network.getNodeById(80);
        negated.addJustificationBasedSupport(new PropositionSet(new int[]{60, 64, 75, 78}));
        negated.addJustificationBasedSupport(new PropositionSet(new int[]{81, 85, 89}));

        Node zero = Network.buildBaseNode("0", Semantic.infimum);

        ArrayList<Wire> wires = new ArrayList<>();
        wires.add(new Wire(Relation.arg, Network.getNodeById(80)));
        wires.add(new Wire(Relation.max, zero));
        wires.add(new Wire(Relation.min, zero));

        negating = (PropositionNode) Network.buildMolecularNode(wires, RelationsRestrictedCaseFrame.andOrRule);

        negating.addJustificationBasedSupport(new PropositionSet(new int[]{46, 48, 49}));

        ArrayList<BitSet> minimalNoGoods = Controller.getMinimalNoGoods();

        minimalNoGoods.add(genBitSetFromArray(new int[]{1, 4, 6}));
        minimalNoGoods.add(genBitSetFromArray(new int[]{46, 48, 49, 81, 85, 89, 90}));
        minimalNoGoods.add(genBitSetFromArray(new int[]{46, 48, 49, 81, 85, 89, 95}));
        minimalNoGoods.add(genBitSetFromArray(new int[]{46, 48, 49, 81, 85, 89, 95, 99}));
        minimalNoGoods.add(genBitSetFromArray(new int[]{46, 48, 49, 81, 85, 89, 97, 102}));
    }

    public void setupContradiction2() {

        ArrayList<BitSet> minimalNoGoods = Controller.getMinimalNoGoods();

        minimalNoGoods.add(genBitSetFromArray(new int[]{1, 4, 6}));

        minimalNoGoods.add(genBitSetFromArray(new int[]{46, 48, 49, 81, 85, 89}));
    }

    public void setupContradiction3() throws NodeNotFoundInNetworkException, NotAPropositionNodeException, IllegalIdentifierException, CannotBuildNodeException, EquivalentNodeException, CaseFrameMissMatchException {

        negatedProp = (PropositionNode) Network.getNodeById(60);

        Node zero = Network.buildBaseNode("0", Semantic.infimum);

        ArrayList<Wire> wires = new ArrayList<>();
        wires.add(new Wire(Relation.arg, negatedProp));
        wires.add(new Wire(Relation.max, zero));
        wires.add(new Wire(Relation.min, zero));

        negatingProp = (PropositionNode) Network.buildMolecularNode(wires, RelationsRestrictedCaseFrame.andOrRule);

        ArrayList<BitSet> minimalNoGoods = Controller.getMinimalNoGoods();

        minimalNoGoods.add(genBitSetFromArray(new int[]{1, 4, 6}));

        minimalNoGoods.add(genBitSetFromArray(new int[]{46, 48, 49, 81, 85, 89, 90}));
        minimalNoGoods.add(genBitSetFromArray(new int[]{60, negatingProp.getId(), 62}));
    }

    public PropositionSet setupContradiction4() throws NodeNotFoundInNetworkException, NotAPropositionNodeException, IllegalIdentifierException, CannotBuildNodeException, EquivalentNodeException, DuplicatePropositionException, NodeNotFoundInPropSetException, CannotInsertJustificationSupportException, CaseFrameMissMatchException {

        ArrayList<BitSet> minimalNoGoods = Controller.getMinimalNoGoods();
        minimalNoGoods.add(genBitSetFromArray(new int[]{1, 4, 6}));
        minimalNoGoods.add(genBitSetFromArray(new int[]{46, 48, 49, 81, 85, 89, 90}));
        PropositionSet output = new PropositionSet();

        output = output.add(8800);
        output = output.add(8802);

        for (int i = 50; i <= 60; i++) {

            output = output.add(i);

            PropositionNode negatingProp = createContradictoryNode((PropositionNode) Network.getNodeById(i),null);
            negatingProps.add(new Pair(i, negatingProp.getId()));

            if (i == 50)
                minimalNoGoods.add(genBitSetFromArray(new int[]{i, negatingProp.getId()}));
            else
                minimalNoGoods.add(genBitSetFromArray(new int[]{i, negatingProp.getId(), (int) ((Math.random() * 30.0) + 61)}));

            output = output.add(negatingProp.getId());
        }

        PropositionNode negatedProp = (PropositionNode) Network.getNodeById(80);
        negatedProp.addJustificationBasedSupport(new PropositionSet(new int[]{50, 95, 105}));
        negatedProp.addJustificationBasedSupport(new PropositionSet(new int[]{81, 85, 89}));

        ArrayList<PropositionSet> negatingSupports = new ArrayList<>();
        negatingSupports.add(new PropositionSet(new int[]{60, (Integer)negatingProps.get(0).getValue(), 95, 105}));
        negatingSupports.add(new PropositionSet(new int[]{46, 48, 49}));

        output = output.union(new PropositionSet(new int[]{81,85,89,46,48,49}));

        PropositionNode negatingProp = createContradictoryNode(negatedProp,negatingSupports);

        Controller.checkForContradiction(negatingProp,Controller.getContextByName(testContextName), false);

//        output = output.add(negatedProp.getId());
//        output = output.add(negatingProp.getId());

//        negatingProps.add(new Pair(negatedProp.getId(), negatingProp.getId()));

        return output;
    }

    public PropositionNode createContradictoryNode(PropositionNode negatedNode, ArrayList<PropositionSet> negatingSupports) throws NodeNotFoundInNetworkException, NodeNotFoundInPropSetException, NotAPropositionNodeException, IllegalIdentifierException, CannotBuildNodeException, EquivalentNodeException, DuplicatePropositionException, CannotInsertJustificationSupportException, CaseFrameMissMatchException {


        Node zero = Network.buildBaseNode("0", Semantic.infimum);

        ArrayList<Wire> wires = new ArrayList<>();
        wires.add(new Wire(Relation.arg, negatedNode));
        wires.add(new Wire(Relation.max, zero));
        wires.add(new Wire(Relation.min, zero));

        PropositionNode negatingProp = (PropositionNode) Network.buildMolecularNode(wires, RelationsRestrictedCaseFrame.andOrRule);

        if (negatingSupports != null) {
            for (PropositionSet support: negatingSupports)
                negatingProp.addJustificationBasedSupport(support);
        }

        return negatingProp;

    }

}