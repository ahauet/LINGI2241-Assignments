import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.registry.LocateRegistry;

public class Task1_2 {

	
	public static void main(String []args) throws IOException{
		
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

		//Length of warm-up phase
		int x = Integer.parseInt(args[0]);
		//Cache size
		int n = Integer.parseInt(args[1]);
		
		double nbProcessedRequest = 0;
		
		LFUCache lfu = new LFUCache(n, x);
		LRUCache lru = new LRUCache(n, x);
		RemoveLargestFirstCache rlf = new RemoveLargestFirstCache(n, x);
		
		
		String s;
		while ((s = in.readLine()) != null && s.length() != 0) {
			String request[] = s.split(" ");
			lfu.add(request[0], Integer.parseInt(request[1]));
			lru.add(request[0], Integer.parseInt(request[1]));
			rlf.add(request[0], Integer.parseInt(request[1]));
			nbProcessedRequest++;
		}
		lfu.writeInFile();
		lru.writeInFile();

		double lruHitRate = lru.getMiss()/nbProcessedRequest * 100;
		double lfuHitRate = lfu.getHit()/nbProcessedRequest * 100;
		
		System.out.println("LRU Hit rate :" + lruHitRate +"%");
		System.out.println("LFU Hit rate :" + lfuHitRate + "%");
		
	}
}