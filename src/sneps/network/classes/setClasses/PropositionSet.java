package sneps.network.classes.setClasses;

import sneps.exceptions.CustomException;
import sneps.exceptions.DuplicatePropositionException;
import sneps.exceptions.NodeNotFoundInNetworkException;
import sneps.exceptions.NodeNotFoundInPropSetException;
import sneps.exceptions.NotAPropositionNodeException;
import sneps.network.Network;
import sneps.network.PropositionNode;

import java.io.Serializable;
import java.util.Arrays;

public class PropositionSet implements Serializable{
    @Override
    public String toString() {
        return "PropositionSet [props=" + Arrays.toString(props) + "]";
    }

    private int[] props;
    private String hash = "";

    /**
     * Constructs a new PropositionSet with an empty array of props
     */
    public PropositionSet() {
        this.props = new int[0];
    }

    /**
     * Constructs a new PropositionSet with an array containing a single prop
     *
     * @param prop proposition to be added to the array of props in this PropositionSet
     * @throws NotAPropositionNodeException
     * @throws NodeNotFoundInNetworkException
     * @throws CustomException
     */
    public PropositionSet(int prop) throws NotAPropositionNodeException, NodeNotFoundInNetworkException {
        if (!(Network.getNodeById(prop) instanceof PropositionNode)) {
            throw new NotAPropositionNodeException();
        }
        this.props = new int[]{prop};
        hash = prop + ",";
    }

    /**
     * Constructs a new PropositionSet with an array containing of propositions
     * deep cloning of the array occurs here.
     *
     * @param props the array of props to populate the props attribute with
     * @throws NodeNotFoundInNetworkException
     */
    public PropositionSet(int[] props) throws NotAPropositionNodeException, NodeNotFoundInNetworkException {
        for (int i = 0; i < props.length; i++)
            if (!(Network.getNodeById(props[i]) instanceof PropositionNode))
                throw new NotAPropositionNodeException();

        this.props = removeDuplicates(props);
        Arrays.sort(this.props);
        for (int i = 0; i < this.props.length; i++) {
            hash += this.props[i] + ",";
        }
    }


    /**
     * Returns a new props array having no duplicates
     *
     * @param props the array of props that duplicates should be removed from if found
     * @return a new array of props having no duplicates
     */
    public static int[] removeDuplicates(int[] props) {
        int current = -1, j = 0;
        int[] temp = new int[props.length];
        for (int i = 0; i < props.length; i++) {
            if (current == props[i])
                continue;
            else {
                current = props[i];
                temp[j] = current;
                j++;
            }
        }
        return Arrays.copyOfRange(temp, 0, j);
    }

    /**
     * Method for returning the props of the PropositionSet
     *
     * @return an int array containing the props
     */
    private int[] getProps() {
        return props;
    }

    /**
     * Returns an array of the props in a given PropositionSet
     * but insures immutability through deep cloning of the props done by the
     * PropositionSet constructor.
     *
     * @return a <b>new</b> int array of props
     * @throws NodeNotFoundInNetworkException
     */
    public static int[] getPropsSafely(PropositionSet set) throws NotAPropositionNodeException, NodeNotFoundInNetworkException {
        return new PropositionSet(set.getProps()).props;
    }

    /**
     * Checks if a given PropositionSet is equivalent to this.
     * It checks for equality by comparing the equivalence of the two props arrays.
     *
     * @param obj
     * @return <code>true</code> if they are equal and <code>false</code> otherwise.
     */
    public boolean equals(Object obj) {
        PropositionSet propositionSet = (PropositionSet) obj;
        int[] inputProps = propositionSet.getProps();
        if (inputProps.length != this.props.length) {
            return false;
        } else {
            for (int i = 0; i < this.props.length; i++) {
                if (this.props[i] != inputProps[i])
                    return false;
            }
        }
        return true;
    }

    /**
     * Checks if this PropositionSet is a subset of a passed PropositionSet.
     *
     * @param propositionSet the set that should be a superset of this PropositionSet
     * @return <code>true</code> if this is a subset of propositionSet, <code>false</code> otherwise.
     */
    public boolean isSubSet(PropositionSet propositionSet) {
        int[] props = propositionSet.getProps();
        int i = 0, j = 0;
        while (i < this.props.length && j < props.length) {
            if (this.props[i] == props[j])
                i++;
            else
                j++;
        }
        return i == this.props.length;
    }

