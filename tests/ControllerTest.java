package tests;

import org.junit.*;

import static org.junit.Assert.*;

import sneps.exceptions.*;
import sneps.network.Network;
import sneps.network.PropositionNode;
import sneps.network.classes.Semantic;
import sneps.network.classes.setClasses.PropositionSet;
import sneps.snebr.Context;
import sneps.snebr.Controller;

public class ControllerTest {

    private static final String testContextName = "Test context";
    private static final String testContext2 = "Test context2";
    private static final Semantic semantic = new Semantic("PropositionNode");

    @BeforeClass
    public static void setUp() throws NotAPropositionNodeException, NodeNotFoundInNetworkException {
        for (int i = 0; i < 8889; i++)
            Network.buildBaseNode("n"+i, semantic);
    }

    @Before
    public void beforeEach() throws DuplicateContextNameException {
        Controller.createContext(testContextName);
    }

    @After
    public void afterEach() {
        Controller.clearSNeBR();
    }


    @AfterClass
    public static void tearDown(){
        Network.clearNetwork();
    }

    @Test
    public void createContext() {
    }

    @Test
    public void createNewContextWithNoHyps() throws DuplicateContextNameException {
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
    public void createContextWithHyps() throws DuplicateContextNameException, NotAPropositionNodeException, NodeNotFoundInNetworkException {
        Context expectedContext = Controller.createContext(testContext2, new PropositionSet(new int[] {1,3,4,5}));
        Context actualContext = Controller.getContextByName(testContext2);
        assertEquals(expectedContext, actualContext);
        assertArrayEquals(PropositionSet.getPropsSafely(expectedContext.getHypothesisSet()), new int [] {1,3,4,5});
        Controller.removeContext(testContext2);
    }

    @Test
    public void removeContext() throws DuplicateContextNameException {
        Controller.createContext("Test context3");
        Controller.removeContext("Test context3");
        assertNull(Controller.getContextByName("Test context3"));
    }

    @Test
    public void addSingleHypToContext() throws NodeNotFoundInNetworkException, ContextNameDoesntExistException, DuplicatePropositionException, NotAPropositionNodeException, CustomException {
        Context cxt = Controller.getContextByName(testContextName);
        int length = PropositionSet.getPropsSafely(cxt.getHypothesisSet()).length;
        Context c = Controller.addPropToContext(testContextName, 4);
        assertEquals(c, Controller.getContextByName(testContextName));
        int [] props = PropositionSet.getPropsSafely(c.getHypothesisSet());
        assertEquals(props.length, length + 1);

        boolean found = false;
        for (int i = 0; i < props.length; i++ ) {
            if(found && props[i] == 4)
                fail("multiple insertion!");
            else if (props[i] == 4)
                found = true;
        }

        if(!found)
            fail("not inserted!");

    }

    @Test
    public void addHypsToContext() throws NotAPropositionNodeException, CustomException, NodeNotFoundInNetworkException, ContextNameDoesntExistException {
        Context cxt = Controller.getContextByName(testContextName);
        int length = PropositionSet.getPropsSafely(cxt.getHypothesisSet()).length;
        Context c = Controller.addPropsToContext(testContextName, new PropositionSet(new int [] {3,4,5,6}));
        assertEquals(c, Controller.getContextByName(testContextName));
        int [] props = PropositionSet.getPropsSafely(c.getHypothesisSet());
        assertEquals(props.length, length + 4);
        assertArrayEquals(props, new int [] {3,4,5,6});
    }

    @Test
    public void addSingleHypToCurrentContext() throws DuplicatePropositionException, NotAPropositionNodeException, NodeNotFoundInNetworkException, ContextNameDoesntExistException {
        Context cxt = Controller.getContextByName("default");
        int length = PropositionSet.getPropsSafely(cxt.getHypothesisSet()).length;
        Context c = Controller.addPropToCurrentContext( 4);
        int [] props = PropositionSet.getPropsSafely(c.getHypothesisSet());
        assertEquals(props.length, length + 1);

        boolean found = false;

        for (int j = 0 ; j < props.length; j++) {
            if(found && props[j] == 4)
                fail("multiple insertion!");
            else if (props[j] == 4)
                found = true;
        }

        assertTrue(found);
    }

    @Test
    public void addHypsToCurrentContext() throws NotAPropositionNodeException, CustomException, NodeNotFoundInNetworkException, ContextNameDoesntExistException {
        Context cxt = Controller.getContextByName("default");
        int length = PropositionSet.getPropsSafely(cxt.getHypothesisSet()).length;
        Context c = Controller.addPropsToCurrentContext(new PropositionSet(new int [] {3,5,6}));
        assertEquals(c, Controller.getContextByName("default"));
        int [] props = PropositionSet.getPropsSafely(c.getHypothesisSet());
        assertEquals(props.length, length + 3);
        assertArrayEquals(props, new int [] {3,5,6});
    }

    @Test
    public void setCurrentContext() throws DuplicateContextNameException, NotAPropositionNodeException, NodeNotFoundInNetworkException {
        Controller.createContext("c6", new PropositionSet(new int [] {5,7}));
        Controller.createContext("c5", new PropositionSet(new int [] {5,7}));

        Controller.setCurrentContext("c5");
        assertEquals(Controller.getCurrentContext(), Controller.getContextByName("c5"));

    }

    @Test
    public void isAsserted() throws NotAPropositionNodeException, NodeNotFoundInNetworkException, NodeNotFoundInPropSetException, ContextNameDoesntExistException, CustomException {
        PropositionSet p = new PropositionSet(new int [] {12, 58});
        Controller.addPropsToCurrentContext(p);
        ((PropositionNode)Network.getNodeById(10)).getBasicSupport().addJustificationBasedSupport(p);
        assertTrue(Controller.getCurrentContext().isAsserted((PropositionNode)Network.getNodeById(12)));
        assertTrue(Controller.getCurrentContext().isAsserted((PropositionNode)Network.getNodeById(58)));
        assertTrue(Controller.getCurrentContext().isAsserted((PropositionNode)Network.getNodeById(10)));
        assertFalse(Controller.getCurrentContext().isAsserted((PropositionNode) Network.getNodeById(37)));
    }

    @Test
    public void isSupport() throws NotAPropositionNodeException, NodeNotFoundInNetworkException, ContextNameDoesntExistException, CustomException, NodeNotFoundInPropSetException {
        ((PropositionNode)Network.getNodeById(10)).getBasicSupport().addJustificationBasedSupport(new PropositionSet(new int [] {4,5,7}));
        PropositionSet p = new PropositionSet(new int [] {4,5,7});
        Controller.addPropsToCurrentContext(p);
        assertTrue(Controller.getCurrentContext().isSupported((PropositionNode) Network.getNodeById(10)));
        assertFalse(Controller.getCurrentContext().isAsserted((PropositionNode) Network.getNodeById(37)));
    }

    @Test
    public void allAsserted() throws NotAPropositionNodeException, NodeNotFoundInNetworkException, ContextNameDoesntExistException, CustomException, NodeNotFoundInPropSetException {
        PropositionSet p = new PropositionSet(new int [] {12, 58, 10});
        PropositionSet support = new PropositionSet(new int [] {12,58});
        PropositionSet p1 = new PropositionSet(new int [] {12, 58, 32});
        Controller.addPropsToCurrentContext(p1);
        ((PropositionNode)Network.getNodeById(10)).getBasicSupport().addJustificationBasedSupport(support);
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
    public void getNames() throws NotAPropositionNodeException, NodeNotFoundInNetworkException, DuplicateContextNameException {
        PropositionSet set = new PropositionSet(new int[] {5,6,7,8});
        for (int i = 1; i < 4; i++)
            Controller.createContext("c"+i, set);
        for (int i = 1; i < 4; i++)
            assertTrue(Controller.getAllNamesOfContexts().contains("c" + i));
    }

}
