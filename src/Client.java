import java.net.*;
import java.io.*;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Client
{
	private Socket socket;
	private DataOutputStream send;
	private DataInputStream receive;
	private Utility utils;

	public Client(String svrName, int port) throws IOException
	{
		socket = new Socket(svrName, port);
		System.out.println("Just connected to " + socket.getRemoteSocketAddress());

		utils = new Utility();
		send = new DataOutputStream(socket.getOutputStream());
		receive = new DataInputStream(socket.getInputStream());
	}


	private void transfer(String filename) throws IOException {
		// send filename
		utils.Send(false, "transfer", send);
		utils.Send(false, filename, send);

		// get the hash of the file and file content responses
        try {
            int hashcode = Integer.parseInt(utils.GetResponse(true, receive));
            String filecontents = utils.GetResponse(true, receive);

            // if the hash sent from the server does not match the hash computed on the client
            // then file was corrupted in transfer, print an error
            // otherwise file was successfully transferred and will be written to file
            if (hashcode != filecontents.hashCode()) {
                System.out.println("ERROR: File contents are incorrect, they must have been corrupted in transfer");
            } else {
                utils.WriteFile("./Client/" + filename, filecontents);
            }
        } catch (NumberFormatException ex) {
            utils.GetResponse(true, receive);
        }
	}

	private void register(String username, String password) throws IOException {
		// send user and pass params
        utils.Send(false,"register", send);
        utils.Send(true, username, send);
        utils.Send(true, password, send);
        utils.GetResponse(false, receive);
	}

	private void create(String filename) throws IOException {
		// send filename param
		utils.Send(false, "create", send);
		utils.Send(false, filename, send);
		utils.GetResponse(false, receive);
	}

	private void list() throws IOException {
		utils.Send(false, "list", send);
		utils.GetResponse(false, receive);
	}

	private void summary(String filename) throws IOException {
		// send filename param
		utils.Send(false, "summary", send);
		utils.Send(false, filename, send);
		utils.GetResponse(false, receive);
	}

	private void subset(String filename) throws IOException {
		// send filename param
		utils.Send(false, "subset", send);
		utils.Send(false, filename, send);

		// get the hash of the file and file content responses
        try {
            int hashcode = Integer.parseInt(utils.GetResponse(true, receive));
            String filecontents = utils.GetResponse(true, receive);

            // if the hash sent from the server does not match the hash computed on the client
            // then file was corrupted in transfer, print an error
            if (hashcode != filecontents.hashCode()) {
                System.out.println("ERROR: File contents are incorrect, they must have been corrupted in transfer");
            }
        } catch (NumberFormatException ex) {
            utils.GetResponse(true, receive);
        }
	}

	private void delete(String filename) throws IOException {
		// send filename param
		utils.Send(false, "delete", send);
		utils.Send(false, filename, send);
		utils.GetResponse(false, receive);
	}

	private void menu() {
		// print out menu for client
		System.out.print("\n\nChoose an option:\n" +
			"1: register\n" +
			"2: create\n" +
			"3: list\n" +
			"4: transfer\n" +
			"5: summary\n" +
			"6: subset\n" +
			"7: delete\n" +
			"8: close\n\n");
	}

	private void close() {
		try
		{
			// send termination to server so it can cleanly cut the connection
			utils.Send(false,"exit", send);
			System.out.println("Server says " + receive.readUTF());
			// close client side socket
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		Scanner scanner = new Scanner(System.in);
		int choice = 0;
		String filename;
		try
		{
			// while the user hasn't closed the connection
			while (choice != 8)
			{
				menu();
				choice = scanner.nextInt();
				// switch on which choice they make
				switch (choice)
				{
					case 1:
						System.out.println("Please enter username: ");
						String username = scanner.next();
						System.out.println("Please enter password: ");
						String password = scanner.next();
						register(username, password);
						break;
					case 2:
						System.out.println("Please enter filename: ");
						filename = scanner.next();
						create(filename);
						break;
					case 3:
						list();
						break;
					case 4:
						System.out.println("Please enter filename: ");
						filename = scanner.next();
						transfer(filename);
						break;
					case 5:
						System.out.println("Please enter filename: ");
						filename = scanner.next();
						summary(filename);
						break;
					case 6:
						System.out.println("Please enter filename: ");
						filename = scanner.next();
						subset(filename);
						break;
					case 7:
						System.out.println("Please enter filename: ");
						filename = scanner.next();
						delete(filename);
						break;
					case 8:
						close();
						break;
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (InputMismatchException ex) {
		    ex.printStackTrace();
        }
	}

	public static void main(String [] args) {
		try {
			Client client = new Client("10.234.136.56", 7555);
			client.run();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}

