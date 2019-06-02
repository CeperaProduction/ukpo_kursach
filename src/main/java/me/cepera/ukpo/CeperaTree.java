package me.cepera.ukpo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class CeperaTree<K, V> implements Map<K, V>{

	private TreeNode root = null;
	private final Comparator<Object> comp;
	
	private final static Object NONE = new Object();
	
	public CeperaTree() {
		this(new Comparator<K>() {
			@Override
			public int compare(Object o1, Object o2) {
				if(o1 instanceof Comparable && o2 instanceof Comparable) ((Comparable)o1).compareTo(o2);
				if(o1.hashCode() > o2.hashCode()) return 1;
				if(o1.hashCode() < o2.hashCode()) return -1;
				return 0;
			}
		});
	}
	
	public CeperaTree(Comparator<K> comparator) {
		comp = (Comparator<Object>) comparator;
	}
	
	private class TreeNode implements Entry<K, V>{

		private K key;
		private V value;
		private TreeNode left, right;
		private byte height;
		
		private TreeNode(K key, V value) {
			this.key = key;
			this.value = value;
			height = 1;
		}
		
		@Override
		public K getKey() {
			return key;
		}

		@Override
		public V getValue() {
			return value;
		}

		@Override
		public V setValue(V value) {
			return this.value = value;
		}
		
	}
	
	private TreeNode find(TreeNode from, Object key, Object value) {
		return find(from, key, value, -1);
	}
	
	private TreeNode find(TreeNode from, Object key, Object value, int calc) {
		if(from != null) {
			if(key != NONE && from.key.equals(key)) {
				return from;
			}
			if(value != NONE && from.value.equals(value)) {
				return from;
			}
			if(comp.compare(key, from.key) < 0)
				return find(from.left, key, value, calc == -1 ? -1 : calc+1);
			else
				return find(from.right, key, value, calc == -1 ? -1 : calc+1);
		}
		return null;
	} 
	
	@Override
	public int size() {
		int c = 0;
		if(root != null) {
			CeperaQueue<TreeNode> q = new CeperaQueue<TreeNode>();
			q.add(root);
			while(!q.isEmpty()) {
				TreeNode node = q.poll();
				c++;
				if(node.left != null) q.add(node.left);
				if(node.right != null) q.add(node.right);
			}
		}
		return c;
	}

	@Override
	public boolean isEmpty() {
		return root == null;
	}

	@Override
	public boolean containsKey(Object key) {
		return find(root, key, NONE) != null;
	}

	@Override
	public boolean containsValue(Object value) {
		return find(root, NONE, value) != null;
	}

	@Override
	public V get(Object key) {
		TreeNode node = find(root, key, NONE, 0);
		return node != null ? node.value : null;
	}

	@Override
	public V put(K key, V value) {
		TreeNode node = new TreeNode(key, value);
		if(root == null) {
			root = node;
			return root.value;
		}
		TreeNode exist = find(root, key, NONE);
		if(exist != null) {
			exist.value = value;
			return exist.value;
		}
		return insert(root, node).value;
	}
	
	private byte height(TreeNode node) {
		return node != null ? node.height : 0;
	}
	
	private int bFactor(TreeNode node) {
		return height(node.right)-height(node.left);
	}
	
	private void fixHeight(TreeNode node) {
		byte h1 = height(node.left);
		byte h2 = height(node.right);
		node.height = (byte) ((h1 > h2 ? h1 : h2) + 1);
	}
	
	private TreeNode rotateRight(TreeNode node) {
		TreeNode node1 = node.left;
		node.left = node1.right;
		node1.right = node;
		if(node == root) root = node1;
		fixHeight(node);
		fixHeight(node1);
		return node1;
	}
	
	private TreeNode rotateLeft(TreeNode node) {
		TreeNode node1 = node.right;
		node.right = node1.left;
		node1.left = node;
		if(node == root) root = node1;
		fixHeight(node);
		fixHeight(node1);
		return node1;
	}
	
	private TreeNode balance(TreeNode node) {
		fixHeight(node);
		if(bFactor(node) == 2) {
			if(bFactor(node.right) < 0) node.right = rotateRight(node.right);
			return rotateLeft(node);
		}
		if(bFactor(node) == -2) {
			if(bFactor(node.left) > 0) node.left = rotateLeft(node.left);
			return rotateRight(node);
		}
		return node;
	}
	
	private TreeNode insert(TreeNode baseNode, TreeNode node) {
		if(baseNode == null) return node;
		if(comp.compare(node.key, baseNode.key) < 0)
			baseNode.left = insert(baseNode.left, node);
		else
			baseNode.right = insert(baseNode.right, node);
		return balance(baseNode);
	}

	@Override
	public V remove(Object key) {
		if(size() == 1) root = null;
		TreeNode node = find(root, key, NONE);
		if(node == null) return null;
		TreeNode min = findMin(node.right);
		node.right = delMin(node.right);
		if(min == null) {
			min = node.left;
			if(node.left != null) {
				node.right = node.left.right;
				node.left = node.left.left;
			}
		}
		if(min != null) {
			node.key = min.key;
			node.value = min.value;
		}else {
			node = del(root, node);
		}
		
		return balance(node).value;
	}
	
	private TreeNode del(TreeNode from, TreeNode node) {
		if(from != null && from != node) {
			if(from.left == node) {
				from.left = null;
				return from;
			}
			if(from.right == node) {
				from.right = null;
				return from;
			}
			if(comp.compare(node.key, from.key) < 0)
				return del(from.left, node);
			else
				return del(from.right, node);
		}
		return from;
	}
	
	private TreeNode findMin(TreeNode node) {
		return node != null && node.left != null ? findMin(node.left) : node;
	}
	
	private TreeNode delMin(TreeNode node) {
		if(node == null) return null;
		if(node.left == null)
			return node.right;
		node.left = delMin(node.left);
		return balance(node);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		for(Entry<? extends K, ? extends V> ent : m.entrySet())
			put(ent.getKey(), ent.getValue());
	}

	@Override
	public void clear() {
		if(root != null) {
			CeperaQueue<TreeNode> q = new CeperaQueue<TreeNode>();
			q.add(root);
			while(!q.isEmpty()) {
				TreeNode node = q.poll();
				if(node.left != null) {
					q.add(node.left);
					node.left = null;
				}
				if(node.right != null) {
					q.add(node.right);
					node.right = null;
				}
			}
		}
	}

	@Override
	public Set<K> keySet() {
		LinkedHashSet<K> set = new LinkedHashSet<K>();
		Collection<Entry<K, V>> list = loop();
		for(Entry<K, V> e : list) {
			set.add(e.getKey());
		}
		return Collections.unmodifiableSet(set);
	}
	
	public Collection<Entry<K, V>> loop(){
		ArrayList<Entry<K, V>> list = new ArrayList<Entry<K, V>>();
		if(root != null) loop(root, list);
		return Collections.unmodifiableCollection(list);
	}
	
	private void loop(TreeNode node, Collection<Entry<K, V>> res) {
		if(node.left != null) loop(node.left, res);
		res.add(node);
		if(node.right != null) loop(node.right, res);
	}

	@Override
	public Collection<V> values() {
		ArrayList<V> res = new ArrayList<V>();
		Collection<Entry<K, V>> list = loop();
		for(Entry<K, V> e : list) {
			res.add(e.getValue());
		}
		return Collections.unmodifiableCollection(res);
	}
	
	public void printTree() {
		ArrayList<K> list = new ArrayList<K>();
		if(root != null) {
			CeperaQueue<TreeNode> q = new CeperaQueue<TreeNode>();
			q.add(root);
			while(!q.isEmpty()) {
				TreeNode node = q.poll();
				list.add(node != null ? node.key : null);
				if(node != null) {
					q.add(node.left);
					q.add(node.right);
				}
			}
		}
		ArrayList<Object[]> lines = new ArrayList<Object[]>();
		Object[] cl = new Object[1];
		lines.add(cl);
		int cw = 0;
		int w = 1;
		for(K e : list) {
			cl[cw] = e;
			if(cw >= w-1) {
				w*=2;
				cw = 0;
				cl = new Object[w];
				lines.add(cl);
				continue;
			}
			cw++;
		}
		int l = 1;
		String d = "";
		for(int i = 0; i < w; i++) {
			d+="------";
		}
		System.out.println(d);
		for(Object[] line : lines) {
			int of = (int)(Math.pow(2, l));
			for(Object o : line) {
				int of2 = (w*6/of-3);
				if(of2 < 1) 
					System.out.printf("%6s", o == null ? " " : o);
				else
					System.out.printf("%"+of2+"s"+"%6s%"+of2+"s", " ", o == null ? " " : o, " ");
			}
			l++;
			System.out.println("\n"+d);
		}
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		LinkedHashSet<Entry<K, V>> set = new LinkedHashSet<Entry<K, V>>();
		Collection<Entry<K, V>> list = loop();
		for(Entry<K, V> e : list) {
			set.add(e);
		}
		return Collections.unmodifiableSet(set);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Collection<Entry<K, V>> list = loop();
		for(Entry<K, V> e : list) {
			if(sb.length() > 0) sb.append(", ");
			sb.append(e.getKey().toString());
			sb.append(" : ");
			sb.append(e.getValue().toString());
		}
		return sb.toString();
	}
	
}
