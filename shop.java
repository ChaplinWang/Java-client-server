/*
*
* tcpServer part from Kurose and Ross template on UNSW cs3331 couse web
* mainly from Chengbin Wang z3313137
*/

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.StringTokenizer;

public class shop {
	public static void main(String[] args)throws Exception {

		int SHOP_PORT = 1501;
		InetAddress BANK_HOST_IP = null;
		int BANK_PORT = 0;

		/* change above port number this if required */

		//System.out.println(String.valueOf(args[1]));
		if (args.length >= 3){
 	//	System.out.println(args[0] + " " + args[1] + " " + args[2]);
		    SHOP_PORT = Integer.parseInt(args[0]);
		    BANK_HOST_IP = InetAddress.getByName(args[1]);
		    BANK_PORT = Integer.parseInt(args[2]);
		}
		// create server socket
		ServerSocket welcomeSocket = new ServerSocket(SHOP_PORT);
		String requestMessageLine;
		
		
		while (true){

		    // accept connection from connection queue
		    Socket connectionSocket = welcomeSocket.accept();
		    System.out.println("connection from " + connectionSocket);

		    // create read stream to get input
		    BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
		    String clientSentence;
		    clientSentence = inFromClient.readLine();

		    // process input


		//    System.out.println(clientSentence); //!!debug
		    
		    // send reply
		    DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
		    requestMessageLine = clientSentence;
		    StringTokenizer LineBreaker = new StringTokenizer(requestMessageLine);		    									
		    String MsgReader = LineBreaker.nextToken();
		    
		    
		    if (MsgReader.equals("GET")){
		    		
		    		String FileName = LineBreaker.nextToken(" ");
		    		if(FileName.startsWith("/") == true){
		    			FileName = FileName.substring(1);
		    			
		    			FileName = "".concat(FileName);
		    		}
		    		
		    		printPage(FileName, outToClient);
		    		
		    }
		    
		    
		    else if (MsgReader.equals("POST")){
	    		int contentLength;
	    		char[] orderList = new char[1024];
	    		int i = 0;
	    		int sum = 0;
	    		HashMap<String, String> Orderhash = new HashMap<String, String>();
	    		
	    		while(true){
	    			String postContent = inFromClient.readLine();
	    			if(postContent.indexOf("Content-Length:") != -1){
	    				contentLength =  Integer.parseInt(postContent.split(" ")[1]);
	    				postContent = inFromClient.readLine();
	    				break;
	    			}
	    		}
	    		
	    		while(i < contentLength){
	    			char letter = (char)inFromClient.read();
	    			orderList[i++] = letter;
	    		}
	    										//System.out.println(String.valueOf(orderList));
	    			StringTokenizer OrderBreaker = new StringTokenizer(String.valueOf(orderList));
	    			
	    			for(i = 0;i < 10;i++){				//insert order info into hashmap
		    			String OrderReader = OrderBreaker.nextToken("&");
		    									//System.out.println(OrderReader);
		    			StringTokenizer TempBreaker = new StringTokenizer(String.valueOf(OrderReader));
		    			String quantity;
		    			String TempReader = TempBreaker.nextToken("=");
		    			quantity = TempBreaker.nextToken();
		    			Orderhash.put(TempReader, quantity);
	    			}
	    			
	    		//	System.out.println(Orderhash.keySet());

	    			
	    			if(Orderhash.get("service").toLowerCase().equals("delivery")){
		    			StringTokenizer TempBreaker = new StringTokenizer(String.valueOf(fileReader("suburb.txt" , Orderhash.get("postcode"))));
			    		String TempReader = TempBreaker.nextToken().toLowerCase();
			    			
		    			if (TempReader.startsWith(Orderhash.get("postcode")) && Orderhash.get("postcode").length() == 4){
		    				try{
				    			int deliveryfee = Integer.parseInt(TempBreaker.nextToken());
				    			sum = sum + deliveryfee;
				  //  			System.out.println(deliveryfee + Integer.parseInt(Orderhash.get("postcode")) + sum);
				    		}catch (Exception e) {
				    			//do nothing
							}
		    			}else{
		    				printPage("pkonly.html", outToClient);
		    				connectionSocket.close();
		    				break;
		    			}
	
	    			}
	    			
	    			for (String key : Orderhash.keySet()) {
	    				
	        			StringTokenizer TempBreaker = new StringTokenizer(String.valueOf(fileReader("price.txt" , key)));
		    			String TempReader = TempBreaker.nextToken().toLowerCase();
		    												//System.out.println(key + "=" + TempReader);
			    		if(TempReader.startsWith(key)){
			    			try{
			    				int price = Integer.parseInt(TempBreaker.nextToken());
			    				sum = sum + price * Integer.parseInt(Orderhash.get(key));
			    	//			System.out.println(key + price + Integer.parseInt(Orderhash.get(key)) + sum);
			    			}catch (Exception e) {
			    				//do nothing
							}
		    			}
			    		
					}
	    			
	    		//	System.out.println(sum);
	    			ConnectToBank(Orderhash.get("userfirst"), Orderhash.get("userfamily"), Orderhash.get("cardno"), sum, outToClient ,BANK_HOST_IP,BANK_PORT);
	    			outToClient.close();
	    			connectionSocket.close();
	    }


		} // end of while (true)

	} // end of main()
	
