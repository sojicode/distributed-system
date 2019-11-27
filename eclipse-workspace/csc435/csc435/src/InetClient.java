/* File is: InetClient.java
 * A client for InetServer. 
 ------------------------------------------------------------------------------*/


import java.io.*;
import java.net.*;

public class InetClient {
	
	public static void main(String args[]) {
		String serverName;
		if(args.length < 1) {
			serverName = "localhost";
		} else {
			serverName = args[0];
		}

		System.out.println("Sojeong's Inet Client.\n");
		System.out.println("Using server: "+ serverName + ", Port: 1700");
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

		try {
			String name;
			do {
				System.out.println("Enter a hostname or an IP address, (q) to end: ");
				System.out.flush();
				name = in.readLine();
				if(name.indexOf("q") < 0) { //If no such value of indexOf() exists, then -1 is returned.
					getRemoteAddress(name, serverName);
				} 
			}
			while (name.indexOf("q") < 0);
			System.out.println("Cancelled by user request.");

		} catch(IOException x) {
			x.printStackTrace();
		}
	}
	
	static String toText (byte ip[]) { //To make portable for 128bit format!
		StringBuffer result = new StringBuffer();
		for(int i = 0; i < ip.length; ++i) {
			if(i > 0) {
				result.append(".");
			}
			result.append(0xFF & ip[i]);
		}
		return result.toString();
	}
	

	static void getRemoteAddress(String name, String serverName) {
		Socket socket;
		BufferedReader fromServer;
		PrintStream toServer;
		String textFromServer;
		
		try {
			socket = new Socket(serverName, 1700); //open, connect to server
			
			// I/O pipe
			fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			toServer = new PrintStream(socket.getOutputStream());
			
			toServer.println(name);
			toServer.flush();
			
			for(int i = 1; i <= 3; i++) {
				textFromServer = fromServer.readLine(); //read from the server
				if(textFromServer != null) {
					System.out.println(textFromServer);
				}
			}
			socket.close(); //close the socket
		} catch(IOException x) {
			System.out.println("Socket error!");
			x.printStackTrace();
		}
	}
	
}
