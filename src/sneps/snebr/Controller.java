package sneps.snebr;

import sneps.exceptions.*;
import sneps.network.Network;
import sneps.network.PropositionNode;
import sneps.network.cables.DownCable;
import sneps.network.cables.DownCableSet;
import sneps.network.cables.UpCableSet;
import sneps.network.classes.setClasses.NodeSet;
import sneps.network.classes.setClasses.PropositionSet;
import sneps.network.classes.term.Molecular;

import java.util.BitSet;
import java.util.Hashtable;
import java.util.Set;

public class Controller {
    private static String currContext = "default";
    private static ContextSet contextSet = new ContextSet(currContext);
    private static Hashtable<Integer, BitSet> minimalNoGoods = new Hashtable<>();
    private static String conflictingContext = null;
    private static int conflictingHyp;
    private static boolean automaticBR = false;


    public static boolean isAutomaticBR() {
        return automaticBR;
    }

    public static void setAutomaticBR(boolean automaticBR) {
        Controller.automaticBR = automaticBR;
    }

    /**
     * Creates a new Context given its name and adds it to SNeBR's ContextSet.
     *
     * @param contextName the name of the Context to be created
     * @return the newly Created Context object
     * @throws DuplicateContextNameException If a context with the same name exists in SNeBR's ContextSet
     */
    public static Context createContext(String contextName) throws DuplicateContextNameException, ContradictionFoundException {
        if (contextSet.getContext(contextName) != null)
            throw new DuplicateContextNameException(contextName);

        Context c = new Context(contextName);
        return contextSet.add(c);
    }

    /**
     * Creates a new empty Context
     *
     * @return new Context object
     */
    public static Context createContext() {
        return new Context();
    }

    /**
     * Clears the knowledge base
     */
    public static void clearSNeBR() {
        contextSet.clear();
        minimalNoGoods.clear();
        currContext = "default";
        contextSet.add(new Context(currContext));
    }

    /**
     * Removes a context from SNeBR's ContextSet.
     *
     * @param contextName name of the context desired to be removed
     * @return <code>true</code> if a context with this name exists, <code>false</code> otherwise
     */
    public static boolean removeContext(String contextName) {
        Context c = contextSet.getContext(contextName);
        if (c == null)
            return false;

        boolean bool = c.removeName(contextName);
        return contextSet.remove(contextName) && bool;
    }

    /**
     * Creates a new Context given its name and  a set of hyps, and adds it to SNeBR's ContextSet.
     *
     * @param contextName name of the Context to be created
     * @param hyps        the set of hyps to be asserted in this Context
     * @return the created Context
     * @throws DuplicateContextNameException if a Context with this name exists in SNeBr's ContextSet
     */
    public static Context createContext(String contextName, PropositionSet hyps) throws DuplicateContextNameException, ContradictionFoundException {
        if (contextSet.getContext(contextName) != null) {
            throw new DuplicateContextNameException(contextName);
        }

        // TODO: 01/04/18 check for contradiction in the hyps
        Context newContext = new Context(contextName, hyps);
        return contextSet.add(newContext);
    }

    /**
     * Asserts a hyp in an existing Context
     *
     * @param contextName the name of the context to assert the hyp in
     * @param hyp         the hyp to be asserted
     * @return a new Context object containing the old Context with the hyp asserted in it
     * @throws ContextNameDoesntExistException if no Context with this name exists in SNeBr's ContextSet
     * @throws DuplicatePropositionException   if the hyp to be asserted in the Context is already asserted
     * @throws NodeNotFoundInNetworkException
     */
    public static Context addPropToContext(String contextName, int hyp) throws ContextNameDoesntExistException, NotAPropositionNodeException, DuplicatePropositionException, NodeNotFoundInNetworkException, ContradictionFoundException {
        Context oldContext = contextSet.getContext(contextName);

        if (oldContext == null)
            throw new ContextNameDoesntExistException(contextName);

        oldContext.removeName(contextName);
        PropositionSet hypSet = oldContext.getHypothesisSet().add(hyp);
        // TODO: 03/04/18 check for contradiction

        Context newContext = new Context(contextName, hypSet);

        return contextSet.add(newContext);
    }

    /**
     * Asserts a set of hyps in an existing Context
     *
     * @param contextName the name of the context to assert the hyp in
     * @param hyps        the set of hyps to be asserted
     * @return a new Context object containing the old Context with the set of hyps asserted in it
     * @throws ContextNameDoesntExistException if no Context with this name exists in SNeBr's ContextSet
     * @throws NodeNotFoundInNetworkException
     * @throws CustomException
     */
    public static Context addPropsToContext(String contextName, PropositionSet hyps) throws ContextNameDoesntExistException, NotAPropositionNodeException, NodeNotFoundInNetworkException, ContradictionFoundException {
        Context oldContext = contextSet.getContext(contextName);

        if (oldContext == null)
            throw new ContextNameDoesntExistException(contextName);

        oldContext.removeName(contextName);
        PropositionSet hypSet = oldContext.getHypothesisSet();

        Context newContext = new Context(contextName, hypSet.union(hyps));

        return contextSet.add(newContext);

    }

