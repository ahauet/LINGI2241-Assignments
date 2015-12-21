import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

public class Client {
	static int bytesRead;
	// Each file names in this array correspond to a problem 
	// For example : 	file name at index 0 is the problem number 1
	//								file name at index x is the problem number x+1
	private static String[] files = {"50x34.png","75x51.png","105x102.png","300x205.png","700x478.png", "500x341.png"
			,"1000x683.png","1200x820.png","1400x957.png","1600x1094.png","1800x1231.png","2000x1368.png",
			"2400x1641.png","2800x1915.png","3200x2188.png","3600x2462.png	","4000x2735.png","4500x3077.png	"
			,"6000x4103.png","8134x5563.png"};

	public static void main(String[] args) throws Exception {
		
		// Check the usage, we need one argument => the problem number
		if(args.length != 1) {
			System.err.println("Usage : java client problem_number");
			System.exit(-1);
		}
		
		int problem_number = Integer.parseInt(args[0]);
		
		//Check if the problem exist
		if (problem_number < 1 || problem_number > files.length) {
			System.err.println("Error : the problem number must be between 1 and " + files.length);
			System.exit(-1);
		}
		
		System.out.println("Client dificulty : "+args[0]);
		
		long beginTime = System.currentTimeMillis();
		
		// connect to server
		Socket socket = new Socket("localhost", 13085);
		
		
		//////////////////////////
		// Send image to server //
		//////////////////////////

		OutputStream outputStream = socket.getOutputStream();
		// Read the image from the file
		BufferedImage image = ImageIO.read(new File(files[problem_number-1]));
		// Create the byteArrayOutputStream
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		// Convert BufferedImage to byteArrayOutputStream
		ImageIO.write(image, "png", byteArrayOutputStream);
		// Convert size in byte[]
		byte[] size = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();
		// Send size to server
		outputStream.write(size);
		// Send image to server
		outputStream.write(byteArrayOutputStream.toByteArray());
		outputStream.flush();
		
		long timeToSend = System.currentTimeMillis()-beginTime;
		
		
		///////////////////////////////
		// Receive image from server //
		///////////////////////////////
		
		long timeBeforeReceive = System.currentTimeMillis();
		
		InputStream inputStream = socket.getInputStream();
		// size of the in byte[] of the file received
		byte[] sizeAr = new byte[4];
		// read the size
		inputStream.read(sizeAr);
		// convert the size in byte[] to int
		int sizeIn = ByteBuffer.wrap(sizeAr).asIntBuffer().get();
		// Image in bite[]
		byte[] imageAr = new byte[sizeIn];
		// size of the image already read
		int sizeReaded = 0;
		// while the image is not completely read
		while(bytesRead > -1 && sizeReaded != sizeIn){
			// read part by part the image
			bytesRead = inputStream.read(imageAr, sizeReaded, sizeIn-sizeReaded);

			if(bytesRead >= 0){
				sizeReaded += bytesRead;
			}
		}
		long timeToReceive = System.currentTimeMillis()- timeBeforeReceive;
		
		
		BufferedImage outputImage = ImageIO.read(new ByteArrayInputStream(imageAr));
		
		long timeBeforeWriteOnDisk =  System.currentTimeMillis();
		
		ImageIO.write(outputImage, "png", new File("finale_" + files[problem_number-1]));
		
		long timeToWriteOnDisk = System.currentTimeMillis() - timeBeforeWriteOnDisk;
		

		socket.close();
		long totalTimeClient = System.currentTimeMillis() - beginTime;
		// print measurements
		System.out.println("time to send " +timeToSend+" ms");
		System.out.println("Time to reveive :" + timeToReceive + " ms");
		System.out.println("Time to write on disk :" + timeToWriteOnDisk + " ms");
		System.out.println("Total time client :" + totalTimeClient + " ms");

	}

}
