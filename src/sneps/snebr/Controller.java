package sneps.snebr;

import sneps.network.PropositionNode;
import sneps.network.classes.setClasses.ContextSet;

public class Controller {
    private static String currContext = "default";
    private static ContextSet contextSet = new ContextSet(currContext);

    public static void addContext(String contextName) {
        Context c = new Context(contextSet.getContext(currContext));
        contextSet.add(c);
    }
}
