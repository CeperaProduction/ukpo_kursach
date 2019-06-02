package me.cepera.ukpo.test;

import java.util.TreeMap;

import org.junit.Before;

public class TestsTest extends TreeTest{

	@Override
	@Before
	public void before() {
		rtTree = new TreeMap<Integer, Object>();
		testTree = new TreeMap<Integer, Object>();
	}
	
}
