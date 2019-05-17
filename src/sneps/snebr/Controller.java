package sneps.snebr;

import sneps.exceptions.*;
import sneps.network.Network;
import sneps.network.Node;
import sneps.network.PropositionNode;
import sneps.network.cables.Cable;
import sneps.network.cables.DownCable;
import sneps.network.cables.UpCable;
import sneps.network.cables.UpCableSet;
import sneps.network.classes.Semantic;
import sneps.network.classes.setClasses.NodeSet;
import sneps.network.classes.setClasses.PropositionSet;
import sneps.network.classes.term.Molecular;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

import com.sun.org.apache.bcel.internal.generic.NEWARRAY;

import oracle.jrockit.jfr.tools.ConCatRepository;

public class Controller {
    private static String currContext = "default";
    private static ContextSet contextSet = new ContextSet(currContext);
    private static ArrayList<BitSet> minimalNoGoods = new ArrayList<>();
    private static String conflictingContext = null;
    private static PropositionSet conflictingHyps;
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
    public static Context createContext(String contextName) throws DuplicateContextNameException{
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
     * Creates a new dummy Context
     *
     * @return new Context object
     */
    public static Context createDummyContext(String contextName, PropositionSet hyps) throws NotAPropositionNodeException, NodeNotFoundInNetworkException {
        return new Context(contextName, hyps);
    }

    /**
     * Clears the global data strctures of SNeBR
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
    public static Context createContext(String contextName, PropositionSet hyps) throws DuplicateContextNameException, ContradictionFoundException, NotAPropositionNodeException, NodeNotFoundInNetworkException, ContextNameDoesntExistException, DuplicatePropositionException, NodeNotFoundInPropSetException {
        if (contextSet.getContext(contextName) != null) {
            throw new DuplicateContextNameException(contextName);
        }

        Context newContext = new Context(contextName);
        contextSet.add(newContext);

        return addPropsToContext(contextName, hyps);
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

        Context temp = new Context(contextName, new PropositionSet(PropositionSet.getPropsSafely(oldContext.getHypothesisSet())));

        ArrayList<NodeSet> contradictions = checkForContradiction((PropositionNode) Network.getNodeById(hyp), temp, false);

        if (contradictions != null) {
            conflictingContext = contextName;
            conflictingHyps = new PropositionSet(new int[] {hyp});
            throw new ContradictionFoundException(contradictions);
        }

        PropositionNode node = (PropositionNode) Network.getNodeById(hyp);
        node.setHyp(true);
        PropositionSet hypSet = oldContext.getHypothesisSet().add(hyp);

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
    public static Context addPropsToContext(String contextName, PropositionSet hyps) throws ContextNameDoesntExistException, NotAPropositionNodeException, NodeNotFoundInNetworkException, ContradictionFoundException, DuplicatePropositionException, NodeNotFoundInPropSetException {
        Context oldContext = contextSet.getContext(contextName);

        if (oldContext == null)
            throw new ContextNameDoesntExistException(contextName);

        oldContext.removeName(contextName);
        Context temp = new Context(contextName, new PropositionSet(PropositionSet.getPropsSafely(oldContext.getHypothesisSet())));
        ArrayList<NodeSet> contradictions;
        int[] hypsArr = PropositionSet.getPropsSafely(hyps);
        for (int i = 0; i < hypsArr.length; i++) {
            checkForContradiction((PropositionNode) Network.getNodeById(hypsArr[i]), temp, true);
            temp = new Context(contextName, temp.getHypothesisSet().add(hypsArr[i]));
        }

        temp = new Context(contextName, temp.getHypothesisSet().remove(hypsArr[hypsArr.length - 1]));
        contradictions = checkForContradiction((PropositionNode) Network.getNodeById(hypsArr[hypsArr.length - 1]), temp, false);

        if (contradictions != null) {
            conflictingContext = contextName;
            conflictingHyps = hyps;
            throw new ContradictionFoundException(contradictions);
        }

        hypsArr = PropositionSet.getPropsSafely(hyps);
        for (int i = 0; i < hypsArr.length; i++) {
        	PropositionNode node = (PropositionNode) Network.getNodeById(hypsArr[i]);
        	 node.setHyp(true);
		}
        temp = new Context(contextName, oldContext.getHypothesisSet().union(hyps));
        contextSet.add(temp);
        return temp;
    }

    public static ArrayList<BitSet> getMinimalNoGoods() {
        return minimalNoGoods;
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
    public static Context addPropsToCurrentContext(PropositionSet hyps) throws ContextNameDoesntExistException, NotAPropositionNodeException, NodeNotFoundInNetworkException, ContradictionFoundException, DuplicatePropositionException, NodeNotFoundInPropSetException {
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

    /**
     * Return a string representation of a context
     * @param contextName
     * @return A string representing the context.
     */
    public static String contextToString(String contextName) {
        return "Context: " + contextName + "\n" + contextSet.getContext(contextName).getHypothesisSet().toString();
    }

    /**
     * Returns all the propositions that are asserted in the context, either directly (hyps) or indirectly (derived).
     * @return A PropositionSet containing the asserted propositions.
     */
    public static PropositionSet allAsserted() throws NotAPropositionNodeException, NodeNotFoundInNetworkException {
        PropositionSet p = new PropositionSet();
        boolean first = true;
        for (Context c : contextSet.getContexts()) {
            if (first) {
                p = new PropositionSet(PropositionSet.getPropsSafely(c.getHypothesisSet()));
                first = false;
            } else
                p = p.union(c.getHypothesisSet());
        }
        return p;
    }

    /**
     * Creates all possible combinations of the supports of two contradictory propositions.
     * @return An ArrayList of type PropositionSet containing all the combinations.
     */
    public static ArrayList<PropositionSet> combine(Collection<PropositionSet> negatingPropSupports, Collection<PropositionSet> negatedPropSupports) throws NotAPropositionNodeException, NodeNotFoundInNetworkException {
        ArrayList<PropositionSet> output = new ArrayList<>();

        for (PropositionSet negatingPropSupp : negatingPropSupports) {
            for (PropositionSet negatedPropSupp : negatedPropSupports) {
                output.add(negatingPropSupp.union(negatedPropSupp));
            }
        }
        return output;


    }

    /**
     * Given an ArrayList of BitSets of propositions it returns an ArrayList of NodeSets of PropositionNodes.
     * @return An ArrayList of type NodeSet containing the PropositionNodes of each corresponding BitSet.
     */
    public static ArrayList<NodeSet> generateNodeSetsFromBitSets(ArrayList<BitSet> conflictingHypsCollection) throws NodeNotFoundInNetworkException, DuplicatePropositionException, NotAPropositionNodeException {
        ArrayList<NodeSet> nodeSetList = new ArrayList<>();
        for (BitSet b : conflictingHypsCollection) {
            NodeSet propSet = new NodeSet();
            for (int i = b.nextSetBit(0); i != -1; i = b.nextSetBit(i + 1))
                propSet.addNode(Network.getNodeById(i));
            nodeSetList.add(propSet);
        }
        return nodeSetList;
    }

    /**
     * Checks if a negation exists given min, max, and arg up or down cables of some node.
     * This is a helper method for getConflictingHypsCollectionForNegating and getConflictingHypsCollectionForNegated.
     * @param min min cable.
     * @param max max cable.
     * @param arg arg cable.
     * @return <code>true</code> if negation exists, <code>false</code> otherwise.
     */
    public static boolean negationExists(Cable min, Cable max, Cable arg) {
        if (
                min != null && max != null && arg != null &&
                        min.getNodeSet().size() == 1 && min.getNodeSet().getNode(0).getIdentifier().equals("0") &&
                        max.getNodeSet().size() == 1 && max.getNodeSet().getNode(0).getIdentifier().equals("0") &&
                        arg.getNodeSet().size() >= 1
                )
            return true;
        else
            return false;
    }

    /**
     * Given a BitSet representation of some context's hyps this method returns all the minimalNoGoods
     * that are subsets of this context.
     * @param contextBitset The context's hyps BitSet representation.
     * @return
     */
    public static ArrayList<BitSet> getConflictingHypsFromMinimalNoGoods(BitSet contextBitset) {
        ArrayList<BitSet> conflictingHypsInContext = new ArrayList<>();
        for (BitSet bitSet : minimalNoGoods) {
            BitSet temp = (BitSet) bitSet.clone();
            temp.and(contextBitset);
            if (temp.equals(bitSet))
                conflictingHypsInContext.add(temp);
        }
        if (conflictingHypsInContext.size() > 0)
            return conflictingHypsInContext;
        return null;
    }

    /**
     * Genrate an ArrayList of BitSets from an ArrayList of PropositionSets.
     * This is a helper method for getConflictingHypsCollectionForNegating and getConflictingHypsCollectionForNegated.
     * @param propositionSetCollection ArrayList of PropositionSets.
     * @return ArrayList of BitSets, where each BitSet corresponds to a PropositionSet in propositionSetCollection.
     * @throws NotAPropositionNodeException
     * @throws NodeNotFoundInNetworkException
     */
    public static ArrayList<BitSet> generateBitSetsFromPropositionSets(Collection<PropositionSet> propositionSetCollection) throws NotAPropositionNodeException, NodeNotFoundInNetworkException {
        ArrayList<BitSet> bitSets = new ArrayList<>();
        for (PropositionSet propSet : propositionSetCollection) {
            int[] props = PropositionSet.getPropsSafely(propSet);
            BitSet temp = new BitSet();
            for (int i = 0; i < props.length; i++)
                temp.set(props[i]);
            bitSets.add(temp);
        }
        return bitSets;

    }

    /**
     * Given a negating node, it's arg downcable, and a BitSet representation of some context's hyps;
     * it first updates the minimalNoGoods with the supports of the negating proposition node and the supports of the negated proposition node.
     * @param arg Downcable having the negated node.
     * @param tempContextBitset a BitSet representation of some context's hyps with a newly asserted hyp added to it to test for contradiction.
     * @return An ArrayList of NodeSets, each having a combination of the supports, asserted in some context, of the two conflicting propsositions.
     * If not a single such combination exists <code>null</code> is returned.
     * @throws NotAPropositionNodeException
     * @throws NodeNotFoundInNetworkException
     * @throws DuplicatePropositionException
     */
    public static ArrayList<NodeSet> getConflictingHypsCollectionForNegating(PropositionNode negatingNode, DownCable arg, BitSet tempContextBitset) throws NotAPropositionNodeException, NodeNotFoundInNetworkException, DuplicatePropositionException {
        Collection<PropositionSet> negatingPropSupports = negatingNode.getAssumptionBasedSupport().values();
        Collection<PropositionSet> combinedContradictorySupports = new ArrayList<>();

        for (Node dominatedNode : arg.getNodeSet()) {
            Collection<PropositionSet> negatedNodeSupports = ((PropositionNode) dominatedNode).getAssumptionBasedSupport().values();
            combinedContradictorySupports.addAll(combine(negatingPropSupports, negatedNodeSupports));
        }

        /*                    add to minimalNoGoods  */
        Collection<BitSet> combinedContradictorySupportsBitSetCollection = generateBitSetsFromPropositionSets(combinedContradictorySupports);

//        to avoid ConcurrentModificationException
        ArrayList<BitSet> minimalNoGoodsClone = (ArrayList<BitSet>) minimalNoGoods.clone();

        for (BitSet bitSet : combinedContradictorySupportsBitSetCollection) {
            boolean intersects = false;
            for (BitSet bitSet1 : minimalNoGoodsClone) {
                BitSet temp = (BitSet) bitSet.clone();
                temp.and(bitSet1);
                if (temp.equals(bitSet)) {
                    int index = minimalNoGoods.indexOf(bitSet1);
                    if (intersects)
                        minimalNoGoods.remove(index);
                    else {
                        minimalNoGoods.remove(index);
                        minimalNoGoods.add(index, temp);
                        intersects = true;
                    }

                } else if (temp.equals(bitSet1)) {
                    intersects = true;
                    break;
                }
            }
            if (!intersects)
                minimalNoGoods.add(bitSet);
        }

        ArrayList<BitSet> conlifctingHypsInContextCollection = getConflictingHypsFromMinimalNoGoods(tempContextBitset);
        if (conlifctingHypsInContextCollection != null)
            return generateNodeSetsFromBitSets(conlifctingHypsInContextCollection);
        else
            return null;

    }

    public static ArrayList<NodeSet> getConflictingHypsCollectionForNegated(PropositionNode negatedNode, UpCable arg, BitSet tempContextBitset) throws NotAPropositionNodeException, NodeNotFoundInNetworkException, DuplicatePropositionException {
        PropositionNode negatingNode = (PropositionNode) arg.getNodeSet().getNode(0);
        Collection<PropositionSet> negatedPropSupports = negatedNode.getAssumptionBasedSupport().values();
        Collection<PropositionSet> negatingPropSupports = negatingNode.getAssumptionBasedSupport().values();

        Collection<PropositionSet> combinedContradictorySupports = combine(negatingPropSupports, negatedPropSupports);

        /*                    add to minimalNoGoods  */
        Collection<BitSet> combinedContradictorySupportsBitSetCollection = generateBitSetsFromPropositionSets(combinedContradictorySupports);

//        to avoid ConcurrentModificationException
        ArrayList<BitSet> minimalNoGoodsClone = (ArrayList<BitSet>) minimalNoGoods.clone();

        for (BitSet bitSet : combinedContradictorySupportsBitSetCollection) {
            boolean intersects = false;
            for (BitSet bitSet1 : minimalNoGoodsClone) {
                BitSet temp = (BitSet) bitSet.clone();
                temp.and(bitSet1);
                if (temp.equals(bitSet)) {
                    int index = minimalNoGoods.indexOf(bitSet1);
                    if (intersects)
                        minimalNoGoods.remove(index);
                    else {
                        minimalNoGoods.remove(index);
                        minimalNoGoods.add(index, temp);
                        intersects = true;
                    }

                }
                else if (temp.equals(bitSet1)) {
                    intersects = true;
                    break;
                }
            }
            if (!intersects)
                minimalNoGoods.add(bitSet);
        }

        ArrayList<BitSet> conlifctingHypsInContextCollection = getConflictingHypsFromMinimalNoGoods(tempContextBitset);
        if (conlifctingHypsInContextCollection != null)
            return generateNodeSetsFromBitSets(conlifctingHypsInContextCollection);
        else
            return null;
    }

    /**
     * Checks if some node's addition to a context c introduces a contradiction.
     * @param node
     * @param c
     * @param skipCache This is a boolean flag to allow skipping the cache checking stage. It is useful for testing purposes only, for now.
     * @return
     * @throws NodeNotFoundInNetworkException
     * @throws DuplicatePropositionException
     * @throws NotAPropositionNodeException
     */
    public static ArrayList<NodeSet> checkForContradiction(PropositionNode node, Context c, boolean skipCache) throws NodeNotFoundInNetworkException, DuplicatePropositionException, NotAPropositionNodeException {

        if (c.getNames().contains(conflictingContext)) {
            checkForContradictionCore(node, c, true);
            return checkForContradictionCore(node, c, false);
        }

        return checkForContradictionCore(node, c, skipCache);
    }

    public static ArrayList<NodeSet> checkForContradictionCore(PropositionNode node, Context c, boolean skipCache) throws NodeNotFoundInNetworkException, DuplicatePropositionException, NotAPropositionNodeException {

        //     add  prop supports to a clone of the context's bitset
        BitSet tempContextBitset = (BitSet) c.getHypsBitset().clone();

        Collection<PropositionSet> propsCollection = node.getAssumptionBasedSupport().values();

        for (PropositionSet propSet : propsCollection) {
            int[] props = PropositionSet.getPropsSafely(propSet);
            for (int i = 0; i < props.length; i++)
                tempContextBitset.set(props[i]);
        }

        if (!skipCache) {

            /*       First check in minimalNoGoods */

            ArrayList<BitSet> conflictingHypsInContext = getConflictingHypsFromMinimalNoGoods(tempContextBitset);

            if (conflictingHypsInContext != null)
                return generateNodeSetsFromBitSets(conflictingHypsInContext);
        }
//        else check in down cables and up cables

        /*          check in downcables          */
        if (node.getTerm() instanceof Molecular) {
            Hashtable<String, DownCable> downCables = ((Molecular) node.getTerm()).getDownCableSet().getDownCables();
            DownCable min = downCables.get("min");
            DownCable max = downCables.get("max");
            DownCable arg = downCables.get("arg");

            if (negationExists(min, max, arg)) {
                ArrayList<NodeSet> conflictingHypsInContextFromDownCables = getConflictingHypsCollectionForNegating(node, arg, tempContextBitset);
                if (conflictingHypsInContextFromDownCables != null)
                    return conflictingHypsInContextFromDownCables;
            }
        }

        /*          check in upcables            */
        UpCableSet up = node.getUpCableSet();

        if (up.getUpCables().size() > 0) {
            UpCable min = up.getUpCable("min");
            UpCable max = up.getUpCable("max");
            UpCable arg = up.getUpCable("arg");
            if (negationExists(min, max, arg)) {
                ArrayList<NodeSet> conflictingHypsInContextFromUpCables = getConflictingHypsCollectionForNegated(node, arg, tempContextBitset);
                if (conflictingHypsInContextFromUpCables != null)
                    return conflictingHypsInContextFromUpCables;
            }

        }

        return null;
    }

    /**
     * Handles the last found contradiction in the system.
     * @param hypsToBeRemoved A PropositionSet having all the hyps to be removed
     * from the contraictory context to resolve the contradiction.
     * @param ignore Boolean flag to ignore the contradiciton and adds the contradictory proposition anyway.
     * @throws NodeNotFoundInNetworkException
     * @throws NotAPropositionNodeException
     * @throws ContextNameDoesntExistException
     * @throws NodeNotFoundInPropSetException
     * @throws DuplicatePropositionException
     */
    public static void handleContradiction(PropositionSet hypsToBeRemoved, boolean ignore) throws NodeNotFoundInNetworkException, NotAPropositionNodeException, ContextNameDoesntExistException, NodeNotFoundInPropSetException, DuplicatePropositionException {
        if (ignore) {
            Context inconsistentContext = new Context(conflictingContext, contextSet.getContext(conflictingContext).getHypothesisSet().union(conflictingHyps));
            contextSet.add(inconsistentContext);
            return;
        }
        else {

            if (hypsToBeRemoved != null) {
                removeHypsFromContext(hypsToBeRemoved, conflictingContext);
                PropositionSet modifiedHyps = conflictingHyps.removeProps(hypsToBeRemoved);
                Context resolvedContext = new Context(conflictingContext, contextSet.getContext(conflictingContext).getHypothesisSet().union(modifiedHyps));
                contextSet.add(resolvedContext);
            }
            conflictingContext = null;
            conflictingHyps = null;
        }

    }

    /**
     * removes a set of hyps from a context.
     * @param hyps
     * @param contextName
     * @return
     * @throws ContextNameDoesntExistException
     * @throws NotAPropositionNodeException
     * @throws NodeNotFoundInNetworkException
     */
    public static Context removeHypsFromContext(PropositionSet hyps, String contextName) throws ContextNameDoesntExistException, NotAPropositionNodeException, NodeNotFoundInNetworkException{
        Context c = contextSet.getContext(contextName);
        if (c == null) throw new ContextNameDoesntExistException(contextName);
        PropositionSet propSet = c.getHypothesisSet().removeProps(hyps);
        c = new Context(contextName, propSet);
        return contextSet.add(c);
    /*
        c =  contextSet.add(c);
        Network.defineDefaults();
        if (conflictingContext != null && contextName == conflictingContext) {
            PropositionNode tempProp = (PropositionNode)Network.buildBaseNode("n"+ -5000, Semantic.proposition);
            if (Controller.checkForContradictionCore(tempProp, c, false) == null)
                conflictingContext = null;
            Network.removeNode(tempProp);
        }
        return c;
    */

    }

    /**
     * removes a proposition fromm all contexts. This means removing all its supports from all contexts.
     * @param node
     * @throws NodeNotFoundInPropSetException
     * @throws NotAPropositionNodeException
     * @throws NodeNotFoundInNetworkException
     */
    public static void removePropositionFromAllContexts(PropositionNode node) throws NodeNotFoundInPropSetException, NotAPropositionNodeException, NodeNotFoundInNetworkException {
        int hyp = node.getId();
        for (String contextName: contextSet.getNames()) {
            Context c = new Context(contextName, contextSet.getContext(contextName).getHypothesisSet().remove(hyp));
            contextSet.add(c);
            /*c = contextSet.add(c);
            Network.defineDefaults();
            if (conflictingContext != null && contextName == conflictingContext) {
                PropositionNode tempProp = (PropositionNode)Network.buildBaseNode("n"+ -5000, Semantic.proposition);
                if (Controller.checkForContradictionCore(tempProp, c, false) == null)
                    conflictingContext = null;
            Network.removeNode(tempProp);
            } */
        }
    }

    /**
     * Returns all the names of contexts available in the system.
     * @return
     */
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
    
    /*
     * Loops on all proposition nodes in the network, and checks them against the list of hyps in all contexts.
     * Nodes that are not hyps in any context are removed from the network
     */
    public static void k0Compression(ContextSet contextSet) throws NumberFormatException, NotAPropositionNodeException, NodeNotFoundInNetworkException, NodeCannotBeRemovedException, NodeNotFoundInPropSetException{
    	Hashtable<String, PropositionNode> propositionNodes = Network.getPropositionNodes();
    	Set<String> tmp = contextSet.getNames();
    	String[] contextIds = new String[tmp.size()];
    	Iterator<String> iter = tmp.iterator();
    	int i = 0;
    	while (iter.hasNext()) {
    	    contextIds[i] = iter.next();
    	    i++;
    	}
    	Set<String> nodeKeys = propositionNodes.keySet();
    	for(String key: nodeKeys){
        	for(int x = 0; x < contextIds.length; x++) {
        		Context tmpContext = contextSet.getContext(contextIds[x]);
        		PropositionSet currProp = new PropositionSet(propositionNodes.get(key).getId());
        		// (currProp).isSubSet(tmpContext.getHypothesisSet())
        		// (propositionNodes.get(key)).isHyp()
        		if((currProp).isSubSet(tmpContext.getHypothesisSet())){
        			break;
        		}
        		if(x == contextIds.length - 1){
        			Network.removeNode(propositionNodes.get(key));
        			removePropositionFromAllContexts(propositionNodes.get(key));
        		}
        	}
    	}
    	
    }
    
    @SuppressWarnings("unchecked")
	public static void GTrim(BaseSupportGraph G) {
    	GTrimHelper(G, G.getGraphSize());
    }
    
    @SuppressWarnings("unchecked")
	public static void GTrimHelper(BaseSupportGraph G, int previousGraphSize){
    	
    	LinkedList<LinkedList<GraphNode>> hypsList = G.getHypsAdjList();
    	LinkedList<LinkedList<GraphNode>> supportsList = G.getSupportsAdjList();
    	
    	for (Iterator iter = hypsList.iterator(); iter.hasNext();){
    		LinkedList<GraphNode> currentRow = (LinkedList<GraphNode>) iter.next();
    		if(currentRow.size() <= 1){
    			GraphNode currentNode = currentRow.peek(); // gets the first element of the list (the Node to be removed)
    			G.removeFromHypList(currentNode);
    		}
    	}
    	
    	for (Iterator iter = supportsList.iterator(); iter.hasNext();){
    		LinkedList<GraphNode> currentRow = (LinkedList<GraphNode>) iter.next();
    		if(currentRow.size() <=1){
    			GraphNode currentSupport = currentRow.peek(); // gets the first element of the list (the Support to be removed)
    			G.removeFromSupportList(currentSupport);
    		}
    		
    	}
    	
    	if(G.getGraphSize() == previousGraphSize){
    		return;
    	} else {
    		GTrimHelper(G, G.getGraphSize());
    	}
    }
    
    public static int[] noOfOccurancesInSupports(PropositionNode nodeToBeChecked) throws NotAPropositionNodeException, NodeNotFoundInNetworkException{
    	int[] result = new int[2];
    	int noOfOccurancesInJustifications = 0;
    	int noOfOccurancesInAssumptions = 0;
    	Hashtable<String, PropositionNode> propositionNodes = Network.getPropositionNodes();
    	Set<String> nodeKeySet = propositionNodes.keySet();
    	PropositionSet currNode = new PropositionSet(nodeToBeChecked.getId());
    	for(String key : nodeKeySet){
    		Hashtable<String, PropositionSet> currJustificationSupport = propositionNodes.get(key).getJustificationSupport();
    		Set<String> justsKeySet = currJustificationSupport.keySet();
    		for(String jKey : justsKeySet){
        		if(currNode.isSubSet(currJustificationSupport.get(jKey))){
        			noOfOccurancesInJustifications++;
        		}
    		}
    		Hashtable<String, PropositionSet> currAssumptionBasedSupport = propositionNodes.get(key).getAssumptionBasedSupport();
    		Set<String> assumptionsKeySet = currAssumptionBasedSupport.keySet();
    		for(String aKey : assumptionsKeySet){
        		if(currNode.isSubSet(currAssumptionBasedSupport.get(aKey))){
        			noOfOccurancesInAssumptions++;
        		}
    		}
    	}
    	
    	
    	result[0] = noOfOccurancesInJustifications;
    	result[1] = noOfOccurancesInAssumptions;
		
    	return result;
    	
    }
    
    
    public static void alphaCut(double threshold) throws NotAPropositionNodeException, NodeNotFoundInNetworkException, NodeNotFoundInPropSetException{
    	Hashtable<String, PropositionNode> propositionNodes = Network.getPropositionNodes();
    	Set<String> nodeKeySet = propositionNodes.keySet();
    	
    	for(String key : nodeKeySet){
    		int [] occs = noOfOccurancesInSupports(propositionNodes.get(key));
    		double alpha = (occs[0] / occs[1]);
    		if(alpha < threshold){
    			removeNodeFromLayeredBeliefState(propositionNodes.get(key));
    		}
    	}
    }
    
    public static void removeNodeFromLayeredBeliefState(PropositionNode toBeRemoved) throws NotAPropositionNodeException, NodeNotFoundInNetworkException, NodeNotFoundInPropSetException{
    	ArrayList<ArrayList<Integer>> parentsTree = getParentsLayered(toBeRemoved);
    	
    	for(int i = 0; i < parentsTree.size()-1; i++){ // revise -1
    		ArrayList<Integer> currentLayer = parentsTree.get(i);
    		
    			for(int j = 0; j < currentLayer.size()-1; j++) { // revise -1
    				int parentId = currentLayer.get(j);
    				PropositionNode currParent = (PropositionNode) Network.getNodeById(parentId);
    				PropositionSet currParentPropSet = new PropositionSet(parentId);
    				int toBeRemovedId = toBeRemoved.getId();
    				PropositionSet toBeRemovedPropSet = new PropositionSet(toBeRemovedId);
    				if(i == 0) {
    	   				Hashtable<String, PropositionSet> justSupp = currParent.getJustificationSupport();
        				Set<String> justKeySet = justSupp.keySet();
        				for(String justKey : justKeySet){
        					PropositionSet currJust = justSupp.get(justKey);
        					if(toBeRemovedPropSet.isSubSet(currJust)){
        						currJust.remove(toBeRemovedId); // stopped here
        						justSupp.remove(justKey); // revise
        					}
        				}
    				}
    				Hashtable<String, PropositionSet> assumpSupp = currParent.getAssumptionBasedSupport();
    				Set<String> assumpKeySet = assumpSupp.keySet();
    				for(String assumpKey : assumpKeySet){
    					PropositionSet currAssump = assumpSupp.get(assumpKey);
    					if(currParentPropSet.isSubSet(currAssump)){
    						assumpSupp.remove(assumpKey);
    					}
    				}

    			}
    	}
    }
    
    public static ArrayList<ArrayList<Integer>> getParentsLayered(PropositionNode child) throws NotAPropositionNodeException, NodeNotFoundInNetworkException{
    	ArrayList<ArrayList<Integer>> result = new ArrayList<ArrayList<Integer>>();
    	boolean parentsFound = false;
    	ArrayList<Integer> firstLayer = getDirectParents(child);
    	if(firstLayer.size() > 0) {
    		parentsFound = true;
    		result.add(firstLayer);
    	}
    	int i = 0;
    	while(parentsFound){
    		ArrayList<Integer> nextLayer = new ArrayList<Integer>();
    		ArrayList<Integer> prevLayer = result.get(i);
    		for(int j = 0; j < prevLayer.size()-1; j++){
    			PropositionNode currChild = (PropositionNode) Network.getNodeById(prevLayer.get(j));
    			concatLists(nextLayer, getDirectParents(currChild));
    		}
    		
    		if(nextLayer.size() > 0){
    			result.add(nextLayer);
    			i++;
    		} else {
    			parentsFound = false;
    		}
    	}
    	
    	return result;
    }
    
    public static ArrayList<Integer> getDirectParents(PropositionNode child) throws NotAPropositionNodeException, NodeNotFoundInNetworkException {
    	ArrayList<Integer> result = new ArrayList<Integer>();
    	Hashtable<String, PropositionNode> propositionNodes = Network.getPropositionNodes();
    	Set<String> nodesKeySet = propositionNodes.keySet();
    	PropositionSet childPropSet = new PropositionSet(child.getId());
    	
    	for(String NodeKey : nodesKeySet) {
    		PropositionNode currNode = propositionNodes.get(NodeKey);
    		Hashtable<String, PropositionSet> currJustSupp = currNode.getJustificationSupport();
    		Set<String> suppsKeySet = currJustSupp.keySet();
    		for(String suppKey : suppsKeySet) {
    			PropositionSet currSet = currJustSupp.get(suppKey);
    			if(childPropSet.isSubSet(currSet)){
    				result.add(currNode.getId());
    				break;
    			}
    		}
    	}
    	
    	
    	return result;
    }
    
    public static ArrayList<Integer> concatLists(ArrayList<Integer> l1, ArrayList<Integer> l2) {
    	
    	for(int i = 0; i< l2.size(); i++) {
    		l1.add(l2.get(i));
    	}
    	
    	return l1;
    }
    
    
    public ArrayList<ArrayList<Integer>> findTriplets(BaseSupportGraph G) {
    	
    	ArrayList<ArrayList<Integer>> result = new  ArrayList<ArrayList<Integer>>();
    	LinkedList<LinkedList<GraphNode>> hypsList = G.getHypsAdjList();
    	LinkedList<LinkedList<GraphNode>> supportsList = G.getSupportsAdjList();
    	
    	for(Iterator iter = hypsList.iterator(); iter.hasNext();){
    		ArrayList<Integer> path = new ArrayList<Integer>();
    		LinkedList<GraphNode> currentRow = (LinkedList<GraphNode>) iter.next();
    		GraphNode firstNode = currentRow.getFirst();
    		int i = 0;
    		path.add(firstNode.getGraphId());
    		for(Iterator it = currentRow.iterator(); it.hasNext();){
    			GraphNode currentSuppNode = (GraphNode) it.next();
    			if(i > 0){
    				path.add(currentSuppNode.getGraphId());
    				for (Iterator iterator = supportsList.iterator(); iterator.hasNext();) {
    					LinkedList<GraphNode> currSupportRow = (LinkedList<GraphNode>) iterator.next();
    					GraphNode currSupport = currSupportRow.getFirst();
    					if(currSupport.equals(currentSuppNode)){
    						int j = 0;
    						for (Iterator itr = currSupportRow.iterator(); itr.hasNext();){
    							GraphNode currPropNode = (GraphNode) itr.next();
    							if(j>0){
    								path.add(currPropNode.getGraphId());
    								result.add(path);
    								path.remove(path.size()-1);
    							}
    							i++;
    						}
    						path.remove(path.size()-1);
    						break;
    					}
    				}
    			}
    			i++;
    		}
    		
    	}
    	
    	for(Iterator iter = supportsList.iterator(); iter.hasNext();){
    		ArrayList<Integer> path = new ArrayList<Integer>();
    		LinkedList<GraphNode> currentRow = (LinkedList<GraphNode>) iter.next();
    		GraphNode firstNode = currentRow.getFirst();
    		int i = 0;
    		path.add(firstNode.getGraphId());
    		for(Iterator it = currentRow.iterator(); it.hasNext();){
    			GraphNode currentPropNode = (GraphNode) it.next();
    			if(i > 0){	
    				path.add(currentPropNode.getGraphId());
    				for (Iterator iterator = hypsList.iterator(); iterator.hasNext();) {
    					LinkedList<GraphNode> currPropRow = (LinkedList<GraphNode>) iterator.next();
    					GraphNode currProp = currPropRow.getFirst();
    					if(currProp.equals(currentPropNode)){
    						int j = 0;
    						for (Iterator itr = currPropRow.iterator(); itr.hasNext();){
    							GraphNode currSuppNode = (GraphNode) itr.next();
    							if(j>0){
    								path.add(currSuppNode.getGraphId());
    								result.add(path);
    								path.remove(path.size()-1);
    							}
    							i++;
    						}
    						path.remove(path.size()-1);
    						break;
    					}
    				}
    			}
    			i++;
    		}
    		
    	}
    	
		return result;
    	
    }
    
    public static ArrayList<Integer> findHittingSet(ArrayList<ArrayList<Integer>> sets) { //greedy hitting set algorithm
    	ArrayList<Integer> result = new ArrayList<Integer>();
    	
    	while(!(sets.isEmpty())){
    		int mode = mostCommonElement(sets);
        	for(int i = 0; i < sets.size(); i++) {
        		if(sets.get(i).contains(mode)) {
        			result.remove(i);
        		}
        	}
    		result.add(mode);
    	}

    	
    	return result;
    }
    
    public static int mostCommonElement(ArrayList<ArrayList<Integer>> sets){ //revise //efficiency 0
    	int maxValue = 0;
    	ArrayList<Integer> superSet = new ArrayList<Integer>();
    	for(int i = 0; i < sets.size(); i++) {
    		ArrayList<Integer> currSet = sets.get(i);
    		for(int j = 0; j < currSet.size(); j++) {
    			if(currSet.get(j) > maxValue){
    				maxValue = currSet.get(j);
    			}
    			superSet.add(currSet.get(j));
    		}
    	}
    	int[] maxVals = new int[maxValue];
    	
    	for(int i = 0; i < superSet.size(); i++){
    		int currElement = superSet.get(i);
    		for(int j = 0; j < superSet.size(); j++) {
    			int tmp = superSet.get(i);
    			if(currElement == tmp){
    				maxVals[currElement]++;
    			}
    		}
    	}
    	int maxCount = 0;
    	int max = 0;
    	for(int i = 0; i < maxVals.length; i++) {
    		if(maxVals[i] > maxCount){
    			maxCount = maxVals[i];
    			max = i;
    		}
    	}
    	
    	
    	return max;
    }

    
    public static void save(String f) throws FileNotFoundException, IOException {
    	ObjectOutputStream cos = new ObjectOutputStream(new FileOutputStream(new File(f)));
		cos.writeObject(contextSet);
		cos.close();
    }
    
    public static void load(String f) throws IOException, ClassNotFoundException {
    	ObjectInputStream cis= new ObjectInputStream(new FileInputStream(new File(f)));
    	ContextSet tempSet = (ContextSet) cis.readObject();
		Controller.contextSet = tempSet;
		cis.close();
		tempSet = null;
    }
    
    public static void main(String[] args) throws NotAPropositionNodeException, NodeNotFoundInNetworkException, IllegalIdentifierException {
    	Network net;
    	Semantic sem;
    	String semanticType = "Proposition";
    	ContextSet contexts = new ContextSet();
    	PropositionNode n0;
    	PropositionNode n1;
    	PropositionNode n2;
    	PropositionNode n3;
    	PropositionNode n4;
    	PropositionNode n5;
    	PropositionNode n6;
    	PropositionNode n7;
    	PropositionNode n8;
    	PropositionNode n9;
    	PropositionNode n10;
    	PropositionNode n11;
    	PropositionNode n12;
    	PropositionNode n13;
    	PropositionNode n14;
    	
    	sem = new Semantic(semanticType);
    	net = new Network();
    	
    	//Building Network Nodes
    	//The Network Nodes Labels and Corresponding Ids
    	net.buildBaseNode("s", sem);// 0
		net.buildBaseNode("p", sem);// 1
		net.buildBaseNode("q", sem);// 2
		net.buildBaseNode("r", sem);// 3
		net.buildBaseNode("m", sem);// 4
		net.buildBaseNode("n", sem);// 5
		net.buildBaseNode("v", sem);// 6
		net.buildBaseNode("z", sem);// 7
		net.buildBaseNode("a", sem);// 8
		net.buildBaseNode("b", sem);// 9
		net.buildBaseNode("c", sem);// 10
		net.buildBaseNode("d", sem);// 11
		net.buildBaseNode("e", sem);// 12
		net.buildBaseNode("f", sem);// 13
		net.buildBaseNode("g", sem);// 14
		
		
		//Getting the Network PropositionNodes
		 n0 = (PropositionNode) net.getNode("s");
		 n1 = (PropositionNode) net.getNode("p");
		 n2 = (PropositionNode) net.getNode("q");
		 n3 = (PropositionNode) net.getNode("r");
		 n4 = (PropositionNode) net.getNode("m");
		 n5 = (PropositionNode) net.getNode("n");
		 n6 = (PropositionNode) net.getNode("v");
		 n7 = (PropositionNode) net.getNode("z");
		 n8 = (PropositionNode) net.getNode("a");
		 n9 = (PropositionNode) net.getNode("b");
		 n10 = (PropositionNode) net.getNode("c");
		 n11 = (PropositionNode) net.getNode("d");
		 n12 = (PropositionNode) net.getNode("e");
		 n13 = (PropositionNode) net.getNode("f");
		 n14 = (PropositionNode) net.getNode("g");
		 
		 //Setting Specific Nodes to be Hyps. So that no support are needed for this node.
		 //If node is not set, it is considers as Derived node.
		 n2.setHyp(true);
		 n4.setHyp(true);
		 n5.setHyp(true);
		 n6.setHyp(true);
		 n7.setHyp(true);
		 n9.setHyp(true);
		 n10.setHyp(true);
		 n11.setHyp(true);
		 n12.setHyp(true);
		 n13.setHyp(true);
		 n14.setHyp(true);
		 
		 //BaseSupportGraph G = new BaseSupportGraph();
		 System.out.println(n1.getParentNodes());
		 System.out.println(n1.getMySupportsTree());
		//GraphNode gn0 = new GraphNode(new PropositionSet(props));
		//GraphNode gn1 = new GraphNode(new PropositionSet(props1));
		//System.out.println(gn0);
		//System.out.println(gn1);
		//System.out.println(gn0.equals(gn1));
		//PropositionSet p0 = gn0.getPropositionSet();
		//PropositionSet p1 = gn1.getPropositionSet();
		//System.out.println(p0.equals(p1));
	}
}