import java.net.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Scanner;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class Client
{
	private String svrName = "127.0.0.1";
	private Socket socket = null;
	DataOutputStream send = null;
	DataInputStream receive = null;
	private static SecretKeySpec secretKey = new SecretKeySpec("5u8x/A?D(G+KbPeS".getBytes(), "AES");
	Cipher cipher = null;
	public Client(int port) throws IOException
	{
		socket = new Socket(svrName, port);
		System.out.println("Just connected to " + socket.getRemoteSocketAddress());

		send = new DataOutputStream(socket.getOutputStream());
		receive = new DataInputStream(socket.getInputStream());
	}

	private String ReadFile(String filename) throws IOException {
		return new String(Files.readAllBytes(Paths.get(filename)));
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

	public void requestFile() {
		Send("transfer");
		Send("clientinfo.txt");
		try
		{
			System.out.println(decrypt(receive.readUTF()));
			// write file to Client folder
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void register() {

	}

	public void create() {

	}

	public void list() {

	}

	public void summary() {

	}

	public void subset() {

	}

	public void delete() {

	}

	public void menu() {
		System.out.print("Choose an option:\n" +
			"1: register\n" +
			"2: create\n" +
			"3: list\n" +
			"4: transfer\n" +
			"5: summary\n" +
			"6: subset\n" +
			"7: delete\n" +
			"8: close\n");
	}

	public void close() {
		try
		{
			Send("exit");
			System.out.println("Server says " + receive.readUTF());
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		// menu here which calls each function individually
		Scanner scanner = new Scanner(System.in);
		int choice = 0;
		while (choice != 8) {
			menu();
			choice = scanner.nextInt();
			switch (choice) {
				case 1:
					register();
					break;
				case 2:
					create();
					break;
				case 3:
					list();
					break;
				case 4:
					requestFile();
					break;
				case 5:
					summary();
					break;
				case 6:
					subset();
					break;
				case 7:
					delete();
					break;
				case 8:
					close();
					break;
			}
		}
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
			Client client = new Client(7555);
			client.run();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

