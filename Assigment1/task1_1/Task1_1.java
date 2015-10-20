import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Task1_1 {

	
	public static void main(String []args) throws IOException{
		
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

		//Length of warm-up phase
		int x = Integer.parseInt(args[0]);
		//Cache size
		int n = Integer.parseInt(args[1]);
		
		int nbProcessedRequest = 0;
		
		LFUCache lfu = new LFUCache(n, x);
		LRUCache lru = new LRUCache(n, x);
		
		
		String s;
		while ((s = in.readLine()) != null && s.length() != 0) {
			String request[] = s.split(" ");
			lfu.add(request[0]);
			lru.add(request[0]);
			nbProcessedRequest++;
		}
		
		lfu.writeInFile();
		lru.writeInFile();

		double lruHitRate = Double.parseDouble(Integer.toString(lru.getHit()))/(nbProcessedRequest-x) * 100.0;
		double lfuHitRate = Double.parseDouble(Integer.toString(lfu.getHit()))/(nbProcessedRequest-x) * 100.0;
		
		System.out.println("LRU Hit rate :" + lruHitRate +"%");
		System.out.println("LFU Hit rate :" + lfuHitRate + "%");
		
	}
}