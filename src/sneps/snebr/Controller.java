package sneps.snebr;

import sneps.network.PropositionNode;
import sneps.network.classes.setClasses.ContextSet;

public class Controller {
    private static String currContext = "default";
    private static ContextSet contextSet = new ContextSet(currContext);

    public static Context addContext(String contextName) {
        Context c = new Context(contextSet.getContext(currContext));
        c.addName(contextName);
        contextSet.add(c);
        return c;
    }
    
    public static Context addPropToContext(PropositionNode p, String contextName) {
        Context oldContext =  contextSet.getContext(contextName);
        Context newContext;

        if (oldContext != null) {
            oldContext.removeName(contextName);
            newContext = new Context(oldContext.getHypothesisSet());
            newContext.addName(contextName);
            contextSet.add(newContext);
        } else {
            newContext = new Context(contextSet.getContext(currContext).getHypothesisSet());
            newContext = newContext.addProp(p);
            newContext.addName(contextName);
            contextSet.add(newContext);
        }

        return newContext;
    }

//    public static Context addPropToContext(PropositionNode p, Context context) {
//        updateContextSet(c);
//        return c;
//    }

    public Context addPropToCurrentContext(PropositionNode p) {
        return addPropToContext(p, currContext);
    }

    public Context setCurrentContext(String contextName) {
        currContext = contextName;
        Context context =  contextSet.getContext(contextName);
        if (context == null) {
            context = addContext(contextName);
        }
        return  context;
    }

    public Context getCurrentContext() {
        return contextSet.getContext(currContext);
    }

    public static void checkForContradiction(Context c){
        // TODO: 13/03/18
    }

}
