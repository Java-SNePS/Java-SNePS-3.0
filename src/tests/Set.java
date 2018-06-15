package tests;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.TreeMap;

import sneps.network.PropositionNode;
import sneps.network.classes.setClasses.PropositionSet;

public class Set  {
	
	public Set() {
		
	}
	
	
	public static void arrayBench() {
		int[] arr = new int[1000000];
		
		Date date = new Date();
		for(int i = 0; i < 200000; i++){
			int rand = (int) (Math.random()*100000);
			Integer j = new Integer(rand);
			arr[i] = j;
		}
		Date date2 = new Date();
		System.out.println(date2.getTime() - date.getTime());
		
		for(int i = 0; i < 200000; i++){
			
		}
		
		
	}
	
	
	public static void avlBench() {
		AvlTree2 avlTree = new AvlTree2();
		
		
		for(int i = 0; i < 200000; i++){
			int rand = (int) (Math.random()*100000);
			Integer j = new Integer(rand);
			avlTree.insert(j);
		}
		
		Date date = new Date();
		for(int i = 0; i < 200000; i++){
			avlTree.contains(i);
		}
		
		Date date2 = new Date();
		System.out.println(date2.getTime() - date.getTime());
	}
	
	public static void RBBench() {
		RedBlackTree<Integer> RBTree= new RedBlackTree<Integer>();
		
		for(int i = 0; i < 200000; i++){
			int rand = (int) (Math.random()*100000);
			RBTree.add(rand);
		}
		Date date = new Date();
		
		
		for(int i = 0; i < 200000; i++){
		RBTree.contains(i);
	}
		Date date2 = new Date();
		System.out.println(date2.getTime() - date.getTime());
		
		
	}
	public static void TreeMapBench() {
		TreeMap<Integer, Integer> treeMap = new TreeMap<Integer, Integer>();
		
		Date date = new Date();
		for(int i = 0; i < 200000; i++){
			int rand = (int) (Math.random()*100000);
			treeMap.put(rand,rand);
		}

		Date date2 = new Date();
		System.out.println(date2.getTime() - date.getTime());
		
		for(int i = 0; i < 1000000; i++){
			treeMap.get(i);
		}

	}
	


	public static void main(String[] args) {
		arrayBench();
	}
	
}