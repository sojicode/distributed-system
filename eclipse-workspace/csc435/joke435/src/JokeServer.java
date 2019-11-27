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
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

class Worker extends Thread {
	Socket socket;        //From Socket class, assign to socket
	Worker(Socket socket) {    //create the Worker Constructor
		this.socket = socket;       //assign this.socket to socket
		}
	
	String jokeId; //create a String type joke-id to keep tracking(which one is which)
	String proverbId; //create a String type proverbId-id
	
	public void run() {          // get the Input and Output streams from the socket
		PrintStream out = null;  //initially, it starts with null value. 
		BufferedReader in = null; //initially, null value before we get something. 
		
		
		try {
			
			in = new BufferedReader(new InputStreamReader(socket.getInputStream())); //get-in through the socket
			out = new PrintStream(socket.getOutputStream()); // get-out through the socket

			
			try {
				String clientName; //create a string type variable to get a name from Client
				clientName = in.readLine(); //server reads the client name, when client write a name in client console, server get the name here
				
				System.out.println(clientName + ","); //show the client name in server console
				
				String selecMode; //create a string type variable to get a mode type from Client 
				selecMode = in.readLine(); //server reads client's choice of mode
				
				jokeId = in.readLine(); //server reads joke-Id from the client
				//System.out.println(jokeId);
				proverbId =in.readLine(); //server reads proverb-Id from the client
			

				//System.out.println(clientName + ","); 
				
				
				if((jokeId.equals("1111")) && (JokeServer.modeCheck == true)) { //this part make work without admin console
				//if((jokeId == "1111")) { //this part make work without admin console
					System.out.println("JOKE CYCLE COMPLETED"); //notice when your joke cycle is finished
					jokeId = "0000";} //change all string to 0000 because 1111 means that you told all jokes(joke cycle)
				
				
				if((proverbId.equals("1111")) && (JokeServer.modeCheck == false))	{
					System.out.println("PROVERB CYCLE COMPLETED"); //notice when your proverb cycle is finished
					proverbId = "0000";} 
				
				if(JokeServer.statement == true) { //switch the original statement false to true
					System.out.println("Bye, this is fine, nothing wrong."); 
				}
				else {
							
						//client admin works together
						//client admin choose joke mode or proverb mode
				
						//below comments for debugging
						//System.out.println("JokeServer.mode is : "+JokeServer.mode);
						//System.out.println("JokeServer.mode is : "+JokeServer.modeCheck);
						//System.out.println("JokeServer.mode.equals joke is : "+JokeServer.mode.equals("joke"));
						
						if (JokeServer.mode.equals("joke") && (JokeServer.modeCheck == true)) {
							//below comments for debugging
							//System.out.println(AdminWorker.passing().indexOf("jokeMode"));
							//System.out.println("JokeServer.mode.equals joke is : "+JokeServer.mode.equals("joke"));
							//System.out.println(("In the Loop, JokeServer.mode is : "+JokeServer.mode));
							out.println(sayJokes(clientName)); //gives jokes to the client 
							out.println(jokeId); //check the joke-id
							out.println(proverbId); //check the proverb-id
							//out.println(sayJokes(clientName));
							//System.out.println("I want to check this. ");
							System.out.println("Jokes start.\n"); //the server can see that joke start with joke-mode
						}
						
//						System.out.println("JokeServer.mode.equals joke is : "+JokeServer.mode.equals("joke"));
//						System.out.println("JokeServer.mode is : "+JokeServer.modeCheck);
						
						else if (JokeServer.mode.equals("proverb") && JokeServer.modeCheck == false) {
							//} else if ((selecMode.length() < 1) && (AdminWorker.passing().indexOf("proverb") < 0)){
							//System.out.println(AdminWorker.passing().indexOf("proverbMode"));
							out.println(sayProverbs(clientName)); //gives proverbs to the client
							out.println(jokeId); //check the joke-id
							out.println(proverbId); //check the proverb-id
							//out.println(sayProverbs(clientName));
							System.out.println("Proverbs start.\n"); //the server can see that proverb start with proverb-mode
						} 
						
//						else
//						{
//							//out.println("HELLO! we should not be here!");
//						}
					
				}

			} catch(IOException x) { //try-catch pair
				System.out.println("Server reads error"+x);
				x.printStackTrace();
			}
			socket.close();  //close this connection, but not close the server
			
		} catch(IOException ioe) { //try-catch pair
			System.out.println("You got some error." +ioe);
			ioe.printStackTrace();
		}
	}

