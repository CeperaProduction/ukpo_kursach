package me.cepera.ukpo;

import java.util.Iterator;
import java.util.TreeMap;

public class Main {
	
	public static void main(String[] args) {
		TreeMap<Integer, Integer> rtTree = new TreeMap<>();
		CeperaTree<Integer, Integer> tree = new CeperaTree<Integer,Integer>();
		for(int i = 0; i < 20; i++) {
			tree.put(i, 0);
			rtTree.put(i, 0);
		}
		Iterator<Integer> it1 = tree.keySet().iterator();
		Iterator<Integer> it2 = rtTree.keySet().iterator();
		while(it1.hasNext() && it2.hasNext()) {
			System.out.println(it1.next() + " " + it2.next());
		}
	}
	
}
