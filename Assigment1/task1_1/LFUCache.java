import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.PriorityQueue;

public class LFUCache {
	
	static class LFUElement {
		
		private String request;
		private long findice;
		private int frequency;
		private static int indice = 0;
		
		public LFUElement(String request, int frequency) {
			this.request = request;
	        String s = Integer.toString(frequency) + indice;
	        indice++;
	        this.findice = Long.valueOf(s);
	        this.frequency = frequency;
		}
		
		public long getFdate() {
			return findice;
		}
		
		public String getRequest() {
			return request;
		}
		
		@Override
		public String toString() {
			return request + " - " + findice;
		}
		
		@Override
		public boolean equals(Object obj) {
			LFUElement element = (LFUElement) obj;
			
			return this.request.equals(element.getRequest());
		}
	}
	
	class LFUELementComparator implements Comparator<LFUElement> {
		@Override
		public int compare(LFUElement e1, LFUElement e2) {
			return (int) (e1.findice - e2.findice);
		}	
	}
	
		
	private Comparator<LFUElement> comparator = new LFUELementComparator();
	private LinkedHashMap<String, LFUElement> cache = new LinkedHashMap<String, LFUElement >();
	private PriorityQueue<LFUElement> queue;
	private int size;
	private int hit = 0;
	private int warmup;
	
	public LFUCache(int size, int warmup) {
		this.size = size;
		this.warmup = warmup;
		this.queue = new PriorityQueue<LFUElement>(size, comparator);
	}
	
	public int getHit() {
		return hit;
	}

	public void add(String request) {
		if (cache.containsKey(request)) {
			LFUElement tmp = cache.get(request);
			queue.remove(tmp);
			LFUElement newElement = new LFUElement(tmp.request,tmp.frequency + 1);
			cache.put(request, newElement);
			queue.add(newElement);
			if(queue.size() != cache.size()) {
				System.err.println("Violation Cache size and Queue size not equals ");
			}
			if (warmup == 0) {
				hit++;
			} else {
				warmup--;
			}
			
		} 
		else if (cache.size() >= size) {
			LFUElement removeElement = queue.remove();
			cache.remove(removeElement.request);
			LFUElement newElement = new LFUElement(request, 1);
			queue.add(newElement);
			cache.put(request, newElement);
			if (warmup == 0) {
			} else {
				warmup--;
			}
		}
		else {
			LFUElement newElement = new LFUElement(request, 1);
			queue.add(newElement);
			cache.put(request, newElement);
			if (warmup == 0) {
			} else {
				warmup--;
			}
		}
		
		if(cache.size() > size || queue.size() > size) {
			System.err.println("Violation cache size");
		}
	}

	public void print() {
		System.out.println(queue.toString());
	}
	

	public void writeInFile() throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter("cache_lfu.txt", "UTF-8");
		Iterator<String> it = cache.keySet().iterator();
		while(it.hasNext()) {
			writer.println(it.next());
		}
		writer.close();
	}
}