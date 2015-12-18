import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

public class Client {
	static int bytesRead;

	public static void main(String[] args) throws Exception {

		Socket socket = new Socket("localhost", 13085);

		//Send to server

		OutputStream outputStream = socket.getOutputStream();
		// Read the image from the file
		BufferedImage image = ImageIO.read(new File("picture2.jpg"));
		// Create the byteArrayOutputStream
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		// Convert BufferedImage to byteArrayOutputStream
		ImageIO.write(image, "jpg", byteArrayOutputStream);
		// Convert size in byte[]
		byte[] size = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();
		// Send size to server
		outputStream.write(size);
		// Send image to server
		outputStream.write(byteArrayOutputStream.toByteArray());
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
		ImageIO.write(outputImage, "jpg", new File("picture1_received2.jpg"));
		System.out.println("close socket");



		socket.close();

	}

}