    /**
     * Performs a union of this PropositionSet and a passed PropositionSet and returns
     * a new PropositionSet with the union
     *
     * @param propSet the PropositionSet to perform union with.
     * @return the union of the two PropositionSets
     * @throws NodeNotFoundInNetworkException
     */

    public PropositionSet union(PropositionSet propSet) throws NotAPropositionNodeException, NodeNotFoundInNetworkException {
        int[] props = propSet.getProps();
        int[] props1 = this.getProps();
        int[] props2 = new int[props.length + props1.length];

        int i = 0, j = 0, k = 0;

        while (i < props.length || j < props1.length) {

            if (i >= props.length) {  // length of arg passed
                props2[k++] = props1[j++];
                continue;
            } else if (j >= props1.length) {
                props2[k++] = props[i++];
                continue;
            }

            if (props[i] == props1[j]) {
                props2[k] = props[i];
                i++;
                j++;
            } else if (props[i] < props1[j]) {
                props2[k] = props[i];
                i++;
            } else {
                props2[k] = props1[j];
                j++;
            }
            k++;
        }

        int[] output = Arrays.copyOfRange(props2, 0, k);
        return new PropositionSet(output);
    }

    public boolean isEmpty() {
        return props.length == 0;
    }

    public PropositionSet clearSet() {
        return new PropositionSet();
    }

    public String getHash() {
        return hash;
    }

    /**
     * Returns a new PropositionSet without the proposition passed as an argument.
     *
     * @param prop the proposition that shouldn't be present in the returned PropositionSet
     * @return a new PropositionSet not having prop.
     * @throws NodeNotFoundInPropSetException if prop is not found in this PropositionSet
     * @throws NodeNotFoundInNetworkException
     */
    public PropositionSet remove(int prop) throws NodeNotFoundInPropSetException, NotAPropositionNodeException, NodeNotFoundInNetworkException {
        if (this.props.length == 0)
            return new PropositionSet();

        int[] current = this.getProps();
        int[] newSet = new int[current.length - 1];
        int j = 0;
        boolean found = false;
        if (props[props.length - 1] < prop)
            throw new NodeNotFoundInPropSetException("The Node You Are Trying To Remove is Not Found");
        for (int i = 0; i < current.length; i++) {
            if (prop < current[i] && !found)
                throw new NodeNotFoundInPropSetException("The Node You Are Trying To Remove is Not Found");
            if (!(prop == current[i])) {
                newSet[j] = current[i];
                j++;
            } else {
                found = true;
            }
        }
        return new PropositionSet(newSet);
    }

    public PropositionSet removeProps(PropositionSet propSet) throws NotAPropositionNodeException, NodeNotFoundInNetworkException {
        int[] props = this.getProps();
        int[] props1 = propSet.getProps();
        int[] props2 = new int[props.length];

        int i = 0, j = 0, k = 0;

        for (; i < props.length; i++) {

            if (j >= props1.length) {
                props2[k++] = props[i];
                continue;
            }

            if (props[i] == props1[j]) {
                j++;
            } else {
                props2[k] = props[i];
                k++;
            }
        }

        int[] output = Arrays.copyOfRange(props2, 0, k);
        return new PropositionSet(output);

    }

    /**
     * Creates a new PropositionSet with a prop if it isn't a duplicate.
     *
     * @param prop The proposition that is desired to be added.
     * @return A new PropositionSet with the added prop.
     * @throws DuplicatePropositionException  If the prop is a duplicate
     * @throws NodeNotFoundInNetworkException
     */
    public PropositionSet add(int prop) throws DuplicatePropositionException, NotAPropositionNodeException, NodeNotFoundInNetworkException {
        int[] props = this.props;
        int[] props2 = new int[props.length + 1];
        int i = 0, j = 0;
        boolean inserted = false;
        while (i < props.length) {
            if (props[i] == prop)
                throw new DuplicatePropositionException();

            if (!inserted && prop < props[i]) {
                props2[j++] = prop;
                inserted = true;
            } else {
                props2[j++] = props[i++];
            }
        }

        if (!inserted)
            props2[j] = prop;

        hash = hash + ",";

        return new PropositionSet(props2);

    }


}