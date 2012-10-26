import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;

public class bank {
	public static void main(String[] args)throws Exception {

		
		// see if we do not use default server port
		int serverPort = 1502; 
		/* change above port number this if required */
		
		if (args.length >= 1)
		    serverPort = Integer.parseInt(args[0]);
		// System.out.println(serverPort);
	    
		// create server socket
		ServerSocket welcomeSocket = new ServerSocket(serverPort);
		
		while (true){

		    // accept connection from connection queue
		    Socket connectionSocket = welcomeSocket.accept();
		    System.out.println("connection from " + connectionSocket);

		    // create read stream to get input
		    BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
		    String clientSentence;
		    clientSentence = inFromClient.readLine();

		    // process input


	    

		    // send reply
		    DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());

		    
		    StringTokenizer LineBreaker = new StringTokenizer(clientSentence);
		    										
		    String FirstName = LineBreaker.nextToken("&");
		    String FamilyName = LineBreaker.nextToken("&");
		    String Cardnumber = LineBreaker.nextToken("&");
		    int Spending = Integer.parseInt(LineBreaker.nextToken("&"));
		    
		    Pattern namePattern = Pattern.compile("/[^a-z0-9 ]/gi");
		    
		    
		    										//   System.out.println(FirstName+FamilyName+Cardnumber+Spending);

		    String Info = FirstName.concat("\t").concat(FamilyName).concat("\t").concat(Cardnumber);
		  //  System.out.println(Info);
		    java.util.regex.Matcher match = namePattern.matcher(Info);
		    
		    String matchInfo = String.valueOf(fileReader("credit.txt" , Info));
		//    System.out.println(matchInfo + matchInfo.length());
		    
		    if(FirstName.length() == 0 || FamilyName.length() == 0 || Cardnumber.length() < 8 || match.matches()																				){
		    										//System.out.println("this is a hacker!!!! stop him!!");
		    		outToClient.writeBytes("nouser");
		    }
		    
		    else if (matchInfo.length() == 0 || matchInfo.equals("null")){
		    										//	System.out.println("no info for this client");  //debug
		    	outToClient.writeBytes("nouser");
		    }else{

			    StringTokenizer TempBreaker = new StringTokenizer(matchInfo);
			    String TempReader = TempBreaker.nextToken("\t");
			    TempReader = TempBreaker.nextToken("\t");
			    TempReader = TempBreaker.nextToken("\t");
			    TempReader = TempBreaker.nextToken("\t");
			    
			    int balance =Integer.parseInt(TempReader);
			    TempReader = TempBreaker.nextToken("\t");
			    
			    int credit = Integer.parseInt(TempReader);
			 //   System.out.println(balance + "+" + credit);
			    
			    if(credit < Spending){
			    							System.out.println("insufficient");
			    		outToClient.writeBytes("insufficient\r\n");
			    }else{
			    							System.out.println("approved");
			    		outToClient.writeBytes("approved");
			    		credit = credit - Spending;
			    		balance = balance + Spending;
			    		String newInfo = Info.concat("\t").concat(String.valueOf(balance)).concat("\t").concat(String.valueOf(credit)).concat("\n");

			    		RandomAccessFile f = new RandomAccessFile("credit.txt", "rw");

			    		while (f.readLine().startsWith(Info) == false);

			    		f.seek(f.getFilePointer()-matchInfo.length()-1);
			    		f.writeBytes(newInfo);
			    }
			  
			    
		    
		    
		    }
		    
		    connectionSocket.close();
		
		    

		} // end of while (true)

	}
	
    static String fileReader(String filename,String key){
		String t = null;
		 try{
			  FileInputStream tempfile = new FileInputStream(filename);
			  DataInputStream tempdata = new DataInputStream(tempfile);
			  BufferedReader buffer = new BufferedReader(new InputStreamReader(tempdata));
			  
			  while ((t = buffer.readLine()).startsWith(key) == false);
			  tempdata.close();
			  
		 }catch (Exception error){//Catch exception if any
			 //do nothing
		 }
		return t;
}// end of main()

}

