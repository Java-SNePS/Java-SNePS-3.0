package tests;

import static org.junit.Assert.*;
import org.junit.Test;
import sneps.network.classes.setClasses.PropositionSet;

import java.util.Arrays;


public class PropositionSetTest {

    @Test
    public void testConstructor2() {
        int prop = 800;
        int [] props = new int[]{324,423,523,4200,7332,8888};
        PropositionSet set = new PropositionSet(props, prop);
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
        assertTrue(testSet.equals(set));
    }

    @Test
    public void isSubSet() {
        int [] props1 = new int[]{324,423,523,4200,7332,8888};
        int [] props2 = new int[]{324,4200,8888};
        PropositionSet superSet = new PropositionSet(props1);
        PropositionSet subSet = new PropositionSet(props2);
        assertTrue(subSet.isSubSet(superSet));

    }
}