    /**
     * Asserts a hyp in the current Context
     *
     * @param hyp the hyp to be asserted in the current Context
     * @return a new Context object containing the asserted hyp
     * @throws ContextNameDoesntExistException if no Context with this name exists in SNeBr's ContextSet
     * @throws DuplicatePropositionException   if the hyp to be asserted in the Context is already asserted
     * @throws NodeNotFoundInNetworkException
     */
    public static Context addPropToCurrentContext(int hyp) throws ContextNameDoesntExistException, DuplicatePropositionException, NotAPropositionNodeException, NodeNotFoundInNetworkException, ContradictionFoundException {
        return addPropToContext(currContext, hyp);
    }

    public static String getCurrentContextName() {
        return currContext;
    }

    /**
     * Asserts a set of hyps in the current Context
     *
     * @param hyps the set of hyps to be asserted
     * @return a new Context object containing the old Context with the set of hyps asserted in it
     * @throws ContextNameDoesntExistException if no Context with this name exists in SNeBr's ContextSet
     * @throws NodeNotFoundInNetworkException
     * @throws CustomException
     */
    public static Context addPropsToCurrentContext(PropositionSet hyps) throws ContextNameDoesntExistException, NotAPropositionNodeException, CustomException, NodeNotFoundInNetworkException, ContradictionFoundException {
        return addPropsToContext(currContext, hyps);
    }

    /**
     * Sets the current context to some Context by the Context's name
     *
     * @param contextName the name of the Context to be set as the current Context
     * @return Context object containing the current Context
     */
    public static Context setCurrentContext(String contextName) throws ContradictionFoundException, ContextNameDoesntExistException {
        Context context = contextSet.getContext(contextName);
        if (context == null) {
            throw new ContextNameDoesntExistException(contextName);
        }
        currContext = contextName;

        return context;
    }

    /**
     * Returns the current Context object
     *
     * @return Context object of the current Context
     */
    public static Context getCurrentContext() {
        return contextSet.getContext(currContext);
    }

    public static String contextToString(String contextName) {
        return "Context: " + contextName + "\n" + contextSet.getContext(contextName).getHypothesisSet().toString();
    }

    public static PropositionSet allAsserted() throws NotAPropositionNodeException, NodeNotFoundInNetworkException {
        PropositionSet p = new PropositionSet();
        boolean first = true;
        for (Context c : contextSet.getContexts()) {
            if (first) {
                p  = new PropositionSet(PropositionSet.getPropsSafely(c.getHypothesisSet()));
                first = false;
            }
            else
                p = p.union(c.getHypothesisSet());
        }
        return p;
    }

    public static void checkForContradiction(int prop, Context c) throws NodeNotFoundInNetworkException, DuplicatePropositionException, NotAPropositionNodeException {

        //TODO
        //        check in minimalNoGoods
//
//        if found then contradiction
//
//        else check in upcable and down cable

        PropositionNode node = (PropositionNode) Network.getNodeById(prop);



//        if (node.getTerm() instanceof Molecular) {
//            Hashtable<String, DownCable> downCables = ((Molecular)node.getTerm()).getDownCableSet().getDownCables();
//            DownCable min = downCables.get("min");
//            DownCable max = downCables.get("max");
//            DownCable arg = downCables.get("arg");
//
//            if (min != null && max!= null && arg != null) {
//             if (min.getNodeSet().getNode(0).getIdentifier().equals("0") && max.getNodeSet().getNode(0).getIdentifier().equals(0)
//                     && )
//            }
//
//        }

        UpCableSet up = node.getUpCableSet();

//        if (up.getUpCables().l)

//
//        if found then contradiction and update minimalNoGoods
//
//        else
//
//
//
//        PropositionSet temp = c.getHypothesisSet().add(hyp);
//
//        // temp.add()
//
//
//
//        UpCableSet up = p.getUpCableSet();
//        if (up.get)
//        DownCableSet down;
//        if (p.getTerm() instanceof Molecular) {
//            down = ((Molecular)p.getTerm()).getDownCableSet();
//        }
//        {3,5}
//        {{1,3,7}, {3,5}, {}}
    }

    public static void handleContradiction(PropositionSet hypsToBeRemoved, boolean ignore) throws NodeNotFoundInNetworkException, NotAPropositionNodeException, ContextNameDoesntExistException, NodeNotFoundInPropSetException, DuplicatePropositionException {
        if (ignore) {
            Context inconsistentContext = new Context(conflictingContext, contextSet.getContext(conflictingContext).getHypothesisSet().add(conflictingHyp));
            contextSet.add(inconsistentContext);
            return;
        }

        if (hypsToBeRemoved != null) {
            removeHypsFromContext(hypsToBeRemoved, conflictingContext);
        }

    }

    public static Context removeHypsFromContext(PropositionSet hyps, String contextName) throws ContextNameDoesntExistException, NodeNotFoundInPropSetException, NotAPropositionNodeException, NodeNotFoundInNetworkException {
        Context c = contextSet.getContext(contextName);
        if (c == null) throw new ContextNameDoesntExistException(contextName);
        PropositionSet propSet = c.getHypothesisSet().removeProps(hyps);
        c = new Context(contextName, propSet);
        return contextSet.add(c);
    }


    public static Set<String> getAllNamesOfContexts() {
        return contextSet.getNames();
    }

    /**
     * Returns a Context given its name
     *
     * @param contextName the name of the Context to be returned
     * @return Context object
     */
    public static Context getContextByName(String contextName) {
        return contextSet.getContext(contextName);
    }

}
