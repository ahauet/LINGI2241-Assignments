import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
	private static String[] files = {"picture50x32.jpg","picture75x48.jpg", 
			"picture150x96.jpg", "picture250x160.jpg", "picture400x300.jpg",
			"picture640x409.jpg", "picture800x511.jpg", "picture1024x645.jpg", "picture1600x1022.jpg"};
	// Name for the solution of a problem
	private static String[] result_files = {"picture50x32_result.jpg","picture75x48_result.jpg", 
			"picture150x96_result.jpg", "picture250x160_result.jpg", "picture400x300_result.jpg",
			"picture640x409_result.jpg", "picture800x511_result.jpg", "picture1024x645_result.jpg", "picture1600x1022_result.jpg"};

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
		
		
		Socket socket = new Socket("localhost", 13085);

		//Send to server

		OutputStream outputStream = socket.getOutputStream();
		// Read the image from the file
		File fileToSend = new File (files[problem_number-1]);
		byte [] byteArrayToSend  = new byte [(int)fileToSend.length()];
		FileInputStream fileInputStream = new FileInputStream(fileToSend);
	    BufferedInputStream buffuredInputStream = new BufferedInputStream(fileInputStream);
	    buffuredInputStream.read(byteArrayToSend,0,byteArrayToSend.length);
		
		System.out.println(byteArrayToSend.length);
		
		
		//BufferedImage image = ImageIO.read(new File(files[problem_number-1]));
		// Create the byteArrayOutputStream
		//ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		// Convert BufferedImage to byteArrayOutputStream
		//ImageIO.write(image, "jpg", byteArrayOutputStream);
		// Convert size in byte[]
		byte[] size = ByteBuffer.allocate(4).putInt((int)fileToSend.length()).array();
		// Send size to server
		outputStream.write(size);
		// Send image to server
		outputStream.write(byteArrayToSend);
		outputStream.flush();
		
		//Receive to server
		
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

		
		BufferedImage outputImage = ImageIO.read(new ByteArrayInputStream(imageAr));
		ImageIO.write(outputImage, "jpg", new File(result_files[problem_number-1]));
		System.out.println("close socket");



		socket.close();

	}

}
