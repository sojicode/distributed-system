/* File is : InetServer.java
 * A Multi-threaded server for InetClient.
 * This will not run unless TCP/IP is loaded on your machine.
 -----------------------------------------------------------------------------------*/

import java.io.*;  //For the Input Output 
import java.net.*; //For the Java networking

/* Process the data coming over the connection, according to the mode*/
class Worker extends Thread {
	Socket socket;        //Class member, socket, local to Worker
	Worker(Socket s) {    //Constructor
		socket = s;       //assign s to socket
		}
	
	public void run() {          // get Input/Output streams in/out from the socket
		PrintStream out = null;
		BufferedReader in = null;
		
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintStream(socket.getOutputStream());
			//this branch may not execute when expected
			try {
				String name;
				name = in.readLine(); //read line by line
				System.out.println("Looking up " + name);
				printRemoteAddress(name, out);
			} catch(IOException x) {
				System.out.println("Server read error");
				x.printStackTrace();
			}
			socket.close();  //close this connection, but not close the server
		} catch(IOException ioe) {
			System.out.println(ioe);
		}
	}

	static void printRemoteAddress(String name, PrintStream out) {
		try {
			out.println("Looking up "+ name + " now ...");
			InetAddress machine = InetAddress.getByName(name);
			out.println("Host Name : " + machine.getHostName());
			out.println("Host IP : " + toText(machine.getAddress()));
		} catch(UnknownHostException ex) {
			out.println("Failed in atempt to look up "+ name);
		}
		
	}

	static String toText(byte ip[]) {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < ip.length; ++i) {
			if(i > 0) {
				result.append(".");
			}
			result.append(0xff & ip[i]);
		}
		return result.toString();
	}
}

public class InetServer {
	
	public static void main(String a[]) throws IOException {
		int q_len = 6; //number of requests for Operation Systems to queue
		int port = 1700;
		Socket socket;
		
		//create a server socket listening at port 1700
		ServerSocket serversock = new ServerSocket(port, q_len);
		
		System.out.println("Sojeong's Inet server 1.8 starting up! Listening at port 1700.\n");
		while(true) {
			socket = serversock.accept(); //waiting for the next client connection
			new Worker(socket).start();   //created worker to take care of it
		}
	}

}
