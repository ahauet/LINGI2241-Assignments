import java.util.Arrays;
import java.util.LinkedList;

public class LRUCache {
	
	private int cacheSize;
	private LinkedList<String> list;
	private int miss = 0;
	private int hit = 0;
	
	public LRUCache(int cacheSize) {
		this.list = new LinkedList<String>();
		this.cacheSize = cacheSize;
	}
	
	public void add (String s) {
		if (list.contains(s)) {
			list.remove(s);
			list.add(s);
			hit++;
		}
		else if(list.size() == cacheSize) {
			list.pop();
			list.add(s);
			miss++;
		} else {
			list.add(s);
			miss++;
		}
	}
	
	public void print() {
		System.out.println(Arrays.toString(list.toArray()));
	}
	
	public void printHitMiss() {
		System.out.println("Hit = " +hit+" Miss = " +miss);
	}
	
}
