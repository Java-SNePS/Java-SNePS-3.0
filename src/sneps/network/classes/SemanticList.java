package sneps.network.classes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;

import sneps.network.Network;

public class SemanticList implements Serializable {
	private static ArrayList<Semantic> semanticList = new ArrayList<Semantic>();
	
	public SemanticList() {
		
	}

	public static ArrayList<Semantic> getSemanticList() {
		return semanticList;
	}
	
	public static void addToSemanticList(Semantic sem) {
		semanticList.add(sem);

	}
	
	public static void save(String n) throws FileNotFoundException, IOException {
		ObjectOutputStream slos = new ObjectOutputStream(new FileOutputStream(new File(n)));
		slos.writeObject(semanticList);
		slos.close();
	}
	
	public static void load(String n) throws IOException, ClassNotFoundException {
		ObjectInputStream slis= new ObjectInputStream(new FileInputStream(new File(n)));
		ArrayList<Semantic> tempSL = (ArrayList<Semantic>) slis.readObject();
		SemanticList.semanticList = tempSL;
		slis.close();
		tempSL = null;
	}
}
