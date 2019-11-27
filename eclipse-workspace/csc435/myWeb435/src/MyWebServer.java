/*-------------------------------------------------------------------------------

1. Name / Date: Sojeong Yang /  May.4.2019

2. Java(TM) SE Runtime Environment 18.9 (build 11.0.2+9-LTS)

3. command-line examples:

> javac MyWebServer.java
> java MyWebServer


4. Other Instructions :

The way to retrieve files,

http://localhost:2540
http://localhost:2540/dog.txt
http://localhost:2540/sub1/sub2/



5. Files needed for running the program.

 1)MyWebServer.java
 2)dog.txt
 3)cat.html
 4)addnums.html
 5)dolphin.txt
 6)sub1/sub2/cat.html - - ->(subdirectory)

5. Notes:

You can look up directories and files through this MyWebServer. It displays 
the server's directory at first. Everything works fine. For example,
it shows the mime types correctly and it also shows the addnums web form(fake-cgi) works.
-----------------------------------------------------------------------------------------*/
import java.io.*;
import java.net.*;
import java.util.*;
import java.nio.file.Paths;

//This myWebServWorker takes care of browser request for the server.

class myWebServWorker extends Thread {

	Socket socket;	//assign the socket from the Socket class
	String clientWish; //create a String type client-wish(request)

	public myWebServWorker (String request, Socket socket) //create myWebServer worker constructor
	{
		this.socket = socket; //Constructor-> assign socket to socket
		clientWish = request; //client-wish->request
	}

	public void run(){ //run the myWebServWorker

		BufferedReader in = null; //start with null value
		PrintStream out = null; //start with null value

		try{

			in = new BufferedReader(new InputStreamReader(socket.getInputStream())); //listen and read from the client
			out = new PrintStream(socket.getOutputStream()); //take out stream


			//start: GET / HTTP/1.1 , localhost:2540, length(14) 
			//if there is illegal request or not the same as "localhost:2540"
			if (!clientWish.startsWith("GET") || clientWish.equals("localhost:2540") ||
					!(clientWish.endsWith("HTTP/1.0") || clientWish.endsWith("HTTP/1.1"))) {

				System.out.println(""); //empty string
				System.out.println("Sojeong's Web Server is running now!");
				System.out.println("400(Bad Request): " + clientWish); //type in wrong format
				System.out.println("You typed in malformed syntax"); //show the reason to server
				String errorPg = PageError("400", "Bad Request Error", "The request could not be understood by the server due to malformed syntax.");
				out.println(errorPg);	//show the error browser contents to server

			}
			else {
				//if clientWish -> GET / HTTP/1.1 //get "/"
				String request = clientWish.substring(4, clientWish.length() - 9).trim();//trim the string part			

				if (request.indexOf("..") > -1 || request.indexOf("/.ht") > -1 || request.endsWith("~")) {
					System.out.println("");
					System.out.println("Sojeong's Web Server is running now!");
					System.out.println("403(Forbidden): " + request); //for security
					System.out.println("Your client doesn't have permission to get URL from the server.");//show the reason to server
					String errorPg = PageError("403", "Forbidden", "Your client doesn't have permission to get URL from the server.");
					out.println(errorPg); //show the error browser contents to server

				}
				else {
					if (!request.startsWith("/images/") && !request.endsWith("favicon.ico")) {
						// No printing asking for icon 
						System.out.println("");
						System.out.println("Sojeong's Web Server is running now!");
					}

					request = URLDecoder.decode(request, "UTF-8"); // editing/trimming the URL 									

					if (request.endsWith("/")) {// get rid of the "/" if it has.
						request = request.substring(0, request.length() - 1);
					}

					if (request.indexOf(".") > -1) { // not for directory,for file
						if (request.indexOf(".fake-cgi") > -1) { //if it is asking for "fake-cgi"
							takeAddnum(request, out);
						} else { // if it is just a file, go to the takeFile function
							if (!request.startsWith("/images/")&&!request.startsWith("/favicon.ico")) {
								takeFile(request, out);
							}
						}
					} else { //if it's not just a file, it is directory, go to the takeDisplay function
						takeDisplay(request, out);
					}
				}
			}
			socket.close(); //close the socket!
		}
		catch(IOException ex){
			System.out.println(ex);// check the contents of exception
		}
	}

