package sneps.snebr;

import sneps.exceptions.*;
import sneps.network.PropositionNode;
import sneps.network.classes.setClasses.PropositionSet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Set;

public class Controller {
    private static String currContext = "default";
    private static ContextSet contextSet = new ContextSet(currContext);
    private static HashSet<PropositionSet> minimalNoGoods = new HashSet<PropositionSet>();

    /**
     * Creates a new Context given its name and adds it to SNeBR's ContextSet.
     * @param contextName the name of the Context to be created
     * @return the newly Created Context object
     * @throws DuplicateContextNameException If a context with the same name exists in SNeBR's ContextSet
     */
    public static Context createContext(String contextName) throws DuplicateContextNameException {
        if (contextSet.getContext(contextName) != null)
            throw new DuplicateContextNameException(contextName);

        Context c = new Context(contextName);
        return contextSet.add(c);
    }

    /**
     * Creates a new empty Context
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
        currContext = "default";
        contextSet.add(new Context(currContext));
    }

    /**
     * Removes a context from SNeBR's ContextSet.
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
     * @param contextName name of the Context to be created
     * @param hyps the set of hyps to be asserted in this Context
     * @return the created Context
     * @throws DuplicateContextNameException if a Context with this name exists in SNeBr's ContextSet
     */
    public static Context createContext(String contextName, PropositionSet hyps) throws DuplicateContextNameException {
        if (contextSet.getContext(contextName) != null) {
            throw new DuplicateContextNameException(contextName);
        }

        // TODO: 01/04/18 check for contradiction in the hyps
        Context newContext = new Context(contextName, hyps);
        return contextSet.add(newContext);
    }

    /**
     * Asserts a hyp in an existing Context
     * @param contextName the name of the context to assert the hyp in
     * @param hyp the hyp to be asserted
     * @return a new Context object containing the old Context with the hyp asserted in it
     * @throws ContextNameDoesntExistException if no Context with this name exists in SNeBr's ContextSet
     * @throws DuplicatePropositionException if the hyp to be asserted in the Context is already asserted
     * @throws NodeNotFoundInNetworkException 
     */
    public static Context addPropToContext(String contextName, int hyp) throws ContextNameDoesntExistException, NotAPropositionNodeException, DuplicatePropositionException, NodeNotFoundInNetworkException {
        Context oldContext =  contextSet.getContext(contextName);

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
     * @param contextName the name of the context to assert the hyp in
     * @param hyps the set of hyps to be asserted
     * @return a new Context object containing the old Context with the set of hyps asserted in it
     * @throws ContextNameDoesntExistException if no Context with this name exists in SNeBr's ContextSet
     * @throws NodeNotFoundInNetworkException 
     * @throws CustomException 
     */
    public static Context addPropsToContext(String contextName, PropositionSet hyps) throws ContextNameDoesntExistException, NotAPropositionNodeException, CustomException, NodeNotFoundInNetworkException {
        Context oldContext =  contextSet.getContext(contextName);

        if (oldContext == null)
            throw new ContextNameDoesntExistException(contextName);

        oldContext.removeName(contextName);
        PropositionSet hypSet = oldContext.getHypothesisSet();

        Context newContext = new Context(contextName, hypSet.union(hyps));

        return contextSet.add(newContext);

    }

//    public static Context addPropsToContext(PropositionSet hyps, String contextName) {
//        Context oldContext =  contextSet.getContext(contextName);
//        Context newContext;
//
//        if (oldContext != null) {
//            oldContext.removeName(contextName);
//            PropositionSet oldHypSet = oldContext.getHypothesisSet();
//            newContext = new Context(contextName, oldHypSet);
//        } else {
//            PropositionSet currHypSet = contextSet.getContext(currContext).getHypothesisSet();
//            newContext = new Context(contextName, currHypSet);
//        }
//
//        for (int i = 0; i < hyps.getProps().length; i++) {
//
//        }
//    }

    /**
     * Asserts a hyp in the current Context
     * @param hyp the hyp to be asserted in the current Context
     * @return a new Context object containing the asserted hyp
     * @throws ContextNameDoesntExistException if no Context with this name exists in SNeBr's ContextSet
     * @throws DuplicatePropositionException if the hyp to be asserted in the Context is already asserted
     * @throws NodeNotFoundInNetworkException 
     */
    public static Context addPropToCurrentContext(int hyp) throws ContextNameDoesntExistException, DuplicatePropositionException, NotAPropositionNodeException, NodeNotFoundInNetworkException {
        return addPropToContext(currContext,hyp);
    }

    /**
     * Asserts a set of hyps in the current Context
     * @param hyps the set of hyps to be asserted
     * @return a new Context object containing the old Context with the set of hyps asserted in it
     * @throws ContextNameDoesntExistException if no Context with this name exists in SNeBr's ContextSet
     * @throws NodeNotFoundInNetworkException 
     * @throws CustomException 
     */
    public static Context addPropsToCurrentContext(PropositionSet hyps) throws ContextNameDoesntExistException, NotAPropositionNodeException, CustomException, NodeNotFoundInNetworkException {
        return addPropsToContext(currContext, hyps);
    }

    /**
     * Sets the current context to some Context by the Context's name
     * @param contextName the name of the Context to be set as the current Context
     * @return Context object containing the current Context
     */
    public static Context setCurrentContext(String contextName) throws DuplicateContextNameException {
        Context context = contextSet.getContext(contextName);
        if (context == null) {
            context = createContext(contextName);
        }
        currContext = contextName;

        return  context;
    }

    /**
     * Returns the current Context object
     * @return Context object of the current Context
     */
    public static Context getCurrentContext() {
        return contextSet.getContext(currContext);
    }

    public static void checkForContradiction(Context c){
        // TODO: 13/03/18
    }

    public static Set<String> getAllNamesOfContexts() {
        return contextSet.getNames();
    }

    /**
     * Returns a Context given its name
     * @param contextName the name of the Context to be returned
     * @return Context object
     */
    public static Context getContextByName(String contextName) {
        return contextSet.getContext(contextName);
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
}
