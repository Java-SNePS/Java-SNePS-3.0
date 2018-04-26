package sneps.snebr;

import sneps.exceptions.DuplicatePropositionException;
import sneps.exceptions.NodeNotFoundException;
import sneps.network.PropositionNode;
import sneps.network.classes.setClasses.PropositionSet;
import sneps.exceptions.CustomException;
import sneps.exceptions.DuplicatePropositionException;
import sneps.exceptions.NodeNotFoundInNetworkException;
import sneps.exceptions.NotAPropositionNodeException;
import sneps.network.Network;
import sneps.network.PropositionNode;
import sneps.network.classes.setClasses.PropositionSet;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;


public class Context {
	private PropositionSet hyps;
	private HashSet<String> names;

    /**
     * Constructs a new empty Context
     */
    protected Context() {
        names = new HashSet<String>();
        this.hyps = new PropositionSet();
    }

    /**
     * Constructs a new Context given its name
     *
     * @param contextName the name of the Context to be created
     */
    protected Context(String contextName) {
        this();
        names.add(contextName);
    }

    /**
     * Constructs a new Context from another Context
     *
     * @param c the context that the new Context is constructed from
     */
    protected Context(Context c) {
        this.hyps = c.getHypothesisSet();
        this.names = c.getNames();
    }

    /**
     * Constructs a new Context from a Context and asserts a hypothesis <i>hyp</i> in it.
     *
     * @param c   the context to be used for constructing this new Context
     * @param hyp the hyp to be asserted in the new Context
     * @throws DuplicatePropositionException  if the hyp is present in the context c
     * @throws NodeNotFoundInNetworkException
     */
    protected Context(Context c, int hyp) throws NotAPropositionNodeException, DuplicatePropositionException, NodeNotFoundInNetworkException {
        this.names = c.getNames();
        this.hyps = c.getHypothesisSet().add(hyp);
    }

    /**
     * Constructs a new Context from an old Context <i>c</i>, and adds a new name to this new Context
     *
     * @param contextName
     * @param c
     */
    protected Context(String contextName, Context c) {
        this(c);
        this.names.add(contextName);
    }

    /**
     * Constructs a new Context using its name and sets the Context's hyps to a passed Proposition set <i>hyps</i>.
     *
     * @param contextName name of the new Context
     * @param hyps        the hyps the Context's hyps should be set to
     */
    protected Context(String contextName, PropositionSet hyps) {
        this(contextName);
        this.hyps = hyps;
    }

    /**
     * Returns the hyps of this Context
     *
     * @return a PropositionSet containing the hyps of this Context
     */
    public PropositionSet getHypothesisSet() {
        return hyps;
    }

    /**
     * Returns the names of this Context object.
     *
     * @return a Hashset of type String containing the names of this context.
     */
    protected HashSet<String> getNames() {
        return names;
    }

    public String getName() {
        return null;
    }

    /**
     * Checks if a propositions is asserted in this context
     * @param p the proposition to be checked for assertion.
     * @return <code>true</code> if the proposition exists, otherwise <code>false</code>
     * @throws NotAPropositionNodeException If the node p is not a proposition.
     * @throws NodeNotFoundInNetworkException If the node p doesn't exist in the network.
     */
    public boolean isAsserted(PropositionNode p) throws NotAPropositionNodeException, NodeNotFoundInNetworkException {
        int hyp = p.getId();

        if (Arrays.binarySearch(PropositionSet.getPropsSafely(this.hyps), hyp) < 0) {
            return true;
        } else if (isSupported(p)) {
            return true;
        } else {
            return false;
        }

    }

    private boolean isSupported(PropositionNode node) {
        Collection<PropositionSet> assumptionSet = node.getBasicSupport()
                .getAssumptionBasedSupport()
                .values();
        for (PropositionSet assumptionHyps : assumptionSet) {
            if (assumptionHyps.isSubSet(this.hyps)) {
                return true;
            }
        }
        return false;
    }

    public PropositionSet allAsserted() {
        Collection<PropositionNode> allPropositionNodes = Network.getPropositionNodes().values();
        PropositionSet asserted = new PropositionSet();
        int[] hyps;
        try {
            hyps = PropositionSet.getPropsSafely(this.hyps);
        } catch (NotAPropositionNodeException e) {
            return null;
        } catch (NodeNotFoundInNetworkException e1) {
            return null;
        }
        for (PropositionNode node : allPropositionNodes) {
            try {
                if (Arrays.binarySearch(hyps, node.getId()) < 0) {
                    asserted.add(node.getId());
                } else if (isSupported(node)) {
                    asserted.add(node.getId());
                }
            } catch (NodeNotFoundInNetworkException e1) {
                return null;
            } catch (NotAPropositionNodeException e) {
                return null;
            } catch (DuplicatePropositionException e) {
                System.err.println(e.getMessage());
            }
        }

        return asserted;
    }

    /**
     * Adds a name to the set of names of the context if not a duplicate.
     *
     * @param name Name to be added to the context's names
     * @return <code>true</code> if the name isn't a duplicate <code>false</code> otherwise.
     */
    protected boolean addName(String name) {
        return this.names.add(name);
    }

    /**
     * Adds multiple names to this Context
     *
     * @param names a HashSet of type String of the names to add to this Context
     * @return <code>true</code> if one at least one of the names isn't a duplicate <code>false</code> otherwise.
     */
    protected boolean addNames(HashSet<String> names) {
        return this.names.addAll(names);
    }

    /**
     * Removes a name from the set of names of the context if present.
     *
     * @param name Name to be remove from the context's names
     * @return <code>true</code> if this is found <code>false</code> otherwise.
     */
    protected boolean removeName(String name) {
        return this.names.remove(name);
    }

}
