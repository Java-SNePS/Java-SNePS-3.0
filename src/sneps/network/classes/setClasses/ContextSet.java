package sneps.network.classes.setClasses;

import sneps.snebr.Context;

import java.util.Hashtable;

public class ContextSet {

    private Hashtable<String, Context> contexts;

    public ContextSet() {
        contexts = new Hashtable<String, Context>();
    }

    public ContextSet(String name) {
        this();
        this.contexts.put(name, new Context(name));
    }

    public Context getContext(String name) {
        return contexts.get(name);
    }

    /**
     * updates the hashtable of names to the correct context moreover it ensures no duplicate contexts exists
     * @param c context to be added/merged in the contexts hashtable
    */
    public void add(Context c) {
        Context newContext = identicalContext(c); //check for a duplicate context (shares the same set of hyps)
        if (newContext != c) {
            newContext.addNames(c.getNames());
        }
        c = newContext;
        for (String name: c.getNames()) {
            contexts.put(name, c);
        }
    }

    public Context identicalContext(Context context) {
        for(Context c: contexts.values()) {
            if(c.getHypothesisSet().equals(context))
                return c;
        }
        return context;
    }

}
