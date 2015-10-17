import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.PriorityQueue;

public class LFUCache {
	
	class LFUElement {
		
		private String request;
		//Concatenation between frequency and date
		private long fdate;
		private int frequency;
		private int size;
		
		public LFUElement(String request, int frequency, int size) {
			this.size = size;
			this.request = request;
			Calendar cal = Calendar.getInstance();
	        SimpleDateFormat sdf = new SimpleDateFormat("HHmmssSS");
	        String date = sdf.format(cal.getTime());
	        if(date.length() < 9) {
	        	while(date.length() < 9) {
	        		date += "0";
	        	}
	        }
	        String s = Integer.toString(frequency) + date;
	        this.fdate = Long.valueOf(s);
	        this.frequency = frequency;
		}
		
		public long getFdate() {
			return fdate;
		}
		
		public String getRequest() {
			return request;
		}
		
		@Override
		public String toString() {
			return request + " - " + fdate;
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
			return (int) (e1.fdate - e2.fdate);
		}	
	}
	
		
	private Comparator<LFUElement> comparator = new LFUELementComparator();
	private LinkedHashMap<String, LFUElement> cache = new LinkedHashMap<String, LFUElement >();
	private PriorityQueue<LFUElement> queue = new PriorityQueue<LFUElement>(comparator);
	private int cacheSize;
	private int freeSpace;
	private int miss = 0;
	private int hit = 0;
	
	public LFUCache(int cacheSize) {
		this.cacheSize = cacheSize;
	}
	
	public int getHit() {
		return hit;
	}
	
	public int getMiss() {
		return miss;
	}

	public void add(String request, int size) {
		if (cache.containsKey(request)) {
			LFUElement tmp = cache.get(request);
			if(tmp.size == size) {
				hit++;
			} else {
				while(freeSpace < size) {
					LFUElement removeElement = queue.remove();
					freeSpace += removeElement.size;
					cache.remove(removeElement.request);
				}
				freeSpace -= size;
				miss++;
			}
			queue.remove(tmp);
			LFUElement newElement = new LFUElement(tmp.request,tmp.frequency + 1, size);
			cache.put(request, newElement);
			queue.add(newElement);	
			
		} 
		else {
			while(freeSpace < size) {
				LFUElement removeElement = queue.remove();
				freeSpace += removeElement.size;
				cache.remove(removeElement.request);
			}
			
			LFUElement newElement = new LFUElement(request, 1,size);
			queue.add(newElement);
			cache.put(request, newElement);
			freeSpace -= size;
			miss++;
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