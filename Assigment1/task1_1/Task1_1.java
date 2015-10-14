import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class Task1_1 {

	
	public static void main(String []args) throws IOException {
		
		if (args.length != 2) {
			//TODO lancer une erreur ?
		}
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

		

		//Length of warm-up phase
		int x = Integer.parseInt(args[0]);
		//Cache size
		int n = Integer.parseInt(args[1]);
		
		LRUCache LRU = new LRUCache(n);
		int size = 0;
		String s;
		while ((s = in.readLine()) != null && s.length() != 0) {
			String tmp[] = s.split(" ");
			LRU.add(tmp[0]);
			size++;
		}
		System.out.println("Size = "+ size);
		LRU.printHitMiss();
		
		//String[] list = {"A","B", "C", "D", "A", "B", "E", "A", "B", "C" ,"D" ,"E"};
		//String[] list = {"A","A", "A", "D", "A", "B", "E", "A", "B", "C" ,"D" ,"E"};

		//LRUCache LRU = new LRUCache(n);
		
		
		/*for(String s: list) {
			LFU.add(s);
			LFU.print();
		}*/
	}
}


