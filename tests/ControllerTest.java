package tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import sneps.exceptions.*;
import sneps.network.Network;
import sneps.network.PropositionNode;
import sneps.network.classes.Semantic;
import sneps.network.classes.setClasses.PropositionSet;
import sneps.snebr.Context;
import sneps.snebr.Controller;

import java.util.Arrays;
import java.util.HashSet;

public class ControllerTest {

    private static final String testContextName = "Test context";
    private static final String testContext2 = "Test context2";
    private static final Semantic semantic = new Semantic("Proposition");

    @Before
    public void setUp() throws DuplicateContextNameException, NotAPropositionNodeException, NodeNotFoundInNetworkException {
        Controller.createContext(testContextName);
        for (int i = 0; i < 8889; i++)
            Network.buildBaseNode("n"+i, semantic);
    }


    @After
    public void tearDown(){
        Controller.removeContext(testContextName);
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
    public void setCurrentContext() {
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
}
