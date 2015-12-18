import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

public class Client {

	public static void main(String[] args) throws Exception {
		Socket socket = new Socket("localhost", 13085);
    OutputStream outputStream = socket.getOutputStream();

    BufferedImage image = ImageIO.read(new File("picture1.jpg"));

    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    ImageIO.write(image, "jpg", byteArrayOutputStream);

    byte[] size = ByteBuffer.allocate(100).putInt(byteArrayOutputStream.size()).array();
    System.out.println("Send size " + size.length);
    outputStream.write(size);
    outputStream.write(byteArrayOutputStream.toByteArray());
    outputStream.flush();
    System.out.println("close socket");
    socket.close();

	}

}