	//when client admin choose joke mode and didn't choose anything (default:joke mode)
	private String sayJokes(String name) {
		

		String joke = null; //create string type joke variable
		boolean jokeMode = true; ////create boolean type mode variable(true:joke-mode, false:proverb-mode)
		//System.out.println(jokeMode);
		
		char[] check = jokeId.toCharArray(); //string type joke-id to convert char array because you need to divide it
		
		//below comments for debugging
		//System.out.println(check);
		//System.out.println("this is working. "+name);
		//System.out.println(jokeMode);

		while(jokeMode) { //while it is joke-mode

			boolean jokeValid = false; //set the boolean value for the while-loop works and not going to infinity-loop
			
			while(!jokeValid) { //until jokeValid is going to be true while it is false


				Random rand = new Random(); //using random function from the library
				int number = rand.nextInt(4); //return random integer x, range (0 <= x < 4) 
				//System.out.println("random number is : "+number); <---for debugging
				//System.out.println(jokeId);

				//using switch statement to lead case by case
				switch(number) {
				case 0 : 
					switch(check[0]) { //checking the first index in joke-id string
					case '0':
						check[0] = '1'; //change the number(after give this joke),so not to give again in same cycle
						jokeId = String.valueOf(check); //get the String type of joke-id back
						//System.out.println(jokeId);//check the joke-id
						joke = "JA "+ name +" : Why is Peter Pan always flying? He neverlands.";

						break;
					}
					break;

				case 1 : 
					switch(check[1]) {
					case '0':
						check[1] = '1';
						jokeId = String.valueOf(check);
						//System.out.println(jokeId);
						joke = "JB "+ name +" : We call freind who love math, algebros.";

						break;
					}
					break;

				case 2 : 
					switch(check[2]) {
					case '0':
						check[2] = '1';
						jokeId = String.valueOf(check);
						//System.out.println(jokeId);
						joke = "JC "+ name +" : I broke my finky last week. On the other hand, I'm fine.";

						break;
					}
					break;

				case 3 : 
					switch(check[3]) {
					case '0':
						check[3] = '1';
						jokeId = String.valueOf(check);
						//System.out.println(jokeId);
						joke = "JD "+ name +" : Parallel lines have so much in common, it's sad they'll never meet.";
						break;
					}
					break;
				}

				if(joke == null) { //until finish the joke cycle
					jokeValid = false; //set the (jokeValid) is true to finish the joke cycle
				} else {
					jokeValid = true; //go back to the while-loop 
				}

			}

			return joke; //return the joke
			
		}
		return null;
		
	}
	
	//when client admin choose proverb mode
	private String sayProverbs(String name) {

		String proverb = null; //create string type proverb variable
		boolean proverbMode = true; //set the boolean value for the while-loop works and not going to infinity-loop
		char[] check = proverbId.toCharArray();

		while(proverbMode) { //while it is proverb-mode

			boolean proverbValid = false; //set the boolean value for the while-loop works and not going to infinity-loop
			
			while(!proverbValid) { //until jproverbValid is going to be true while it is false


				Random rand = new Random();
				int number = rand.nextInt(4); //return integer x, (0 <= x < 4) 

				//using switch statement to lead case by case
				switch(number) {
				case 0 :
					switch(check[0]) {
					case '0': 
						check[0] = '1'; //change the number(after give this proverb),so not to give again in same cycle
						proverbId = String.valueOf(check); ////check the proverb-id
						//System.out.println(proverbId);
						proverb = "PA "+ name +" : A journey of a thousand miles begins with a single step.";

						break;
					}
					break;

				case 1 : 
					switch(check[1]) {
					case '0':
						check[1] = '1';
						proverbId = String.valueOf(check);
						//System.out.println(proverbId);
						proverb = "PB "+ name +" : A ship in the harbor is safe, but that is not what a ship is for";
						break;
					}
					break;

				case 2 : 
					switch(check[2]) {
					case '0':
						check[2] = '1';
						proverbId = String.valueOf(check);
						//System.out.println(proverbId);
						proverb = "PC "+ name +" :  Don’t cross a bridge until you come to it.";

						break;
					}
					break;


				case 3 : 
					switch(check[3]) {
					case '0':
						check[3] = '1';
						proverbId = String.valueOf(check);
						//System.out.println(proverbId);
						proverb = "PD "+ name +" :  Every cloud has a silver lining.";

						break;
					}
					break;
				}
				if(proverb == null) { //until finish the proverb cycle
					proverbValid = false; //set the (proverbValid) is true to finish the proverb cycle
				} else {
					proverbValid = true; ////go back to the while-loop
				}

			}
			return proverb; //return the proverb
		}
		return null;
	}

	
	//this code is from the InetServer.
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


//for client admin thread
class AdminWorker extends Thread {
	