	//take care of Common Gateway Interface(CGI)
	private void takeAddnum(String ask, PrintStream out) throws UnsupportedEncodingException, MalformedURLException {
		//it is a pair number, using Map
		HashMap<String, String> number = new HashMap<String, String>();

		URL url = new URL("http:/" + ask);//get new url with request(ask)
		String q = url.getQuery();
		String[] nums = q.split("&");
		for (String num:nums) { //iterate nums
			int index = num.indexOf("=");
			number.put(URLDecoder.decode(num.substring(0, index), "UTF-8"), //get the part of num index
					URLDecoder.decode(num.substring(index + 1), "UTF-8"));
		}

		try { 
			// get object type Integer, num1 and num2
			Integer num1 = Integer.parseInt(number.get("num1"));
			Integer num2 = Integer.parseInt(number.get("num2"));

			// if you input the string or other type except the number, give you error sign
			if (num1 == null || num2 == null) {
				String errorPg = PageError("403", "Forbidden", "Your client doesn't have permission to get URL from the server.");
				out.println(errorPg);//show the error browser 
				System.out.println("Invalid parameter. Please enter the integer."); //error message to the server

			} else { //form the contents
				System.out.println("\n"+ number.get("person")+" : "+ num1 + " + " + num2 + " = " + (num1 + num2));
				StringBuilder addn = new StringBuilder();
				addn.append(number.get("person") +" : "); 
				addn.append(number.get("num1") + " + " + number.get("num2") + " = ");
				addn.append(num1 + num2);
				String page = constructPage(addn.toString(), "Addnums");
				String header = constructHeader("addnums.html", page.length());
				out.println(header);
				out.println(page);
			}

		} catch(NumberFormatException n) {
			//out.println("You put in wrong number!");
			//System.out.println("You put in wrong number!");
			String errorPg = PageError("400", "Bad Request Error", "The request could not be understood by the server due to malformed syntax.");
			out.println(errorPg);

		}
	}

	private void takeFile(String request, PrintStream out) throws FileNotFoundException, IOException {
		// you can obtain a file from the root directory
		String root = ""; //empty-string
		try{
			File f = new File("."); //create File f
			root = f.getCanonicalPath();
		}
		catch(IOException ex){ //catch the exception
			ex.printStackTrace();
		}

		// you can obtain a route
		String route = Paths.get(root, request).toString();		
		StringBuilder frame = new StringBuilder(); //create frame to display the contents
		BufferedReader bfrd  = null;

		File f = new File(route); //File f with route

		if (!f.exists() || !f.isFile()) { // If the file is not exists or it is not a file

		} else { 
			String line = null;  //initially set to null(in case, this is a file)
			String head = constructHeader(route, f.length());//go to the function to construct the header
			out.println(head); //show the content to the browser
			InputStream input = new FileInputStream(f);
			byte[] buff = new byte[30000];//give enough size of buffer

			while (input.available() > 0) {//open a file and display  
				out.write(buff, 0, input.read(buff));
			}

			bfrd = new BufferedReader(new InputStreamReader(new FileInputStream(route)));
			while((line = bfrd.readLine()) != null) { //read line by line until it's not null
				frame.append(line+"\n");
			}

			System.out.println(head); //to show the content-length/content-type
			System.out.println(frame.toString()); //to show the contents of the file

		}
	}