	static String fileReader(String filename,String key){
			String t = null;
			 try{
				  FileInputStream tempfile = new FileInputStream(filename);
				  DataInputStream tempdata = new DataInputStream(tempfile);
				  BufferedReader buffer = new BufferedReader(new InputStreamReader(tempdata));
				  
				  while ((t = buffer.readLine()).toLowerCase().startsWith(key) == false);
				  tempdata.close();
				  
			 }catch (Exception error){//Catch exception if any
				 
				 //do nothing
			 }
			return t;
	}
	
	static void printPage(String filename, DataOutputStream outToClient) {
		
		try{
		File file = new File(filename);
		FileInputStream inFileInputStream = new FileInputStream(filename);
		int FileSize = (int) file.length();
		byte[] fileInBytes = new byte[FileSize];
		inFileInputStream.read(fileInBytes);
		outToClient.writeBytes("HTTP/1.0 200 Document Follows\r\n");
				
		outToClient.writeBytes("Content-Length: " + FileSize + "\r\n");
		outToClient.writeBytes("\r\n");
		outToClient.write(fileInBytes, 0, FileSize);
		System.out.println("output closed\n");//debug
		outToClient.close();
		
		}catch (Exception e) {
			try {
				outToClient.writeBytes("HTTP/1.0 404 Not found\r\n");
			} catch (IOException e1) {
			}
			printPage("404.html", outToClient);
			
		}
	}
	
//	InetAddress BANK_HOST_IP = null;
//	int BANK_PORT = 1502;
	
	static void ConnectToBank(String FirstName, String FamilyName, String Cardnumber, int spending, DataOutputStream outToClient,InetAddress BANK_HOST_IP,int BANK_PORT) throws Exception {
		// get server address
		boolean ConnectionEstablished = false;
		Socket clientSocket = null; 

		if (BANK_PORT < 1500){
			BANK_HOST_IP = InetAddress.getLocalHost();
			BANK_PORT = 1502;
		}
		 System.out.println("BANKPORT   :"+BANK_PORT);

		// create socket which connects to server
		while (!ConnectionEstablished){
			try{
				clientSocket = new Socket(BANK_HOST_IP, BANK_PORT); 
				ConnectionEstablished = true;
			}catch(Exception e){
				ConnectionEstablished = false;
			}
		}
						

		// get input 
		String sentence = FirstName.concat("&").concat(FamilyName).concat("&").concat(Cardnumber).concat("&").concat(Integer.toString(spending));


		//byte[] utf8 = sentence.getBytes("UTF8"); //!!!
		
		//String EncodedSentence = new String(utf8, "UTF8");

		//    System.out.println("EncodedSentence = " + EncodedSentence);

	//	System.out.println(sentence);

		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		outToServer.writeBytes(sentence + '\n');
		
		// create read stream and receive from server
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		String sentenceFromServer;
		sentenceFromServer = inFromServer.readLine();

		// print output
		System.out.println("From Server: " + sentenceFromServer);
		if(sentenceFromServer.equals("approved")){
			printPage("approved.html", outToClient);
		}else if(sentenceFromServer.equals("insufficient")){
			printPage("insufficient.html", outToClient);
		}else if(sentenceFromServer.equals("nouser")){
			printPage("nouser.html", outToClient);
		}


		// close client socket
		outToClient.close();
		clientSocket.close();

	}
	
	

} // end of class TCPServer


