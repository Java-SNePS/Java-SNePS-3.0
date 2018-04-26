package sneps.snebr;


import java.util.Hashtable;

public class ContextSet {

    private Hashtable<String, Context> contexts;

    /**
     * Constructs an empty ContextSet
     */
    public ContextSet() {
        contexts = new Hashtable<String, Context>();
    }


    /**
     * Constructs a new ContextSet with a name of a Context that should be created.
     * @param name name of the Context to be created and added to the ContextSet.
     */
    public ContextSet(String name) {
        this();
        this.contexts.put(name, new Context(name));
    }

    /**
     * Constructs a new ContextSet with a Context
     * @param context the Context to initialize this ContextSet with
     */
    public ContextSet(Context context) {
        this();
        this.add(context);
    }

    /**
     * Returns a Context given its name
     * @param name name of the context to return
     * @return A context that is mapped to the passed name.
     */
    public Context getContext(String name) {
        return contexts.get(name);
    }

    /**
     * Removes a context from the ContextSet
     * @param name the name of the context desired removed
     * @return <code>true</code> if such Context exists in this ContextSet, otherwise <code>false</code>.
     */
    public boolean remove(String name) {
        return contexts.remove(name) != null;
    }

    /**
     * updates the hashtable of names to the correct context moreover it ensures no duplicate contexts exists
     * @param c context to be added/merged in the contexts hashtable
     * @return If a duplicate context is found a new Context object is returned otherwise c is returned.
    */
    public Context add(Context c) {
        Context newContext = identicalContext(c); //check for a duplicate context (shares the same set of hyps)
        if (newContext != c) {
            newContext.addNames(c.getNames());
        }
        c = newContext;
        for (String name: c.getNames()) {
            contexts.put(name, c);
        }
        return c;
    }

    /**
     * Checks if a passed Context has an identical context in this ContextSet.
     * Identicalness is tested by checking the equality of the PropositionSets of the two Contexts.
     * @param context the context that is checked for being identical with another context in this ContextSet
     * @return An identical context in this ContextSet is returned if found otherwise the passed context is returned.
     */
    public Context identicalContext(Context context) {
        for(Context c: contexts.values()) {
            if(c.getHypothesisSet().equals(context.getHypothesisSet()))
                return c;
        }
        return context;
    }

}
