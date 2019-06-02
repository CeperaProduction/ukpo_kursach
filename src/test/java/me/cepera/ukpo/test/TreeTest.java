package me.cepera.ukpo.test;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Test;

import me.cepera.ukpo.CeperaTree;

public class TreeTest {

	Map<Integer, Object> rtTree;
	Map<Integer, Object> testTree;
	
	private static final Object OBJ = new Object[0];
	
	@Before
	public void before() {
		rtTree = new TreeMap<Integer, Object>();
		testTree = new CeperaTree<Integer, Object>();
	}
	
	@Test
	public void testRandomFill() {
		Random r = new Random();
		int lenght = 5 + r.nextInt(996);
		for(int i = 0; i < lenght; i++) {
			int key = r.nextInt(1000);
			rtTree.put(key, OBJ);
			testTree.put(key, OBJ);
		}
		assertIterableEquals(rtTree.keySet(), testTree.keySet(), "Тест на рандомное заполнение и последующую проверку провалился");
	}
	
	@Test
	public void testBigFill() {
		int[] val = {
				Integer.MAX_VALUE,
				Integer.MIN_VALUE,
				(int) (Integer.MAX_VALUE*0.9f),
				(int) (Integer.MIN_VALUE*0.9f),
				(int) (Integer.MAX_VALUE*0.7f),
				(int) (Integer.MIN_VALUE*0.7f),
				(int) (Integer.MAX_VALUE*0.2f),
				(int) (Integer.MIN_VALUE*0.2f)
				};
		for(int v : val) {
			rtTree.put(v, OBJ);
			testTree.put(v, OBJ);
		}
		assertIterableEquals(rtTree.keySet(), testTree.keySet(), "Тест на заполнение большими ключами провалился");
	}
	
	@Test
	public void testRandomFillAndDelete() {
		Random rand = new Random();
		ArrayList<Integer> keys = new ArrayList<Integer>();
		for(int i = 0; i < 100; i++) {
			keys.add(rand.nextInt(100));
		}
		for(int key : keys) {
			rtTree.put(key, OBJ);
			testTree.put(key, OBJ);
		}
		assertIterableEquals(rtTree.keySet(), testTree.keySet(), "Тест на заполнение случайными ключами провалился");
		//testTree.printTree();
		for(int key : keys) {
			rtTree.remove(key);
			testTree.remove(key);
			//testTree.printTree();
			assertIterableEquals(rtTree.keySet(), testTree.keySet(), "Тест на балансировку полсе удаления ключа '"+key+"' провалился");
		}
	}
	
}
