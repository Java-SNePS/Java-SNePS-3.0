package sneps.network;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Set;

public class ConcatenableOnlyGraph {
	
	private Hashtable<Integer, String> StartNodes;
	private Hashtable<Integer, String> EndNodes;
	private String start;
	private String end;
	private Hashtable<Integer, Hashtable<Integer,Integer>>  hashtable;
	private Hashtable<Integer,Integer> temp;
	
	
	public ConcatenableOnlyGraph() {
		 StartNodes = new Hashtable<Integer, String>();
		 EndNodes = new Hashtable<Integer, String>();
		 start = "Start";
		 end = "End";
		 hashtable = new Hashtable<Integer, Hashtable<Integer,Integer>>();
		 temp = new Hashtable<Integer,Integer>();
		 
	}
	
	public Hashtable<Integer, Hashtable<Integer,Integer>> printhashtableConcatenable (int [] data){


		this.StartNodes.put(data[0], start);
		this.EndNodes.put(data[data.length-1], end);
		
		if(this.hashtable.isEmpty()) {
		
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
	
		}
		
		else
			
		{	int counter = 0;
			Hashtable<Integer, Hashtable<Integer,Integer>> temporary = new Hashtable<Integer, Hashtable<Integer,Integer>>();
			for(int i = 0; i<data.length-1;i++) {
				
				Hashtable<Integer,Integer> ht = new Hashtable<Integer,Integer>();
			
				
				ht.put(data[i+1],data[i+1]);
				temporary.put(data[i], ht);
				counter++;
			
		}
			temp.put(0,0);
			temporary.put(data[counter], temp);
			this.hashtable.putAll(temporary);
//			this.hashtable.forEach(
//				    (key, value) -> temporary.merge( key, value, (v1, v2) -> v1 == v2 ? v1 : v2 ));
		
			}
		return hashtable; 
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
	
	public boolean searchpath (Hashtable<Integer, Hashtable<Integer,Integer>> hashtable , int [] arr) {
		
		if(this.StartNodes.containsKey(arr[0]) && this.EndNodes.containsKey(arr[arr.length-1])) {
			
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
		else return false;
			
		}


	public static void main(String[] args) {
		
		ConcatenableOnlyGraph a = new ConcatenableOnlyGraph();
		int[] x = new int[]{ 1,2,3,4 };
		int[] y = new int[]{ 1,2 };
		int [] z = new int[] {1,2,3,4};
		System.out.println(a.searchpath(a.printhashtableConcatenable(x), y));
		System.out.println(a.searchpath(a.printhashtableConcatenable(x), z));

	}

}
