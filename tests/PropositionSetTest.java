package tests;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import sneps.exceptions.*;
import sneps.network.Network;
import sneps.network.classes.Semantic;
import sneps.network.classes.setClasses.PropositionSet;
import sneps.snebr.Controller;


public class PropositionSetTest {

 private static final Semantic semantic = new Semantic("Proposition");


    @BeforeClass
    public static void setUp() throws NotAPropositionNodeException, NodeNotFoundInNetworkException {
        for (int i = 0; i < 8889; i++)
            Network.buildBaseNode("n"+i, semantic);
    }

    @AfterClass
    public static void tearDown() {
        Network.clearNetwork();
        Controller.clearSNeBR();
    }

    @Test
    public void testAdd() throws NotAPropositionNodeException, CustomException, DuplicatePropositionException, NodeNotFoundInNetworkException {
        int prop = 800;
        int [] props = new int[]{324,423,523,4200,7332,8888};
        PropositionSet set = new PropositionSet(props);
        int [] testProps = new int[props.length + 1];
        for (int i = 0, j = 0 ; i < props.length; i++, j++) {
            if (j== 3) {
                testProps[j++] = prop;
                testProps[j] = props[i];
            } else {
                testProps[j] = props[i];
            }
        }
        PropositionSet testSet = new PropositionSet(testProps);
        assertTrue(testSet.equals(set.add(prop)));
    }

    @Test
    public void testRemoveDuplicates() {
        int [] testArr = new int [] {1,2,3,3,3,4,5,6,7,7};
        assertArrayEquals(PropositionSet.removeDuplicates(testArr), new int[]{1,2,3,4,5,6,7});
    }

    @Test
    public void testConstructorWithDuplicateThrowsException() {
        int prop = 523;
        int [] props = new int[]{324,423,523,4200,7332,8888};
        try {
            PropositionSet set = new PropositionSet(props);
            set.add(prop);
            fail("should throw exception");
        } catch (DuplicatePropositionException e) {

        } catch (NotAPropositionNodeException e) {
            e.printStackTrace();
        } catch (NodeNotFoundInNetworkException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void isSubSet() throws NotAPropositionNodeException, NodeNotFoundInNetworkException {
        int [] props1 = new int[]{324,423,523,4200,7332,8888};
        int [] props2 = new int[]{324,4200,8888};
        PropositionSet superSet = new PropositionSet(props1);
        PropositionSet subSet = new PropositionSet(props2);
        assertTrue(subSet.isSubSet(superSet));
        assertFalse(superSet.isSubSet(subSet));

    }

    @Test
    public void union() throws NotAPropositionNodeException, CustomException, NodeNotFoundInNetworkException {
        PropositionSet first = new PropositionSet(new int[] {1,2,3,4,5,6});
        PropositionSet second = new PropositionSet(new int[] {2,4,7,9,10});
        PropositionSet expected = new PropositionSet(new int[] {1,2,3,4,5,6,7,9,10});

        assertArrayEquals(PropositionSet.getPropsSafely(expected), PropositionSet.getPropsSafely(first.union(second)));
    }

    @Test
    public void remove() throws NodeNotFoundInPropSetException, NotAPropositionNodeException, NodeNotFoundInNetworkException {
        PropositionSet set = new PropositionSet(new int[] {1,2,3,4,5,6});
        PropositionSet actual = set.remove(3);
        PropositionSet expected = new PropositionSet(new int[] {1,2,4,5,6});

        assertArrayEquals(PropositionSet.getPropsSafely(expected), PropositionSet.getPropsSafely(actual));
    }

    @Test
    public void removeThrowsNotFoundException() throws NotAPropositionNodeException, NodeNotFoundInNetworkException {
        PropositionSet set = new PropositionSet(new int[] {1,2,3,4,5,6});
        try {
            set.remove(9);
            fail("Exception not thrown!");
        } catch (NodeNotFoundInPropSetException e) {

        }
    }

    @Test
    public void removeHypsTest() throws NotAPropositionNodeException, NodeNotFoundInNetworkException {
        PropositionSet set = new PropositionSet(new int[] {1,2,3,4,5,6});
        PropositionSet newSet = set.removeProps(new PropositionSet(new int[] {3,4,5}));
        assertArrayEquals(PropositionSet.getPropsSafely(newSet), new int [] {1,2,6});
    }
}