	//it gets you to search root directory and files
	private void takeDisplay(String request, PrintStream out)  {

		String root = "";//empty-string
		try{
			File f = new File(".");
			root = f.getCanonicalPath();//returns authoritative pathname
		}
		catch(IOException ex){ //catch the exception
			ex.printStackTrace();
		}

		// change from authoritative pathname to string
		String pathName = Paths.get(root, request).toString();
		//System.out.println("\tpath: "+path);
		
		File f = new File (pathName) ; // when you click the sub-directory/directory
		if (!f.exists()) { // If the file directory does not exist
		} else { 
			System.out.println("\nbinder: " + f.getName());
			
			File[] filesInDirs = f.listFiles(); // Get all the files and sub-directory under current root directory

			StringBuilder dir = new StringBuilder();// cunstruct the html for them 
			dir.append("<table>"); //make the table format
			dir.append("<tr>");
			dir.append("<th><font color = #FF69B4>Index</font></th>"); //show directory index(file list) on the browser
			dir.append("<th><font color = #FF69B4>Size</font></th>"); ////show file size on the browser
			dir.append("</tr>");

			if (!pathName.equals(root)) { // if it's not root directory,
				
				String upper = pathName.substring(0, pathName.lastIndexOf("")); //assign upper path 

				if (upper.equals(root)) { //if it is root directory,
					upper = "root";

				} else { //if it has sub-directory,
					upper = upper.replace(root, "");
				}
				upper = upper.replace("", "/");//change to empty string -> /
				dir.append("<tr>");
				dir.append("<a href=\"" + "http://localhost:2540" + "/\">" + "<font color = #00CED1>Root</font>" + "</a>"); // It make you able to go back to the root under the sub-directory
				dir.append("</tr>");
			}

			ArrayList<File> binders = fileOrDirType(filesInDirs, true); //go to function for directory type
			String fname;
			for (File binder: binders) {

				if (request == null || request.equals("") || request.equals("/")) {
					fname = binder.getName(); 
				} else {
					fname = request + "/" + binder.getName();
				}

				System.out.println("Directory: " + binder.getName()); //to show the server the name of directory
				dir.append("<tr>");
				dir.append("<td><a href=\""+fname+"\">"+ binder.getName()+"</a></td>");				
				dir.append("<td></td>");
				dir.append("</tr>");
			}
			
			ArrayList<File> files = fileOrDirType(filesInDirs, false); //go to function for file type(not directory) 
			for (File file: files) {
				if (request == null || request.equals("") || request.equals("/")) {
					fname = file.getName();
				} else {
					fname = request + "/" + file.getName();
				}

				System.out.println("File: " + file.getName()); //to show the server the name of file
				dir.append("<tr>");
				dir.append("<td><a href=\""+ fname +"\">"+file.getName()+"</a></td>");
				dir.append("<td>" + file.length() + "</td>");
				dir.append("</tr>");
			}

			dir.append("</table>");
			String page = constructPage(dir.toString(), ""); //go to function for setting up the page
			String head = constructHeader(pathName, page.length()); //go to function for setting up the header
			out.println(head);
			out.println(page);
		}
	}

	private String constructHeader(String route, long len) {//to construct the MIME headers
		StringBuilder hd = new StringBuilder();
		hd.append("\nHTTP/1.1 200 OK \r\n");
		hd.append("Content-Length: " + len +"\r\n"); //to show content length
		hd.append("Content-Type: "+ mmType(route)+"\r\n\r\n"); //to show content type(with function to get mime type)
		return hd.toString();
	}

	private String constructPage(String material, String top) { //to construct the page(set up browser version)
		StringBuilder pg = new StringBuilder();
		pg.append("<!DOCTYPE html>");
		pg.append("<html>");
		pg.append("<head>");
		pg.append("<style>");
		pg.append("	table { width:60%; } "); //to give some space
		pg.append("	th, td { padding:7px; text-align:left; }");//to display cell group with padding and align side 
		pg.append("</style>");
		pg.append("</head>");
		pg.append("<body>");
		if (top != null && !top.isEmpty()) {
			pg.append("<h1>" + top + "</h1>");
		}
		else {
			pg.append("<h1><font color = #8A2BE2>Sojeong's Web Server</font></h1>"); //my Web Server title
		}
		pg.append(material);
		pg.append("<hr>");
		pg.append("</body>");
		pg.append("</html>");
		return pg.toString();
	}

