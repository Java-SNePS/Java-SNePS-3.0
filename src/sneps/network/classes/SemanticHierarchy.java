package sneps.network.classes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.LinkedList;

import sneps.exceptions.SemanticNotFoundInNetworkException;
import sneps.network.Network;

public class SemanticHierarchy implements Serializable{

	private static Hashtable<String, Semantic> semantics = new Hashtable<String, Semantic>();

	public static Semantic createSemanticType(String identifier) {
		if (semantics.containsKey(identifier)) {
			return semantics.get(identifier);
		} else {
			Semantic semantic = new Semantic(identifier);
			semantics.put(identifier, semantic);
			return semantics.get(identifier);
		}
	}

	public static Semantic getSemantic(String identifier) throws SemanticNotFoundInNetworkException {
		if (semantics.containsKey(identifier)) {
			return semantics.get(identifier);
		} else {
			throw new SemanticNotFoundInNetworkException(
					"There is no semantic type named '" + identifier + "' in the system");
		}
	}

	public static Hashtable<String, Semantic> getSemantics() {
		return semantics;
	}
	
	public static void save(String f) throws FileNotFoundException, IOException {
		ObjectOutputStream fos = new ObjectOutputStream(new FileOutputStream(new File(f)));
		fos.writeObject(semantics);
		fos.close();
	}
	
	public static void load(String f) throws FileNotFoundException, IOException, ClassNotFoundException {
		ObjectInputStream sems= new ObjectInputStream(new FileInputStream(new File(f)));
		Hashtable<String, Semantic> tempSems = (Hashtable<String, Semantic>) sems.readObject();
		SemanticHierarchy.semantics = tempSems;
		sems.close();
	}

}