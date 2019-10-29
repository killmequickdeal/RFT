import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Client
{
	private String svrName = "127.0.0.1";
	private Socket socket;
	DataOutputStream send;
	DataInputStream receive;
	private Utility utils;

	public Client(int port) throws IOException
	{
		socket = new Socket(svrName, port);
		System.out.println("Just connected to " + socket.getRemoteSocketAddress());

		utils = new Utility();
		send = new DataOutputStream(socket.getOutputStream());
		receive = new DataInputStream(socket.getInputStream());
	}


	public void transfer() {
		utils.Send("transfer", send);
		utils.Send("clientinfo.txt", send);
        utils.GetResponse(true, receive);
        // write file to Client folder ?
        // TODO: check file contents
	}

	public void register(String username, String password) {
        utils.Send("register", send);
        utils.EncryptAndSend(username, send);
        utils.EncryptAndSend(password, send);
        utils.GetResponse(false, receive);
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
			"8: close\n" +
			"9: end program\n");
	}

	public void close() {
		try
		{
			utils.Send("exit", send);
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
		while (choice != 9) {
			menu();
			choice = scanner.nextInt();
			switch (choice) {
				case 1:
                    System.out.println("Please enter username: ");
				    String username = scanner.next();
                    System.out.println("Please enter password: ");
				    String password = scanner.next();
					register(username, password);
					break;
				case 2:
					create();
					break;
				case 3:
					list();
					break;
				case 4:
					transfer();
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

	public static void main(String [] args) {
		try {
			Client client = new Client(7555);
			client.run();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

