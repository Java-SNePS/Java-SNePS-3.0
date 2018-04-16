package tests;

import static org.junit.Assert.*;
import org.junit.Test;
import sneps.exceptions.CustomException;
import sneps.exceptions.DuplicatePropositionException;
import sneps.exceptions.NodeNotFoundException;
import sneps.exceptions.NotAPropositionNodeException;
import sneps.network.Network;
import sneps.network.Node;
import sneps.network.classes.setClasses.PropositionSet;

import java.util.Arrays;


public class PropositionSetTest {

    @Test
    public void testAdd() throws NotAPropositionNodeException, CustomException {
        int prop = 800;
        int [] props = new int[]{324,423,523,4200,7332,8888};
        Network.buildBaseNode()
        PropositionSet set = new PropositionSet(props);
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
            PropositionSet set = new PropositionSet(props, prop);
            fail("should throw exception");
        } catch (DuplicatePropositionException e) {
        }
    }

    @Test
    public void isSubSet() {
        int [] props1 = new int[]{324,423,523,4200,7332,8888};
        int [] props2 = new int[]{324,4200,8888};
        PropositionSet superSet = new PropositionSet(props1);
        PropositionSet subSet = new PropositionSet(props2);
        assertTrue(subSet.isSubSet(superSet));

    }

    @Test
    public void union() {
        PropositionSet first = new PropositionSet(new int[] {1,2,3,4,5,6});
        PropositionSet second = new PropositionSet(new int[] {2,4,7,9,10});
        PropositionSet expected = new PropositionSet(new int[] {1,2,3,4,5,6,7,9,10});

        assertArrayEquals(PropositionSet.getPropsSafely(expected), PropositionSet.getPropsSafely(first.union(second)));
    }

    @Test
    public void remove() throws NodeNotFoundException {
        PropositionSet set = new PropositionSet(new int[] {1,2,3,4,5,6});
        PropositionSet actual = set.remove(3);
        PropositionSet expected = new PropositionSet(new int[] {1,2,4,5,6});

        assertArrayEquals(PropositionSet.getPropsSafely(expected), PropositionSet.getPropsSafely(actual));
    }

    @Test
    public void removeThrowsNotFoundException() {
        PropositionSet set = new PropositionSet(new int[] {1,2,3,4,5,6});
        try {
            set.remove(9);
            fail("Exception not thrown!");
        } catch (NodeNotFoundException e) {

        }
    }
}