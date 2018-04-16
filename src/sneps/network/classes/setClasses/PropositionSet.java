package sneps.network.classes.setClasses;

import sneps.exceptions.DuplicatePropositionException;
import sneps.exceptions.NodeNotFoundException;

import java.util.Arrays;

public class PropositionSet {
	private int[] props;
	private String hash;

	public PropositionSet() {
		this.props = new int[0];
	}

	public PropositionSet(int prop) {
		this.props = new int[] { prop };
		hash = prop + ", ";
	}

	public PropositionSet (int [] props, int prop) throws DuplicatePropositionException {
		for (int i = 0 ; i < props.length; i++)
			if(props[i] == prop)
				throw new DuplicatePropositionException();

		int i = 0, j = 0;
		this.props = new int[props.length + 1];
		boolean inserted = false;
		while (i < props.length) {
			if (!inserted && prop < props[i]) {
				this.props[j] = prop;
				hash += this.props[j++] + ", ";
				inserted = true;
			} else {
				this.props[j] = props[i++];
				hash += this.props[j++] + ", ";
			}
		}
		if (!inserted)
			this.props[j] = prop;

	}

	public PropositionSet(int[] props) {
		this.props = new int[props.length];
		for (int i = 0; i < props.length; i++) {
			this.props[i] = props[i];
			hash += this.props[i] + ", ";
		}
	}

	public PropositionSet add(int prop) throws DuplicatePropositionException {
		return new PropositionSet(this.getProps(), prop);
	}

	public static int[] getPropsSafely(PropositionSet set) {
		return new PropositionSet(set.getProps()).props;
	}

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

	public boolean isEmpty() {
		return props.length == 0;
	}

	public PropositionSet clearSet() {
		return new PropositionSet();
	}

	public int[] getProps() {
		return props;
	}

	public String getHash() {
		return hash;
	}
	public PropositionSet union(PropositionSet propSet) {
		int [] props = propSet.getProps();
		int [] props1 = this.getProps();
		int [] props2 = new int[props.length + props1.length];

		int i = 0, j = 0, k = 0;

		while (i < props.length || j < props1.length) {

			if (i >= props.length) {
				props2[k++] = props1[j++];
				continue;
			} else if (j >= props1.length) {
				props2[k++] = props[i++];
				continue;
			}

			if(props[i] == props1[j]) {
				props2[k] = props[i];
				i++;j++;
			} else if (props[i] < props1[i]) {
				props2[k] = props[i];
				i++;
			} else {
				props2[k] = props1[j];
				j++;
			}
			k++;
		}

		int [] output = Arrays.copyOfRange(props2, 0, k);
		return new PropositionSet(output);
	}

	public PropositionSet remove(int prop) throws NodeNotFoundException {
		int[] current = this.getProps();
		int[] newSet = new int[current.length - 1];
		int j = 0;
		boolean found = false;
		if (props[props.length -1] < prop)
			throw new NodeNotFoundException("The Node You Are Trying To Remove is Not Found");
		for (int i = 0; i < current.length; i++) {
			if (prop < current[i] && !found)
				throw new NodeNotFoundException("The Node You Are Trying To Remove is Not Found");
			if (!(prop == current[i])) {
				newSet[j] = current[i];
				j++;
			} else {
				found = true;
			}
		}
		return new PropositionSet(newSet);
	}


}