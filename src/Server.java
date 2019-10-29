import java.net.*;
import java.io.*;
import java.util.Map;

public class Server extends Thread
{
	private ServerSocket socket;
	private DataInputStream receive = null;
	private DataOutputStream send = null;
    private Utility utils;
    private Map<String, String> users;


    public Server(int port) throws IOException
	{
		socket = new ServerSocket(port);
        utils = new Utility();

    }

	public static void main(String [] args) {
		try {
			Server svr = new Server(7555);
			svr.run();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void transfer() {
	    try {
            String filename = receive.readUTF();
            File[] files = new File("./Server").listFiles();
            String filestring = null;
            for (File file : files) {
                if (file.getName().equals(filename)) {
                    filestring = utils.ReadFile("./Server/" + filename);
                    utils.EncryptAndSend(filestring, send);
                }
            }

            if (filestring == null) {
                utils.EncryptAndSend("Error: File Not Found", send);
            }
        } catch (IOException ex) {
	        ex.printStackTrace();
        }
    }

    public void register() {
	    String username = utils.GetResponse(true, receive);
	    String password = utils.GetResponse(true, receive);
	    if (!(username.equals("Riley"))) {
	        users.put(username, password);
	        utils.Send("Congratulations!", send);
        } else {
            utils.Send("Error: Credentials are not correct", send);
        }
    }

	public void run()
	{
		while (true)
		{
			try
			{
				System.out.println("Waiting on port " +
					socket.getLocalPort());
				Socket server = socket.accept();

				System.out.println("Connection established with: " + server.getRemoteSocketAddress());

				receive = new DataInputStream(server.getInputStream());
				send = new DataOutputStream(server.getOutputStream());

				String msg;
				boolean keepReceiving = true;
				while(keepReceiving) {
					msg = receive.readUTF();
					System.out.println(msg);

					switch(msg) {
						case "transfer":
							transfer();
							break;
                        case "register":
                            register();
                            break;
						case "exit":
                            keepReceiving = false;
                            send.writeUTF("Goodbye!");
                            server.close();
							break;

					}

				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}

		}
	}
}