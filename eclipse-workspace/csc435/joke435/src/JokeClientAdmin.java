
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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class JokeClientAdmin {
	
	public static void main(String args[]) {
		String serverName;
		if(args.length < 1) { //nothing wrote(no argument in command line)
			serverName = "localhost";
		} else {
			serverName = args[0]; //the server name that you wrote in command line
		}
		System.out.println("Sojeong's Joke Client Admin! Listening "+serverName+" at port 5050.\n");
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in)); //for input 
		
		try {
			String modeType = ""; //assign mode-type variable

			do {
				System.out.println("\nWhich mode do you like? Please pick the (jokeMode) / (proverbMode), otherwise (quit) to end: ");
				System.out.flush();
				
				boolean inputvalid = false; 
				
				while(!inputvalid) {
					modeType = in.readLine(); //input what you type in the command line
					
					if(modeType.equals("jokeMode")) {
						inputvalid = true;
					}
					else if (modeType.equals("proverbMode")) {
						inputvalid = true;
					}
					else if (modeType.equals("quit")) {
						inputvalid = true;
					}
					passMode("jokeMode", serverName);
				}
				
				passMode(modeType, serverName);
				//System.out.println("You selected "+ select +".");
			}
			while (modeType.indexOf("quit") < 0); 
			System.out.println("The Client requested to quit this. Bye-bye, see you next time!");

		} catch(IOException ex) {
			System.out.println("\nYou got an error. ");
//			System.out.println(ex);
//			ex.printStackTrace();
		}
	}

	//pass the choice of mode from admin to server
	private static void passMode(String modeType, String serverName) {
		Socket socket;
		PrintStream toServer;
		BufferedReader fromServer;
		
		try {
			socket = new Socket(serverName, 5050); //create socket, port number is 5050
			toServer = new PrintStream(socket.getOutputStream());
			//fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			toServer.println(modeType); //send mode type to server
			toServer.flush(); 
			//System.out.println(fromServer.readLine());
			
		} catch(IOException ex) {
			System.out.println("\nSocket got error. You should fix this! ");
//			System.out.println(ex);
//			ex.printStackTrace();
		}
		
	}

}
