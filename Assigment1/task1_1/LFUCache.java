import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class LFUCache {
	private int cacheSize;
	private Map<String, Integer> list;
	
	public LFUCache(int cacheSize) {
		this.list = new LinkedHashMap<String, Integer>();
		this.cacheSize = cacheSize;
	}
	
	public void add (String s) {
		if (list.containsKey(s)) {
			int frequency = list.remove(s);
			list.put(s, frequency + 1);
		} else if (list.size() == cacheSize) {
			Iterator<String> it = list.keySet().iterator();
			String tmpKey = "";
			int tmpFrequency = Integer.MAX_VALUE;
			while(it.hasNext()) {
				String key = it.next();
				int frequency = list.get(key);
				if (frequency < tmpFrequency) {
					tmpKey = key;
					tmpFrequency = frequency;
				}
			}
			list.remove(tmpKey);
			list.put(s,1);
		} else {
			list.put(s, 1);
		}
	}
	
	public void print() {
		Iterator<String> it = list.keySet().iterator();
		System.out.print("[ ");
		while(it.hasNext()) {
			String key = it.next();
			int frequency = list.get(key);
			System.out.print("{ " + key + ":" + frequency + " }");
		}
		System.out.println(" ]");
	}
}