import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;


class AgentWorker extends Thread {
	//global variable in AgentWorker class
	Socket sock; 
	agentHolder parentAgentHolder; 
	int localPort; 
	
	
	AgentWorker (Socket s, int prt, agentHolder ah) {//this is an AgentWorker constructor 
		sock = s;
		localPort = prt;
		parentAgentHolder = ah;
	}
	public void run() {
		
	
		PrintStream out = null; //initial 'out' is null
		BufferedReader in = null; //initial 'in' is null
		String NewHost = "localhost"; //NewHost is localhost
		int NewHostMainPort = 1565; //using port number is 1565
		String buf = ""; //initial buf is empty string
		int newPort; //make variable to hold new port number
		Socket clientSock;
		BufferedReader fromHostServer;
		PrintStream toHostServer;
		
		try {
			out = new PrintStream(sock.getOutputStream()); //take out stream through the socket
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));//take in stream through the socket
			

			String inLine = in.readLine();//create inLine variable and input the string from the socket 
			StringBuilder htmlString = new StringBuilder();	//create htmlString object	
			
			System.out.println(); //add an empty line on the console
			System.out.println("Request line: " + inLine); //ex) Request line: GET /?person=HelloHello HTTP/1.1
			
			if(inLine.indexOf("migrate") > -1) { //if you input "migrate" on the web except the other texts

				clientSock = new Socket(NewHost, NewHostMainPort); //create client socket object with new host name and port number
				fromHostServer = new BufferedReader(new InputStreamReader(clientSock.getInputStream()));
				toHostServer = new PrintStream(clientSock.getOutputStream()); //send to the server 
				
				//display this message on the console
				//ex) Please host me. Send my port! [State=1]
				toHostServer.println("Please host me. Send my port! [State=" + parentAgentHolder.agentState + "]");
				toHostServer.flush();//do it immediately

				for(;;) {
					buf = fromHostServer.readLine(); //read line by line from the hostServer and put it in buf
					if(buf.indexOf("[Port=") > -1) { //if buf include [Port=, break the for loop
						break;
					}
				}
				
				//create the tempbuf, get the number from the port
				String tempbuf = buf.substring( buf.indexOf("[Port=")+6, buf.indexOf("]", buf.indexOf("[Port=")) );

				newPort = Integer.parseInt(tempbuf);//create newPort and convert String to integer type 
				System.out.println("newPort is: " + newPort); //print out the new port number 
				
				//append htmlString with new HTMLheader with new port number, host name and string GET value
				htmlString.append(AgentListener.sendHTMLheader(newPort, NewHost, inLine));
				
				//to display this on the web
				//ex)We are migrating to host 3003
				//ex)View the source of this page to see how the client is informed of the new location.
				htmlString.append("<h3>We are migrating to host " + newPort + "</h3> \n");
				htmlString.append("<h3>View the source of this page to see how the client is informed of the new location.</h3> \n");
				
				//append this html string from sendHTMLsubmit function result
				//ex) <input type="submit" value="Submit"</p>
				//</form></body></html>
				htmlString.append(AgentListener.sendHTMLsubmit());

				System.out.println("Killing parent listening loop."); //display this message on the console
				ServerSocket ss = parentAgentHolder.sock;

				ss.close(); //close the 'ss' socket
				
				
			} else if(inLine.indexOf("person") > -1) { //if inLine include index, person
				
				parentAgentHolder.agentState++;//increments agentState
				
				htmlString.append(AgentListener.sendHTMLheader(localPort, NewHost, inLine));
				htmlString.append("<h3>We are having a conversation with state   " + parentAgentHolder.agentState + "</h3>\n");
				htmlString.append(AgentListener.sendHTMLsubmit());//append this html string(ex: <input type="submit" value="Submit"</p>)

			} else {//if the case is none of them above, 
				
				htmlString.append(AgentListener.sendHTMLheader(localPort, NewHost, inLine));
				htmlString.append("You have not entered a valid request!\n");//display this message about invalid request
				htmlString.append(AgentListener.sendHTMLsubmit());					
			}
	
			AgentListener.sendHTMLtoStream(htmlString.toString(), out); //take the htmlString and streaming 
			
			sock.close(); //close the socket	
			
		} catch (IOException ioe) {//catch the IOException
			System.out.println(ioe); //display exception contents on the console
		}
	}
	
}

class agentHolder {
	//agentHolder grasp state's information.
	ServerSocket sock; //declare sock with ServerSocket class
	int agentState; //declare int type agentState variable
	
	agentHolder(ServerSocket s) { sock = s;} //this is agentHolder constructor
}

class AgentListener extends Thread {