	private String PageError(String code, String name, String reason) { //to construct error sign when your input is wrong or security purpose
		StringBuilder pg = new StringBuilder(); //make the error-page with StringBuilder
		pg.append("HTTP/1.1 " + code + " " + name + "\r\n\r\n"); //don't forget the linefeeds
		pg.append("<!DOCTYPE html>");
		pg.append("<html>");
		pg.append("<head>");
		pg.append("<title>" + code + " " + name + "</title>"); //show the error code and error name on header
		pg.append("</head>");
		pg.append("<body>");
		pg.append("<h1>" + code + " " + name + "</h1>"); //show the error code and error name on body part
		pg.append("<p>" + reason + "</p>"); //give to short-explain
		pg.append("<hr>");
		pg.append("</body>");
		pg.append("</html>");
		return pg.toString();
	}

	private ArrayList<File> fileOrDirType(File[] lst, boolean binder) { //after figure out file or directory and get the index
		ArrayList<File> fs = new ArrayList<File>();
		if (lst == null || lst.length == 0) { //if it is file, return file
			return fs;
		}

		for(int i = 0; i < lst.length; i++) { //iterate through list 
			if (lst[i].isDirectory() && binder) {//if it is a directory and it is a binder
				fs.add(lst[i]); //add the list
			} else if (lst[i].isFile() && !binder) { //if it is a file and it's not a binder
				fs.add(lst[i]); 
			}
		}
		return fs;
	}

	private static String mmType(String route)	{ //we can get mime types here.
		if (route == null || route.equals("") || route.lastIndexOf(".") < 0) { //route is null or route is equal to empty string, substring index is not "."
			return "text/html"; 
		} else {

			String build = route.substring(route.lastIndexOf(".")); //get the sub string part behind "."
			switch(build) {

			case ".htm":
			case ".html": //end with htm/.html file -> text/html
				return "text/html";
			case ".txt":  //end with .txt file -> text/plain
				return "text/plain";
			case ".ico":  ////end with .ico file -> img/x-icon .ico
				return "img/x-icon .ico";

			default:
				return "text/plain";
			}
		}
	}
}

public class MyWebServer {

	public static boolean controlButton = true;

	public static void main(String args[]){

		int queueLen = 6; //the length is 6, requests for Operation-Systems to queue
		int port = 2540; // we are using port-number 2540!

		Socket socket; //socket from Socket class

		try{
			
			ServerSocket servsocket = new ServerSocket(port, queueLen);//Create a server socket listening at port number 2540
			System.out.println("Sojeong's WebServer(port: "+ port + ") is running now!\n");

			while(controlButton){
				
				socket = servsocket.accept();// this servsocket is waiting for the next client connection
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); //through the socket, get the input from the client

				// Assign client requests to MyWebServerWorker
				String request = ""; //setting a request to empty string
				String clientWish = ""; //setting a clientWish to  empty string
				
				while ((clientWish = in.readLine()) != null) {//while clientWish is not null(empty string is not null), read
					if (request.equals("")) { //if result is empty string,
						request  = clientWish; //request and clientWish is equal
					}
					if (clientWish.equals("")) { // If clientWish is equal to empty string, 
						break;                   //break the while loop
					}
				}
				if (request != null && !request.equals("")) { //request is not null and request is not empty string, start the worker thread
					new myWebServWorker(request, socket).start();
				}
			}
		}
		catch(IOException ex){ //if there is an error, try/catch.
			System.out.println("There is an error.");
			System.out.println(ex); //print the exception contents.
		}
	}
}
