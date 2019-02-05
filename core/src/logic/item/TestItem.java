package logic.item;

import java.util.ArrayList;

public class TestItem {
	public static void main(String[] args) {
		ArrayList<Integer> l1 = new ArrayList<Integer>();
		ArrayList<Integer> l2 = new ArrayList<Integer>();
		for(int i = 0; i<5; i++) {
			l1.add(i);
			l2.add(5-i);
		}
		System.out.println(l1.equals(l2));
		System.out.println(l1.indexOf(2));
		System.out.println(l1.indexOf(44));
		int[] m1 = new int[2];
		int[] m2 = new int[2];
		m1[0] = 0;
		m1[1] = 1;
		m2[0] = 0;
		m2[1] = 1;
	}
}