	Socket sock; //declare sock with Socket class
	int localPort; //declare int type localPort variable
	
	
	AgentListener(Socket As, int prt) { //this is AgentListener constructor
		sock = As; //sock equals parameter As
		localPort = prt; //localPort equals parameter prt(prt will be incrementing by one) 
	}
	int agentState = 0; //set the initial agentState value is 0
	
	
	public void run() {
		BufferedReader in = null; //initial BufferedReader 'in' is null
		PrintStream out = null; //initial PrintStream 'out' is null
		String NewHost = "localhost"; //String 'NewHost' set by 'localhost'
		System.out.println("In AgentListener Thread");//print out this on the console	
		try { // (try/catch) statement start
			String buf;//create String variale -> buf
			out = new PrintStream(sock.getOutputStream()); //take out through the socket
			in =  new BufferedReader(new InputStreamReader(sock.getInputStream()));//take-in through the socket	
			buf = in.readLine(); //read line by line and put it to buf variable
			
			if(buf != null && buf.indexOf("[State=") > -1) {//if buf is not null and buf String has "[State="
				//create 'tempbuf' variable
				//get substring begin with index ("[State="+7) and end with the index -> started with "[State=", look for "]"
				String tempbuf = buf.substring(buf.indexOf("[State=")+7, buf.indexOf("]", buf.indexOf("[State=")));
				agentState = Integer.parseInt(tempbuf);//convert 'tempbuf' value to Integer and put to agenState variable
				System.out.println("agentState is: " + agentState); //print out agentState is + number
						
			}
			
			System.out.println(buf); //print out buf string->(GET / HTTP/1.1)
	
			StringBuilder htmlResponse = new StringBuilder();//create htmlResponse object with StringBuilder to put response info
			
			//go to sendHTMLheader function
			//port number, new host name, buf -> string in the buffer
			htmlResponse.append(sendHTMLheader(localPort, NewHost, buf));
			
			//you will see this message on the browser at the first
			htmlResponse.append("Now in Agent Looper starting Agent Listening Loop\n<br />\n");
			htmlResponse.append("[Port="+localPort+"]<br/>\n");//to show the what port number is used
			htmlResponse.append(sendHTMLsubmit()); //append sendHTMLsubmit function result to htmlResponse 
	
			sendHTMLtoStream(htmlResponse.toString(), out); //take the htmlResponse and streaming 
			
			ServerSocket servsock = new ServerSocket(localPort,2);//create new servsock object with localPort(port number could change)
			
			agentHolder agenthold = new agentHolder(servsock);//create agenthold object and hold servsock
			agenthold.agentState = agentState;
			
			while(true) {//while it is true
				sock = servsock.accept(); //it waits for connecting 
				System.out.println("Got a connection to agent at port " + localPort);//show this on the console with port number
	
				new AgentWorker(sock, localPort, agenthold).start(); //create AgentWorker object and start this thread
			}
		
		} catch(IOException ioe) { //catch the IOException
			//if it fails, show this message
			System.out.println("Either connection failed, or just killed listener loop for agent at port " + localPort);
			System.out.println(ioe);//display exception contents on the console
		}
	}

	/**
	 * to build html header for displaying the browser
	 * parameter: port number, new host name, GET value(string) */
	static String sendHTMLheader(int localPort, String NewHost, String inLine) {
		//create htmlString object by StringBuilder to put string with html form
		StringBuilder htmlString = new StringBuilder();

		htmlString.append("<html><head> </head><body>\n"); //no meta info
		htmlString.append("<h2>This is for submission to PORT " + localPort + " on " + NewHost + "</h2>\n");//first large heading
		htmlString.append("<h3>You sent: "+ inLine + "</h3>"); //second line 
		htmlString.append("\n<form method=\"GET\" action=\"http://" + NewHost +":" + localPort + "\">\n");
		htmlString.append("Enter text or <i>migrate</i>:");
		htmlString.append("\n<input type=\"text\" name=\"person\" size=\"20\" value=\"YourTextInput\" /> <p>\n");
		
		return htmlString.toString();
	}

	/**
	 * if we type in text or migrate in the box, the info that we type in build by this html form */
	static String sendHTMLsubmit() {
		return "<input type=\"submit\" value=\"Submit\"" + "</p>\n</form></body></html>\n";
		//for example: <input type="text" name="person" size="20" value="YourTextInput" />
		//</form></body></html>
	}

	static void sendHTMLtoStream(String html, PrintStream out) {
		//function that send reply header
		out.println("HTTP/1.1 200 OK");
		out.println("Content-Length: " + html.length()); //figure out the length(input size)
		out.println("Content-Type: text/html"); //figure out the type of contents that you input
		out.println(""); //to add empty line	
		out.println(html);
	}
	
}

public class HostServer {

	public static int NextPort = 3000; //declare a static variable
	
	public static void main(String[] a) throws IOException {
		int q_len = 6; //length is 6 and it requests for queue
		int port = 1565; //using port number is 1565
		Socket sock;//create a socket 
		
		ServerSocket servsock = new ServerSocket(port, q_len);//create the servsock object and listening the port 1565
		System.out.println("John Reagan's DIA Master receiver started at port 1565.");//print out this on the console
		System.out.println("Connect from 1 to 3 browsers using \"http:\\\\localhost:1565\"\n");//print out this on the console

		while(true) { //while it is true
		
			NextPort = NextPort + 1; //NextPort will increments by 1
			sock = servsock.accept(); //wait for the connection 
			System.out.println("Starting AgentListener at port " + NextPort);//print out this on the console
			new AgentListener(sock, NextPort).start();//create AgentListener with socket and incrementing port number
		}
		
	}
}
