package sneps.snebr;

import sneps.exceptions.ContextNameDoesntExist;
import sneps.exceptions.DuplicateContextNameException;
import sneps.network.PropositionNode;
import sneps.network.classes.setClasses.PropositionSet;

import java.util.HashSet;

public class Controller {
    private static String currContext = "default";
    private static ContextSet contextSet = new ContextSet(currContext);
    private static HashSet<PropositionSet> minimalNoGoods = new HashSet<PropositionSet>();

    public static Context createContext(String contextName) throws DuplicateContextNameException {
        if (contextSet.getContext(contextName) != null)
            throw new DuplicateContextNameException(contextName);

        Context c = new Context(contextName);
        contextSet.add(c);
        return c;
    }

    public static Context createContext() {
        return new Context();
    }

    public static boolean removeContext(String contextName) {
        Context c = contextSet.getContext(contextName);
        if (c == null)
            return false;

        boolean bool = c.removeName(contextName);
        return contextSet.remove(contextName) && bool;
    }

    public static Context createContext(String contextName, PropositionSet hyps) throws DuplicateContextNameException {
        if (contextSet.getContext(contextName) != null) {
            throw new DuplicateContextNameException(contextName);
        }

        // TODO: 01/04/18 check for contradiction in the hyps
        Context newContext = new Context(contextName, hyps);
        contextSet.add(newContext);
        return newContext;
    }



    public static Context addPropToContext(String contextName, PropositionNode hyp) throws ContextNameDoesntExist {
        Context oldContext =  contextSet.getContext(contextName);

        if (oldContext == null)
            throw new ContextNameDoesntExist(contextName);

        oldContext.removeName(contextName);
        PropositionSet hypSet = oldContext.getHypothesisSet();
        // TODO: 03/04/18 check for contradiction
        hypSet = new PropositionSet(hypSet.getProps(), hyp.getId());

        Context newContext = new Context(contextName, hypSet);

        contextSet.add(newContext);

        return newContext;
    }

    public static Context addPropsToContext(String contextName, PropositionSet hyps) throws ContextNameDoesntExist {
        Context oldContext =  contextSet.getContext(contextName);

        if (oldContext == null)
            throw new ContextNameDoesntExist(contextName);

        oldContext.removeName(contextName);
        PropositionSet hypSet = oldContext.getHypothesisSet();

        Context newContext = new Context(contextName, hypSet.union(hyps));

        contextSet.add(newContext);

        return newContext;

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

    public static Context addPropToCurrentContext(PropositionNode p) throws ContextNameDoesntExist {
        return addPropToContext(currContext,p);
    }

    public static Context setCurrentContext(String contextName) throws DuplicateContextNameException {
        Context context = contextSet.getContext(contextName);
        if (context == null) {
            context = createContext(contextName);
        }
        currContext = contextName;

        return  context;
    }

    public static Context getCurrentContext() {
        return contextSet.getContext(currContext);
    }

    public static void checkForContradiction(Context c){
        // TODO: 13/03/18
    }

    public static Context getContextByName(String contextName) {
        return contextSet.getContext(contextName);
    }

}