	Socket socket;  //assign socket(from the Socket class)
	static String mode; //assign the string type variable mode and make static so you can use it
	
	public AdminWorker(Socket socket) { //constructor
		this.socket = socket;
	}
	
	public void run() {
		PrintStream out = null;
		BufferedReader in = null;
		
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintStream(socket.getOutputStream());
			
			try {
				//JokeServer.mode = in.readLine(); 
				//String adminMode = in.readLine(); 
				//switchMode(mode);
				//JokeServer.mode = adminMode;
				
				mode = in.readLine(); //got mode type info from the client admin console
				
				out.println(switchMode(mode)); //pass the mode what you pick in the switch-mode function 
				
				
			} catch(IOException e) {
				System.out.println("There are I/O Exception: "+ e);
			}
			
			socket.close(); //close the socket
			
		} catch(IOException e) {
			System.out.println("There are I/O Exception: "+ e);
		}
	}
	
	//for tracking which mode that client choose(jokeMode or proverbMode)
	public static String switchMode(String mode) {
		
		if(mode.indexOf("jokeMode") >= 0) {	//AC changed
			JokeServer.modeCheck = true;
			JokeServer.mode = "joke";
			//System.out.println("Joke mode check: "+mode);
			//System.out.println("JokeServer.mode : "+JokeServer.mode);
			JokeServer.statement = false;
			return "joke";
		} else if (mode.indexOf("proverbMode") >= 0) {	// AC changed
			JokeServer.modeCheck = false;
			JokeServer.mode = "proverb";
			//System.out.println("Proverb mode check: "+mode); //from Admin Client
			JokeServer.statement = false;
			//System.out.println("JokeServer.modeCheck is now : "+JokeServer.modeCheck);
			
			return "proverb"; 
		
//		} else {
//			JokeServer.statement = true;
//			System.out.println("Please write which mode you want.\n");
		} 
		return mode; 
	}
	public static String passing() { //to pass the result, it's not a good logic but I did it.
		return switchMode(mode);
	}
}

//The resource of this code was from joke-threads.html that the professor gave to us.
//Runnable interface
class AdminLooper implements Runnable{
	
	public static boolean adminControlButton = true;
	
	
	public void run() { //run() the ClientAdmin-part listening loop
		
		int q_len = 6; // length for operation system to queue
		int port = 5050; //allocate port number 5050 for Admin-clients
		Socket socket;
		
		try {
			ServerSocket serversock = new ServerSocket(port, q_len);
			while(adminControlButton) {
				socket =  serversock.accept();
				new AdminWorker(socket).start();
			}
		} catch(IOException e) {
			System.out.println("There is a problem to run admin: "+ e);
		}
	}
}

public class JokeServer {
	
	//String defaultMode = "joke";
	
	public static String mode = "joke";
	static boolean modeCheck = true;
	static boolean statement = false;
	

	//the resource of this code was from joke-threads.html that the professor gave to us
	
	public static void main(String args[]) throws IOException {
		
		int q_len = 6;   //length 6: requests for Operation Systems to queue
		int port = 4545; //our default port number
		Socket socket;
		boolean adminControlButton = true;
		
		//new thread to handle the client admin
		AdminLooper admin = new AdminLooper(); //create an another thread to handle clientAdmin
		Thread thread = new Thread(admin);
		thread.start();
		
		//Create a server-socket listening at port 4545
		ServerSocket serversock = new ServerSocket(port, q_len);
		
		System.out.println("Sojeong's Joke server 18.9 starting up! Listening at port 4545.\n");
		
		while(adminControlButton) {
			socket = serversock.accept(); //waiting for the next client connection
			new Worker(socket).start();   //created worker to take care of it
		}
	}
	
}

