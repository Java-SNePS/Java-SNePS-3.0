package sneps.snebr;

import sneps.exceptions.ContextNameDuplicateException;
import sneps.network.PropositionNode;
import sneps.network.classes.setClasses.PropositionSet;

import java.util.HashSet;

public class Controller {
    private static String currContext = "default";
    private static ContextSet contextSet = new ContextSet(currContext);
    private static HashSet<PropositionSet> minimalNoGoods = new HashSet<PropositionSet>();

    public static Context createContext(String contextName) throws ContextNameDuplicateException {
        if (contextSet.getContext(contextName) != null)
            throw new ContextNameDuplicateException(contextName);

        Context c = new Context(contextName);
        contextSet.add(c);
        return c;
    }

    public static Context createContext() {
        return new Context();
    }

    public boolean removeContext(String contextName) {
        return contextSet.remove(contextName);
    }

    public static Context createContext(String contextName, PropositionSet hyps) throws ContextNameDuplicateException {
        if (contextSet.getContext(contextName) != null) {
            throw new ContextNameDuplicateException(contextName);
        }

        // TODO: 01/04/18 check for contradiction in the hyps
        Context newContext = new Context(contextName, hyps);
        contextSet.add(newContext);
        return newContext;
    }

    public static Context addPropToContext(String contextName, PropositionNode hyp) {
        Context oldContext =  contextSet.getContext(contextName);
        Context newContext;
        PropositionSet hypSet;

        if (oldContext != null) {
            oldContext.removeName(contextName);
        } else {
            hypSet = contextSet.getContext(currContext).getHypothesisSet();
        }
        newContext = new Context(contextName, hypSet);
        newContext = newContext.addProp(hyp);

        contextSet.add(newContext);

        return newContext;
    }

    public static Context addPropsToContext(String contextName, PropositionSet hyps) {

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

    public static Context addPropToCurrentContext(PropositionNode p) {
        return addPropToContext(p, currContext);
    }

    public static Context setCurrentContext(String contextName) {
        Context context = contextSet.getContext(contextName);
        if (context == null) {
            context = addContext(contextName);
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
