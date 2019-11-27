/*--------------------------------------------------------

1. Name / Date: Sojeong Yang /  May.3.2019

2. Java(TM) SE Runtime Environment 18.9 (build 11.0.2+9-LTS)

3. command-line examples:

> javac MyWebServer.java
> java MyWebServer


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

 1)MyWebServer.java
 2)dog.txt
 3)cat.html
 4)addnums.html
 

5. Notes:

The code has lots of comments(especially System.out.println) because I need to debug. 
----------------------------------------------------------*/

import java.io.*;  // Get the Input Output libraries
import java.net.*; // Get the Java networking libraries
import java.util.StringTokenizer;

class MyWebServerWorker extends Thread {  // Class definition
	
	Socket sock;                   // Class member, socket, local to ListnWorker.
	MyWebServerWorker (Socket s) {sock = s;} // Constructor, assign arg s
	final static byte[] EOL = {(byte)'\r',(byte)'\n'};
	
	//to local sock
	public void run(){
		// Get I/O streams from the socket:
		PrintStream out = null;
		BufferedReader in = null;

		try {
			out = new PrintStream(sock.getOutputStream());
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));

			//      String source;
			String fileName = null;
			String contentType = null;
			String fromBrowser;
			
			if(MyWebServer.controlSwitch != true) {
				
				System.out.println("Shut down...");
				
			} else {
				
				fromBrowser = null;
				
				try {

					fromBrowser = in.readLine();
					//      StringTokenizer st = new StringTokenizer(source," "); //break down the source by the " "
					//      String get = st.nextToken();
					System.out.println(fromBrowser); //GET / HTTP/1.1

					fileName = getSource(fromBrowser);
					fileName = fileName.replace("..", "");//take out .. from the filename

					while(true) {
						fromBrowser = in.readLine();
						//System.out.println(fromBrowser);
						
						if(!fileName.contains(".")) {
							showDirectory(out,fileName);
						} else {
							if(!getContentType(fileName).equals("etc")) {
								showServer(fileName, out, sock);
							}
							else if(fileName.split("[.]")[1].contains("cgi")) {
								if(fileName.contains("?")) {
									String[] urlParsed = fileName.split("[?]");
									String num1 = urlParsed[0];
									String num2 = urlParsed[0];
									
									System.out.println("first: "+num1+" second: "+num2);
								}
							}
						}
					}

				} catch(IOException x) {
					System.out.println("Server got an error.");
					x.printStackTrace();
				}
			}
			sock.close(); // close this connection, but not the server;
	
		}catch (IOException x) {
				System.out.println("Server got an error. ");
		}
			
	}
	
	static void showDirectory(PrintStream out, String fileName) throws IOException {
		StringBuilder volume = new StringBuilder();
		StringBuilder top = new StringBuilder();
		
		System.out.println("here!!!");
		
		File file = new File(".");
		String parent = null;
		
		if(fileName.equals("")){
			
			file = new File(".");
			parent = "/";
			
		} else {
			
			file = new File("./"+fileName);
			fileName = "/"+fileName;
			parent = fileName.substring(0, fileName.lastIndexOf("/"));
			
			if(parent.equals(""))
				parent = "/";
			
		}
		
		File[] fileDirs = file.listFiles();
		
		volume.append("<h1> Index of "+fileName+"</h1>");
		volume.append("<pre>");
		volume.append("<a href='"+parent+"'>Index Directory</a><br><br>");
		
		for(int i=0; i<fileDirs.length; i++) {
			if(fileDirs[i].isDirectory()) {
				volume.append("Dir]&nbsp; <a href="+fileDirs[i].toString().substring(1)+">"+
			fileDirs[i].toString().substring(2)+"</a>\n");
			} else if(fileDirs[i].isFile()) {
				volume.append("[File] <a href="+fileDirs[i].toString().substring(1)+">"+
			fileDirs[i].toString().substring(2)+"</a>\n");
			}
		}
		
		volume.append("</pre>");
		String vString = volume.toString();
		int length = vString.getBytes().length;
		
		top.append("HTTP/1.1 200 OK").append(EOL);
		top.append("Content-Length: " + length).append(EOL);
		top.append("Content-Type: text/html").append(EOL);
		top.append(EOL).append(EOL);
		
		
		out.println(top.toString());
		out.println(vString);
		
		
	}	
		
		
		
	
	//check!!!!!!
	static String getSource(String fromBrowser) {
		
	
		
		int bIndex = fromBrowser.indexOf("/"); // get the position of the start of the filename or file path
		int eIndex = fromBrowser.indexOf(" ", bIndex); //get the pos of the end of filename or file path
		
		if(bIndex == 0) {
			System.out.println("no add path");
		}
		else {
		System.out.println("substring: " + fromBrowser.substring(bIndex+1, eIndex));
		}
		String formData = null;

		String url = fromBrowser.substring(bIndex, eIndex); //grab filename or file path
		int lastIndexOfSlash = url.lastIndexOf("/", eIndex); //get the pos of the last instance of a "/"
		int lastIndexOfQuestion;

		//check to see if passing data from file to another
		if((lastIndexOfQuestion = url.lastIndexOf("?", eIndex)) > lastIndexOfSlash)
		{
			//grab data at end of the filename
			formData = url.substring(lastIndexOfQuestion);
			System.out.println("form data" + formData);
		}	
		
		//get filename for file path and append form data
		String path = fromBrowser.substring(bIndex+1, eIndex) + formData;

		//if the http request for a file or folder has a / at the end, strip it off
		if(path.lastIndexOf("/") == path.length())
			path = path.substring(0, path.length()-1);

		System.out.println("Requested file/folder: " + path);
		return path;

	
	  }
	
	
	static String getContentType(String fileName) {
		String str;
		
		if(fileName.indexOf(".") >= 0) {
			
		
			if(fileName.endsWith(".txt")){
				str = "text/plain";
			}
			else if(fileName.endsWith(".html")) {
				str = "text/html";
			}
			else if(fileName.endsWith(".java")) {
				str = "text/plain";
			}
			else {
				str = "etc";
			}
			System.out.println("Content Type: "+ str);

			return str;
		}
		return null;
	}
	
	static void showServer(String fileName, PrintStream out, Socket sock2) {
		
		StringBuilder build = new StringBuilder();
		StringBuilder roof = new StringBuilder();
		String contentType = getContentType(fileName);
		File f = new File(fileName);
		
		if(f.exists()) {
			BufferedReader fr = null;
			String l = null;
			int len = 0;
			FileInputStream img = null;
			//final byte[] EOL = {(byte)'\r',(byte)'\n'};

			try {
				if(contentType.contains("image")) {
					img = new FileInputStream(f);
					len = (int) f.length();
					byte[] display = new byte[30000];

					int imglen = 0;

					roof.append("HTTP/1.1 200 OK"+EOL);
					roof.append("Content-Length: "+ len+ EOL);
					roof.append("Content-Type: "+ contentType+EOL);
					roof.append(EOL);
					
					byte[] roofBytes = roof.toString().getBytes();//get length
					out.write(roofBytes);
					
					while((imglen = img.read(display)) != -1){
						out.write(display, 0, imglen);
					}
					out.flush();
				}
				else {
					fr = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
					
					while((l = fr.readLine()) != null) {
						build.append(l+"\n");
					}
					len = build.toString().getBytes().length;
					
					roof.append("HTTP/1.1 200 OK"+EOL);
					roof.append("Content-Length: "+ len+ EOL);
					roof.append("Content-Type: "+ contentType+EOL);
					roof.append(EOL);
					
		
					out.println(roof.toString()+build.toString());
					System.out.println("HTTP answer : "+roof.toString()+build.toString());
					
					
				
				}
			} catch(IOException ex) {
				System.out.println("There is an IOException.");
			}

		}
	
	}
}


class MyWebServer {

  public static boolean controlSwitch = true;

  public static void main(String args[]) throws IOException {
    int queueLen = 6; /* Number of requests for OpSys to queue */
    int port = 2540;
    Socket socket;

    //@SuppressWarnings("resource")
	ServerSocket servsock = new ServerSocket(port, queueLen);

    System.out.println("Sojeong's WebServer(port: "+port+ ") is running now!\n");
    while (controlSwitch) {
      // wait for the next client connection:
      socket = servsock.accept();
      new MyWebServerWorker (socket).start(); // Uncomment to see shutdown bug:
      // try{Thread.sleep(10000);} catch(InterruptedException ex) {}
    }
  }
}

/*
 * 			//GET / HTTP/1.1
			System.out.println("clientWish.length(): "+clientWish.length());
			if (!clientWish.startsWith("GET") || clientWish.equals("localhost:2540") ||
			*/


