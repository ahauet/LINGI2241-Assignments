import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

public class Server {



	public static void main(String[] args) throws Exception {
		int bytesRead;
		BufferedImage outputImage = null;
		Socket socket = null;
		ServerSocket serverSocket = null;

		try {
			serverSocket = new ServerSocket(13085);
			while (true) {
				System.out.println("Waiting...");
				try {
					socket = serverSocket.accept();
					InputStream inputStream = socket.getInputStream();

					System.out.println("Reading: " + System.currentTimeMillis());
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
					while(sizeReaded != sizeIn){
						// read the image
						bytesRead = inputStream.read(imageAr, sizeReaded, sizeIn-sizeReaded);

						if(bytesRead >= 0){
							sizeReaded += bytesRead;
						}
					}
					
					System.out.println("size2 :" + imageAr.length);//S imageAr.length

					BufferedImage inputImage = null;
					// change the image in color to black and white
					try {

						inputImage = ImageIO.read(new ByteArrayInputStream(imageAr));
						outputImage = new BufferedImage(inputImage.getWidth(), inputImage.getHeight(),BufferedImage.TYPE_INT_RGB);
						for (int x = 0; x < inputImage.getWidth(); x++) {
							for (int y = 0; y < inputImage.getHeight(); y++) {
								int rgb = inputImage.getRGB(x, y);
								int blue = 0x0000ff & rgb;
								int green = 0x0000ff & (rgb >> 8);
								int red = 0x0000ff & (rgb >> 16);
								int lum = (int) (red * 0.299 + green * 0.587 + blue * 0.114);
								outputImage.setRGB(x, y, lum | (lum << 8) | (lum << 16));
							}
						}

					} catch (IOException e) {
						e.printStackTrace();
					}

					// Send the image

					OutputStream outputStream = socket.getOutputStream();
					// Create the byteArrayOutputStream
					ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
					// Convert BufferedImage to byteArrayOutputStream
					//byteArrayOutputStream = ((DataBufferByte) inputImage.getData().getDataBuffer()).getData();
					ImageIO.write(inputImage, "jpg", byteArrayOutputStream);
					System.out.println("size 4 :"+ byteArrayOutputStream.size());
					// Convert size in byte[]
					byte[] sizeOut = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();
					// Send size to server
					outputStream.write(sizeOut);
					// Send image to server
					outputStream.write(byteArrayOutputStream.toByteArray());
					outputStream.flush();

					System.out.println("Close: " + System.currentTimeMillis());

					System.out.println("close the socket");
				}
				finally {
					if (socket!=null) socket.close();
				}
			}
		}
		finally{
			if (serverSocket != null) serverSocket.close();
		}

	}

}
