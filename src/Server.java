import java.net.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Server extends Thread
{
	private Socket server;
	private DataInputStream receive = null;
	private DataOutputStream send = null;
    private Utility utils = new Utility();
    private Map<String, String> users = new HashMap<>();



    public Server(Socket serversocket)
	{
		server = serversocket;
    }

	public static void main(String [] args) {
        try {
            ServerSocket serversocket = new ServerSocket(7555);

            while(true) {
                new Thread(new Server(serversocket.accept())).start();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
//		try {
//			Server svr = new Server(7555);
//			svr.run();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}

	private boolean byzantine() {
		return utils.GetRandom(10) == 0;
	}

	private boolean FileExists(String filename) {
		File[] files = new File("./Server").listFiles();
		boolean fileexists = true;
		for (File file : files)
		{
			if (file.getName().equals(filename))
			{
				fileexists = false;
			}
		}

		return fileexists;
	}

	private void transfer(String filename) {
	    try {
            File[] files = new File("./Server").listFiles();
            String filestring;
            boolean filefound = false;
            for (File file : files) {
                if (file.getName().equals(filename)) {
					filefound = true;
                    filestring = utils.ReadFile("./Server/" + filename);
                    utils.Send(true, Integer.toString(filestring.hashCode()), send);

                    if (byzantine()) {
                    	filestring = utils.GetSubstring(filestring);
					}
                    utils.Send(true, filestring, send);
                }
            }

            if (!filefound) {
                utils.Send(true, "Error: File Not Found", send);
            }
        } catch (IOException ex) {
	        ex.printStackTrace();
        }
    }

	private void register(String username, String password) {
    	// check if registration successful
	    if (!(username.equals("Riley"))) {
	    	// store credentials
	        users.put(username, password);
	        // send congratulation message
	        utils.Send(false, "Congratulations!", send);
        } else {
			// send error message
			utils.Send(false,"Error: Credentials are not correct", send);
        }
    }

	private void create(String filename) {
		// check if file exists
		// if not, create file. If it does exist send error
		if (!FileExists(filename)) {
			utils.WriteFile("./Server/"+filename, utils.CreateRandomContents());
			utils.Send(false,  filename + " created", send);
		} else {
			utils.Send(false, "File " + filename + " already exists", send);
		}
	}

	private void list() {
		File[] files = new File("./Server").listFiles();
		if (files.length == 0) {
			// send error if no files
			utils.Send(false, "No files available on server", send);
		} else {
			String filelist = "";
			for (File file : files) {
				filelist += file.getName() + "\n";
			}
			// send the files that exist
			utils.Send(false, filelist, send);
		}
	}

	private void summary(String filename) throws IOException {
    	//if file exists create the summary
		//otherwise send error
		if (FileExists(filename)) {
			String filestring = utils.ReadFile("./Server/" + filename);
			String[] words = filestring.split("\\s+");
			String[] lines = filestring.split("\n");
			long numlines = lines.length;
			int numwords = words.length;

			utils.Send(false, "Filename: " + filename + "\nLine count: " + numlines + "\nWord count: " + numwords, send);
		} else {
			utils.Send(false, "ERROR: File Not Found", send);
		}
	}

	private void subset(String filename) throws IOException {

    	// if file exists return a subset of the file
		// otherwise send an error
		if (FileExists(filename)) {
			String filestring = utils.GetSubstring(utils.ReadFile("./Server/" + filename));
			utils.Send(true, Integer.toString(filestring.hashCode()), send);
			// if byzantine behavior triggers, get another substring
			if (byzantine()) {
				filestring = utils.GetSubstring(filestring);
			}

			utils.Send(true, filestring, send);

		} else {
			utils.Send(false, "ERROR: File Not Found", send);
		}
	}

	private void delete(String filename) {
    	// if the file exists, delete it
		// if deletion fails send an error
		// if file does not exist send an error
		if (FileExists(filename)) {
			File file = new File("./Server/"+filename);
			if (file.delete())
			{
				utils.Send(false, "File " + filename + " deleted successfully", send);
			} else {
				utils.Send(false, "ERROR: Could not delete file", send);
			}
		} else {
			utils.Send(false, "ERROR: File Not Found", send);
		}
	}

	public void run()
	{
		// loop while server is on
//		while (true)
//		{
//			try
//			{
//				// wait for client connection
//				System.out.println("Waiting on port " +
//					socket.getLocalPort());
//				Socket server = socket.accept();

//				System.out.println("Connection established with: " + server.getRemoteSocketAddress());

				// create input and output streams from socket
            try
            {
                receive = new DataInputStream(server.getInputStream());
                send = new DataOutputStream(server.getOutputStream());
				String msg;
				boolean keepReceiving = true;
				try
				{
					String filename;
					while (keepReceiving)
					{
						msg = utils.GetResponse(false, receive);
						// switch on which action the client requests
						switch (msg)
						{
							case "transfer":
								filename = utils.GetResponse(false, receive);
								transfer(filename);
								break;
							case "register":
								String username = utils.GetResponse(true, receive);
								String password = utils.GetResponse(true, receive);
								register(username, password);
								break;
							case "create":
								filename = utils.GetResponse(false, receive);
								create(filename);
								break;
							case "list":
								list();
								break;
							case "summary":
								filename = utils.GetResponse(false, receive);
								summary(filename);
								break;
							case "subset":
								filename = utils.GetResponse(false, receive);
								subset(filename);
								break;
							case "delete":
								filename = utils.GetResponse(false, receive);
								delete(filename);
								break;
							case "exit":
								keepReceiving = false;
								utils.Send(false, "Goodbye!", send);
								server.close();
								break;
						}
					}
				} catch (SocketException ex) {
					ex.printStackTrace();
				}

			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
//}