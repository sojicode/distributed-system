/*--------------------------------------------------------

1. Name / Date: Sojeong Yang /  Apr.13.2019

2. Java(TM) SE Runtime Environment 18.9 (build 11.0.2+9-LTS)

3. command-line examples:

> javac JokeServer.java
> java JokeServer


4. Other Instructions :

When you choose the mode, you should type in the words without parentheses ().

In separate shell windows:

> java JokeServer
> java JokeClient
> java JokeClientAdmin

All acceptable commands are displayed on the various consoles.

Run JokeClient on the other machine, but instead of typing java JokeClient, 
you’ll add the IP address of the computer running JokeServer.
In my case, the IP address was 192.168.1.67 so typed:
Java JokeClient 192.168.1.67

> java JokeClient 192.168.1.67
> java JokeClientAdmin 192.168.1.67

5. Files needed for running the program.

 ¡.   JokeServer.java
 ¡¡.  JokeClient.java
 ¡¡¡. JokeClientAdmin.java

5. Notes:


The code has lots of comments(especially System.out.println) because I need to debug. 

----------------------------------------------------------*/

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID; //import get library for making unique id



public class JokeClient {
	
	static String jokeId; //for joke
	static String proverbId;//for proverb
	
	public static void main(String args[]) throws IOException {
		
		String serverName;
		
		if(args.length < 1) { //nothing wrote(no argument in command line)
			serverName = "localhost"; // default server name is localhost(127.0.0.1)
		} else {
			serverName = args[0]; //the server name that you wrote in command line
		}

		System.out.println("Sojeong's Joke Client.");
		System.out.println("Using server: "+ serverName + ", Port: 4545\n"); //4545 for the primary server
		//remember - secondary server: 140.192.1.9 port: 4546
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in)); //for input 


		String clientName = null;

		System.out.println("Please write your name...:D "); //show only at first!! Don't ask again.
		System.out.flush();
		clientName = in.readLine(); //read the client name
		System.out.println("\nWelcome, "+ clientName +"!");
		
		
		/* This comments is that I tried something other way to keep tracking jokes and proverbs 
		 * but it didn't work right now but I left because I want to think this more later. 
		 */
		
//		Random rand = new Random();
		
//		jokeOrigin = new int[4];
//		jokeOrigin[0] = 0;
//		jokeOrigin[1] = 0;
//		jokeOrigin[2] = 0;
//		jokeOrigin[3] = 0;
		//System.out.println(jokeOrigin);

//		proverbOrigin = new int[4]; 
//		proverbOrigin[0] = 0;
//		proverbOrigin[1] = 0;
//		proverbOrigin[2] = 0;
//		proverbOrigin[3] = 0;
		//System.out.println(proverbOrigin);
		 
		
		/* I tried to assign the joke-Id BigInteger number but I had problem to send
		 * socket, it need DataInput/OutputStream and it need different application to read but I couldn't figure out yet.
		 * it's easier to make String type id than integer type id .
		 * when we send to string through the socket, it works fine with readLine().
		 */
		
		//assign joke-id here in client side
		jokeId = "0000";
		//System.out.println(jokeId);
		
		//assign proverb-id here in client side
		proverbId = "0000";
		//System.out.println(proverbId);		

		try {
			String selecMode; //to put the information about what we choose for below the question

			do {
				System.out.println("\nI'd like to give you Jokes or Proverbs! If you want to continue, please press enter, or (quit) to end: ");

				System.out.flush();
				selecMode = in.readLine(); //if you press an enter
				pass(selecMode, clientName, serverName); //go to the function with the information(the mode that you select, your-name, server-name)
				//System.out.println("You selected "+ select +".");
			}
			while (selecMode.indexOf("quit") < 0);
			System.out.println("The program is finished. Bye-bye, see you next time!");

		} catch(IOException ex) {
			ex.printStackTrace();
		}
	}
	
	//this function is that using your information and send it to server through the socket 
	public static void pass(String selectMode, String clientName, String serverName) {
		Socket socket = null; //initial socket sets to null
		BufferedReader fromServer; //assign variable for getting the data from the server
		PrintStream toServer; //assign variable for sending the data to Server
		String textFromServer;

		
		try {
			socket = new Socket(serverName, 4545); //open, connect to serverName, port 4545
			
			// this is the I/O pipe!
			fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			toServer = new PrintStream(socket.getOutputStream());

	
			toServer.println(clientName); //send client name (from JokeClient -> to JokeServer)
			toServer.println(selectMode); //send the mode that you picked (from JokeClient -> to JokeServer)
			toServer.println(jokeId);
			toServer.println(proverbId);


			toServer.flush();
			
			textFromServer = fromServer.readLine(); //read from the server(line by line)
			jokeId = fromServer.readLine();
			proverbId = fromServer.readLine();
			//textFromServer = fromServer.readLine();
			
			if(textFromServer != null) {
				System.out.println(textFromServer);
			}
			if(jokeId != null) { //check which joke is
				System.out.println("Joke Id : "+jokeId); 
			}
			if(proverbId != null) { //check which proverb is
				System.out.println("Proverb Id : "+proverbId+"\n");
			}
			
			socket.close(); //close the socket
			
		} catch(IOException ex) {
			System.out.println("There is an an error.");
//			System.out.println(ex);
//			ex.printStackTrace();
		}
		
	}

	//To make portable for 128bit format!
	//this code is also from InetClient.
	static String toText (byte ip[]) { 
		StringBuffer result = new StringBuffer();
		for(int i = 0; i < ip.length; ++i) {
			if(i > 0) {
				result.append(".");
			}
			result.append(0xFF & ip[i]);
		}
		return result.toString();

	}
	
}

