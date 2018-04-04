package sneps.network.classes.setClasses;

import java.util.ArrayList;
import java.util.Arrays;

public class PropositionSet {
	private int[] props;

	public PropositionSet() {
		this.props = new int[0];
	}

	public PropositionSet(int prop) {
		this.props = new int[]{prop};
	}

	public PropositionSet(int [] props) {
		this.props = new int[props.length];
		for (int i = 0; i < props.length ; i++) {
			this.props[i] = props[i];
		}
	}

	public PropositionSet(int [] props, int prop) {
		int i = 0, j = 0;
		this.props = new int[props.length + 1];
		boolean inserted = false;
		while (i < props.length) {
			if(!inserted && prop < props[i]) {
				this.props[j++] = prop;
				inserted = true;
			} else {
				this.props[j++] = props[i++];
			}
		}
		if(!inserted)
			this.props[j] = prop;

	}

	public int[] getProps() {
		return props;
	}

	public boolean equals(Object obj) {
		PropositionSet propositionSet = (PropositionSet)obj;
		int [] inputProps = propositionSet.getProps();
		if (inputProps.length != this.props.length) {
			return false;
		}
		else {
			for (int i = 0; i < this.props.length; i ++) {
				if (this.props[i] != inputProps[i])
					return false;
			}
		}
		return true;
	}

	public boolean isSubSet(PropositionSet propositionSet) {
		int [] props =  propositionSet.getProps();
		int i = 0, j = 0;
		while(i < this.props.length && j < props.length) {
			if (this.props[i] == props[j])
				i++;
			else
				j++;
		}
		return i == this.props.length;
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

	public PropositionSet remove(int prop) {
		int [] output = new in
	}

}
/*
	public PropositionSet union(int[] props) {
		int[] FirstSet = this.getProps();
		int[] SecondSet = props;
		int[] newSet = new int[FirstSet.length + SecondSet.length];
		int i = 0, j = 0, q = 0, elements=0;
		while (i < FirstSet.length || j < SecondSet.length) {
			if (i == FirstSet.length) {
				newSet[q++] = SecondSet[j++];
				elements++;
			} else {
				if (j == SecondSet.length) {
					newSet[q++] = FirstSet[i++];
					elements++;
				} else {
					if (FirstSet[i] < SecondSet[j]) {
						newSet[q++] = FirstSet[i];
						elements++;
					} else {
						if (FirstSet[i] > SecondSet[j]) {
							newSet[q++] = SecondSet[j++];
							elements++;
						} else {
							newSet[q++] = FirstSet[i++];
							j++;
							elements++;
						}
					}
				}
			}

		}
		if(elements == newSet.length){
			return new PropositionSet(newSet);
		}else{
			int[] smallSet = new int[elements];
			for(int z = 0; z< smallSet.length; z++)
				smallSet[z] = newSet[z];
			return new PropositionSet(smallSet);
		}

	}
}
*/