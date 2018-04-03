package tests;

import org.junit.Test;
import sneps.exceptions.DuplicateContextNameException;
import sneps.network.classes.setClasses.PropositionSet;
import sneps.snebr.Context;
import sneps.snebr.ContextSet;
import sneps.snebr.Controller;

import javax.naming.ldap.Control;

import static org.junit.Assert.*;

public class ContextSetTest {

    @Test
    public void getContext() throws DuplicateContextNameException {
        ContextSet set = new ContextSet();
        Context context = Controller.createContext("test context", new PropositionSet(4));
        set.add(context);

        assertEquals(context, set.getContext("test context"));
    }

    @Test
    public void remove() {
    }

    @Test
    public void add() {
    }

    @Test
    public void identicalContext() {
    }
}