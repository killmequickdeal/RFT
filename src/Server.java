import java.net.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class Server extends Thread
{
	private ServerSocket svrSocket;
	private static SecretKeySpec secretKey = new SecretKeySpec("5u8x/A?D(G+KbPeS".getBytes(), "AES");
	Cipher cipher = null;
	DataInputStream receive = null;
	DataOutputStream send = null;

	public Server(int port) throws IOException
	{
		svrSocket = new ServerSocket(port);
	}

	public void Send(String msg) {
		try
		{
			send.writeUTF(msg);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}
	private String ReadFile(String filename) throws IOException
	{
		return new String(Files.readAllBytes(Paths.get(filename)));
	}

	private String EncryptAndEncodeToBase64String(String s) {
		String secretstring = null;
		try
		{
			cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			secretstring = Base64.getEncoder().encodeToString(cipher.doFinal(s.getBytes()));
		} catch (NoSuchPaddingException | NoSuchAlgorithmException | IllegalBlockSizeException
			| BadPaddingException | InvalidKeyException ex) {
			ex.printStackTrace();
		}
		return secretstring;
	}

	public void EncryptAndSend(String msg) {
		try
		{
			send.writeUTF(EncryptAndEncodeToBase64String(msg));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}

	private String decrypt(String s) {
		String plaintextstring = null;

		try
		{
			cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			plaintextstring = new String(cipher.doFinal(Base64.getDecoder().decode(s)));
		} catch (NoSuchPaddingException | NoSuchAlgorithmException | IllegalBlockSizeException
			| BadPaddingException | InvalidKeyException ex) {
			ex.printStackTrace();
		}

		return plaintextstring;
	}
	public static void main(String [] args) {
		try {
			Server svr = new Server(7555);
			svr.run();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run()
	{
		while (true)
		{
			try
			{
				System.out.println("Waiting for client on port " +
					svrSocket.getLocalPort() + "...");
				Socket server = svrSocket.accept();
				final Base64.Decoder decoder = Base64.getDecoder();

				System.out.println("Just connected to " + server.getRemoteSocketAddress());



				receive = new DataInputStream(server.getInputStream());
				send = new DataOutputStream(server.getOutputStream());

				String msg;
				boolean keepReceiving = true;
				while(keepReceiving) {
					msg = receive.readUTF();
					System.out.println(msg);

					switch(msg) {
						case "transfer":
							System.out.println("in transfer");
							String filename = receive.readUTF();
							File[] files = new File("./Server").listFiles();
							String filestring = null;
							for(File file : files) {
								if (file.getName().equals(filename)) {
									filestring = ReadFile("./Server/" + filename);
									EncryptAndSend(filestring);
								}
							}

							if(filestring == null) {
								EncryptAndSend("Error: File Not Found");
							}
							break;

						case "exit":
							System.out.println("in exit");

							keepReceiving = false;
							send.writeUTF("Goodbye!");
							server.close();
							break;

					}

				}
			}
			catch (SocketTimeoutException s)
			{
				System.out.println("Socket timed out!");
				break;
			}
			catch (IOException e)
			{
				e.printStackTrace();
				break;
			}
		}
	}
}