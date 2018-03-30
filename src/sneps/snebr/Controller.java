package sneps.snebr;

import sneps.network.PropositionNode;
import sneps.network.classes.setClasses.ContextSet;
import sneps.network.classes.setClasses.PropositionSet;

import java.util.HashSet;

public class Controller {
    private static String currContext = "default";
    private static ContextSet contextSet = new ContextSet(currContext);
    private static HashSet<PropositionSet> minimalNoGoods = new HashSet<PropositionSet>();


    public static Context addContext(String contextName) {
        Context c = new Context(contextName, contextSet.getContext(currContext));
        contextSet.add(c);
        return c;
    }

    public static Context addPropToContext(PropositionNode hyp, String contextName) {
        Context oldContext =  contextSet.getContext(contextName);
        Context newContext;

        if (oldContext != null) {
            oldContext.removeName(contextName);
            PropositionSet oldHypSet = oldContext.getHypothesisSet();
            PropositionSet hypSet = new PropositionSet(oldHypSet.getProps(), hyp.getId());
            newContext = new Context(contextName, hypSet);
        } else {
            PropositionSet currHypSet = contextSet.getContext(currContext).getHypothesisSet();
            PropositionSet hypSet = new PropositionSet(currHypSet.getProps(), hyp.getId());
            newContext = new Context(contextName, hypSet);
        }
        contextSet.add(newContext);

        return newContext;
    }

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
