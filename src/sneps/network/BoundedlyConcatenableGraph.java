package sneps.network;

import java.util.Enumeration;
import java.util.Hashtable;

public class BoundedlyConcatenableGraph {
	
	public static Hashtable<Integer, Hashtable<Integer,Integer>> printhashtableConcatenableLow (int [] data , int lowerlimit){
		

		if(lowerlimit <= data.length ) {
			

		Hashtable<Integer, String> StartNodes = new Hashtable<Integer, String>();
		Hashtable<Integer, String> EndNodes = new Hashtable<Integer, String>();
		
		String start = "Start";
		String end = "End";
		
		StartNodes.put(data[0], start);
		EndNodes.put(data[data.length-1], end);
	
		Hashtable<Integer, Hashtable<Integer,Integer>>  hashtable = new Hashtable<Integer, Hashtable<Integer,Integer>>();
		Hashtable<Integer,Integer> temp = new Hashtable<Integer,Integer>();
		int counter = 0;
		
		for(int i = 0; i<data.length-1;i++) {
			
			Hashtable<Integer,Integer> ht = new Hashtable<Integer,Integer>();
			ht.put(data[i+1],data[i+1]);
			hashtable.put(data[i], ht);
			counter++;
			
		}
		
		temp.put(0,0);
		hashtable.put(data[counter], temp);
		
		System.out.println();
		System.out.println(StartNodes);
		System.out.println(EndNodes);
		System.out.println();
	
	return hashtable;
		}
		else return null;
	
}
public static Hashtable<Integer, Hashtable<Integer,Integer>> printhashtableConcatenableHigh (int [] data , int higherlimit){
		

		if(higherlimit >= data.length ) {
			

		Hashtable<Integer, String> StartNodes = new Hashtable<Integer, String>();
		Hashtable<Integer, String> EndNodes = new Hashtable<Integer, String>();
		
		String start = "Start";
		String end = "End";
		
		StartNodes.put(data[0], start);
		EndNodes.put(data[data.length-1], end);
	
		Hashtable<Integer, Hashtable<Integer,Integer>>  hashtable = new Hashtable<Integer, Hashtable<Integer,Integer>>();
		Hashtable<Integer,Integer> temp = new Hashtable<Integer,Integer>();
		int counter = 0;
		
		for(int i = 0; i<data.length-1;i++) {
			
			Hashtable<Integer,Integer> ht = new Hashtable<Integer,Integer>();
			ht.put(data[i+1],data[i+1]);
			hashtable.put(data[i], ht);
			counter++;
			
		}
		
		temp.put(0,0);
		hashtable.put(data[counter], temp);
		
		System.out.println();
		System.out.println(StartNodes);
		System.out.println(EndNodes);
		System.out.println();
	
	return hashtable;
		}
		else return null;
	
}

public static Hashtable<Integer, Hashtable<Integer, Integer>> printhashtableReversibleLow (int [] data , int lowerlimit){ 
	
	if(lowerlimit <= data.length ) {

	Hashtable<Integer, String> StartNodes = new Hashtable<Integer, String>();
	Hashtable<Integer, String> EndNodes = new Hashtable<Integer, String>();
	
	String start = "Start";
	String end = "End";
	
	StartNodes.put(data[0], start);
	EndNodes.put(data[data.length-1], end);
	
	Hashtable<Integer, Hashtable<Integer, Integer>>  hashtable = new Hashtable<Integer, Hashtable<Integer, Integer>>();
	
	Hashtable<Integer, Integer> aaa = new Hashtable<Integer, Integer>();
	aaa.put(data[1], data[1]);
	hashtable.put(data[0], aaa);
	
	for(int i = 1; i<data.length-1;i++) {
	
		Hashtable<Integer, Integer> aa = new Hashtable<Integer, Integer>();
		aa.put(data[i+1], data[i+1]);
		aa.put(data[i-1], data[i-1]);
		hashtable.put(data[i], aa);
		
}	
	Hashtable<Integer, Integer> aaaa = new Hashtable<Integer, Integer>();
	aaaa.put(data[data.length-2], data[data.length-2]);
	hashtable.put(data[data.length-1], aaaa);
	
	System.out.println();
	System.out.println(StartNodes);
	System.out.println(EndNodes);
	System.out.println();
	
return hashtable;
	}
	else return null;

}

public static Hashtable<Integer, Hashtable<Integer, Integer>> printhashtableReversibleHigh (int [] data , int higherlimit){ 
	
	if(higherlimit >= data.length ) {

	Hashtable<Integer, String> StartNodes = new Hashtable<Integer, String>();
	Hashtable<Integer, String> EndNodes = new Hashtable<Integer, String>();
	
	String start = "Start";
	String end = "End";
	
	StartNodes.put(data[0], start);
	EndNodes.put(data[data.length-1], end);
	
	Hashtable<Integer, Hashtable<Integer, Integer>>  hashtable = new Hashtable<Integer, Hashtable<Integer, Integer>>();
	
	Hashtable<Integer, Integer> aaa = new Hashtable<Integer, Integer>();
	aaa.put(data[1], data[1]);
	hashtable.put(data[0], aaa);
	
	for(int i = 1; i<data.length-1;i++) {
	
		Hashtable<Integer, Integer> aa = new Hashtable<Integer, Integer>();
		aa.put(data[i+1], data[i+1]);
		aa.put(data[i-1], data[i-1]);
		hashtable.put(data[i], aa);
		
}	
	Hashtable<Integer, Integer> aaaa = new Hashtable<Integer, Integer>();
	aaaa.put(data[data.length-2], data[data.length-2]);
	hashtable.put(data[data.length-1], aaaa);
	
	System.out.println();
	System.out.println(StartNodes);
	System.out.println(EndNodes);
	System.out.println();
	
return hashtable;
	}
	else return null;

}
public static Hashtable<Integer, Hashtable<Integer, Integer>> printhashtableSeparableLow (int [] data , int lowerlimit){ 
	
	if(lowerlimit <= data.length ) {

	Hashtable<Integer, String> StartNodes = new Hashtable<Integer, String>();
	Hashtable<Integer, String> EndNodes = new Hashtable<Integer, String>();
	
	String start = "Start";
	String end = "End";
	for(int i = 0; i< data.length; i++) {
		
		StartNodes.put(data[i], start);
		EndNodes.put(data[i], end);
	}
	
	
	Hashtable<Integer, Hashtable<Integer, Integer>>  hashtable = new Hashtable<Integer, Hashtable<Integer, Integer>>();
	
	Hashtable<Integer, Integer> aaa = new Hashtable<Integer, Integer>();
	aaa.put(data[1], data[1]);
	hashtable.put(data[0], aaa);
	
	for(int i = 1; i<data.length-1;i++) {
	
		Hashtable<Integer, Integer> aa = new Hashtable<Integer, Integer>();
		aa.put(data[i+1], data[i+1]);
		aa.put(data[i-1], data[i-1]);
		hashtable.put(data[i], aa);
		
}	
	Hashtable<Integer, Integer> aaaa = new Hashtable<Integer, Integer>();
	aaaa.put(data[data.length-2], data[data.length-2]);
	hashtable.put(data[data.length-1], aaaa);
	
	System.out.println();
	System.out.println(StartNodes);
	System.out.println(EndNodes);
	System.out.println();
	
return hashtable;
	}
	else return null;

}

public static Hashtable<Integer, Hashtable<Integer, Integer>> printhashtableSeparableHigh (int [] data , int higherlimit){ 
	
	if(higherlimit >= data.length ) {

	Hashtable<Integer, String> StartNodes = new Hashtable<Integer, String>();
	Hashtable<Integer, String> EndNodes = new Hashtable<Integer, String>();
	
	String start = "Start";
	String end = "End";
	for(int i = 0; i< data.length; i++) {
		
		StartNodes.put(data[i], start);
		EndNodes.put(data[i], end);
	}
	
	
	Hashtable<Integer, Hashtable<Integer, Integer>>  hashtable = new Hashtable<Integer, Hashtable<Integer, Integer>>();
	
	Hashtable<Integer, Integer> aaa = new Hashtable<Integer, Integer>();
	aaa.put(data[1], data[1]);
	hashtable.put(data[0], aaa);
	
	for(int i = 1; i<data.length-1;i++) {
	
		Hashtable<Integer, Integer> aa = new Hashtable<Integer, Integer>();
		aa.put(data[i+1], data[i+1]);
		aa.put(data[i-1], data[i-1]);
		hashtable.put(data[i], aa);
		
}	
	Hashtable<Integer, Integer> aaaa = new Hashtable<Integer, Integer>();
	aaaa.put(data[data.length-2], data[data.length-2]);
	hashtable.put(data[data.length-1], aaaa);
	
	System.out.println();
	System.out.println(StartNodes);
	System.out.println(EndNodes);
	System.out.println();
	
return hashtable;
	}
	else return null;

}
public static void removehashtable (Hashtable<Integer, Hashtable<Integer,Integer>> hashtable , int key) {
	
	Enumeration<Hashtable<Integer, Integer>> enumration = hashtable.elements();
	while(enumration.hasMoreElements()){
        Hashtable<Integer, Integer> elmnt = enumration.nextElement();
        if(elmnt.containsKey(key)){
        	elmnt.remove(key);
        }
    }
	hashtable.remove(key); 
	System.out.println();
}

public static boolean searchpath (Hashtable<Integer, Hashtable<Integer,Integer>> hashtable , int [] arr) {
	
	for(int i=0; i<arr.length-1;i++) {
		
			int element = arr[i];
			int nextelement = arr[i+1];
			
			try {
				if(hashtable.get(element).get(nextelement) == nextelement) {
					
				}
				else 
					return false;
			} 
			catch (NullPointerException e){
				
				return false;
			}
			
		
	}
	return true;
	
}

	public static void main(String[] args) {
		
		int[] a = new int[]{ 1,2,3 };
		System.out.print(printhashtableConcatenableLow(a, 4));
		

	}

}
