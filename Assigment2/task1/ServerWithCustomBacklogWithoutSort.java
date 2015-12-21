import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

public class ServerWithCustomBacklogWithoutSort {

	private static List<Element> pq;

	public static void main(String[] args) throws IOException {

		ServerSocket server = new ServerSocket(13085);

		pq = new LinkedList<>();

		Thread t1 = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					while(true) {
						Socket socket = server.accept();
						pq.add(new Element(socket));
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		});

		Thread t2 = new Thread(new Runnable() {

			@Override
			public void run() {
				while(true) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					if (!pq.isEmpty()) {
						Element element = pq.remove(0);
						byte[] image = element.getImage();

						BufferedImage inputImage = null;
						BufferedImage outputImage = null;

						// change the image in color to black and white
						try {
							inputImage = ImageIO.read(new ByteArrayInputStream(image));
							outputImage = new BufferedImage(inputImage.getWidth(), inputImage.getHeight(),BufferedImage.TYPE_INT_RGB);
							System.out.println("with" + inputImage.getWidth() + "height" + inputImage.getHeight());
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

							// Send the image

							OutputStream outputStream = element.getClient_socket().getOutputStream();
							// Create the byteArrayOutputStream
							ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
							ImageIO.write(outputImage, "png", byteArrayOutputStream);
							System.out.println("size 4 :"+ byteArrayOutputStream.size());
							// Convert size in byte[]
							byte[] sizeOut = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();
							// Send size to server
							outputStream.write(sizeOut);
							// Send image to server
							outputStream.write(byteArrayOutputStream.toByteArray());
							outputStream.flush();

							if (element.getClient_socket() != null) {
								element.getClient_socket().close();
							}

						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		});

		t1.start();
		t2.start();
	}
}

class Element2 {

	private Socket client_socket;
  private InputStream client_in;
  private OutputStream client_out;
  private byte[] image;

  public Element2(Socket socket) {
		try {
			this.client_socket = socket;
			this.client_in = client_socket.getInputStream();
			this.client_out = client_socket.getOutputStream();
			int bytesRead = 0;
			byte[] sizeAr = new byte[4];
			client_in.read(sizeAr);
			// convert the size in byte[] to int
			int sizeIn = ByteBuffer.wrap(sizeAr).asIntBuffer().get();
			// Image in bite[]
			this.image = new byte[sizeIn];
			// size of the image already read
			int sizeReaded = 0;
			// while the image is not completely read
			while(sizeReaded != sizeIn){
				// read the image
				bytesRead = client_in.read(this.image, sizeReaded, sizeIn-sizeReaded);

				if(bytesRead >= 0){
					sizeReaded += bytesRead;
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

  public byte[] getImage() {
		return image;
	}

  public OutputStream getClient_out() {
		return client_out;
	}

  public Socket getClient_socket() {
		return client_socket;
	}

}
