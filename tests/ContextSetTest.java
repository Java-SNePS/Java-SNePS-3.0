package tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sneps.exceptions.CustomException;
import sneps.exceptions.DuplicateContextNameException;
import sneps.exceptions.IllegalIdentifierException;
import sneps.exceptions.NodeNotFoundInNetworkException;
import sneps.exceptions.NotAPropositionNodeException;
import sneps.network.Network;
import sneps.network.classes.Semantic;
import sneps.network.classes.setClasses.PropositionSet;
import sneps.snebr.Context;
import sneps.snebr.ContextSet;
import sneps.snebr.Controller;

import static org.junit.Assert.*;

public class ContextSetTest {
	private Context context;
	private ContextSet contextSet;
	final static String contextName = "test context";
	private final Semantic semantic = new Semantic("Proposition");

	@Before
	public void setUp() throws DuplicateContextNameException, NotAPropositionNodeException, CustomException,
			NodeNotFoundInNetworkException, IllegalIdentifierException {
		for (int i = 0; i < 8889; i++)
			Network.buildBaseNode("n" + i, semantic);
		context = Controller.createContext(contextName, new PropositionSet(new int[] { 1, 3, 4 }));
		contextSet = new ContextSet(context);
	}

	@Test
	public void getContext() {
		assertEquals(context, contextSet.getContext(contextName));
	}

	@Test
	public void remove() {
		contextSet.remove(contextName);
		assertNull(contextSet.getContext(contextName));
	}

	@Test
	public void add() throws DuplicateContextNameException, NotAPropositionNodeException, CustomException,
			NodeNotFoundInNetworkException {
		Context temp = Controller.createContext("temp context", new PropositionSet(new int[] { 34, 89 }));
		contextSet.add(temp);
		assertEquals(temp, contextSet.getContext("temp context"));
	}

	@Test
	public void identicalContext() throws DuplicateContextNameException, NotAPropositionNodeException, CustomException,
			NodeNotFoundInNetworkException {
		Context c2 = Controller.createContext("context 2", new PropositionSet(new int[] { 1, 3, 4 }));
		assertEquals(contextSet.identicalContext(c2), context);
	}

	@After
	public void removeContext() {
		Controller.removeContext(contextName);
	}

}