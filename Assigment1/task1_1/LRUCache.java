import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.LinkedList;

public class LRUCache {
	
	private int cacheSize;
	private LinkedList<String> list;
	private int hit = 0;
	private int warmup;
	
	public LRUCache(int cacheSize, int warmup) {
		this.list = new LinkedList<String>();
		this.cacheSize = cacheSize;
		this.warmup = warmup;
	}
	
	public int getHit() {
		return hit;
	}
	
	public void add (String s) {
		if (list.contains(s)) {
			list.remove(s);
			list.add(s);
			if (warmup == 0) {
				hit++;
			} else {
				warmup--;
			}
		}
		else if(list.size() == cacheSize) {
			list.pop();
			list.add(s);
			if (warmup == 0) {
			} else {
				warmup--;
			}
		} else {
			list.add(s);
			if (warmup == 0) {
			} else {
				warmup--;
			}
		}
	}
	
	public void writeInFile() throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter("cache_lru.txt", "UTF-8");
		Iterator<String> it = list.iterator();
		while(it.hasNext()) {
			writer.println(it.next());
		}
		writer.close();
	}
	
}
