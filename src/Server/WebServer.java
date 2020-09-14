package Server;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.StringTokenizer;


public class WebServer implements Runnable {

	static final File WEB_ROOT = new File(".");
	static final String DEFAULT_FILE = "index.html";
	static final String FILE_NOT_FOUND = "404.html";
	static final String METHOD_NOT_SUPPORTED = "not_supported.html";
	// port to listen connection
	static final int PORT = 8080;
	
	// verbose mode
	static final boolean verbose = true;
	
	// Client Connection via Socket Class
	private Socket connect;
	
	public WebServer(Socket c) {
		connect = c;
	}
	
	public static void main(String[] args) {
		try {
			ServerSocket serverConnect = new ServerSocket(PORT);
			System.out.println("Server started.\nListening for connections on port : " + PORT + " ...\n");
			
			// we listen until user halts server execution
			while (true) {
				WebServer myServer = new WebServer(serverConnect.accept());
				
				if (verbose) {
					System.out.println("Connecton opened. (" + new Date() + ")");
				}
				
				Thread thread = new Thread(myServer);
				thread.start();
			}
			
		} catch (IOException e) {
			System.err.println("Server Connection error : " + e.getMessage());
		}
	}

	@Override
	public void run() {
		InputStreamReader in = null; PrintWriter out = null; BufferedOutputStream dataOut = null;
		String[] headers = null;
		
		try {
			in = new InputStreamReader(connect.getInputStream());
			out = new PrintWriter(connect.getOutputStream());
			dataOut = new BufferedOutputStream(connect.getOutputStream());
			
			HttpRequest req = new HttpRequest(in);
			File file = req.RequestedFile();
			int fileLength = (int) file.length();
			String contentMimeType = getContentType(file.getName());
			headers = getResponseHeaders(file.getName(), contentMimeType, fileLength);
			for(int  i = 0; i < 6; i++ ) {
				out.println(headers[i]);
			}
			out.flush(); // flush character output stream buffer
			dataOut.write(getFileBytes(file, fileLength), 0 ,fileLength);	
			dataOut.flush();					
		} catch (IOException ioe) {
			System.err.println("Server error : " + ioe);
		} finally {
			try {
				in.close();
				out.close();
				dataOut.close();
				connect.close(); // we close socket connection
			} catch (Exception e) {
				System.err.println("Error closing stream : " + e.getMessage());
			} 
			
			if (verbose) {
				System.out.println("Connection closed.\n");
			}
		}		
	}
	
	private String[] getResponseHeaders(String method, String contentType, int fileLength) {
		String[] headers = new String[6];
		switch(method) {
			case METHOD_NOT_SUPPORTED : headers[0] = "HTTP/1.1 501 Not Implemented"; break;
			case FILE_NOT_FOUND : headers[0] = "HTTP/1.1 404 File Not Found"; break;
			default : headers[0] = "HTTP/1.1 200 OK";
		}
		headers[1] = "Server: Java HTTP Server from Sappikatla : 1.0";
		headers[2] = "Date: " + new Date();
		headers[3] = "Content-type: " + contentType;
		headers[4] = "Content-length: " + fileLength;
		headers[5] = "\n";
		return headers;
	}
	
	private String getContentType(String fileRequested) {
		if (fileRequested.endsWith(".htm")  ||  fileRequested.endsWith(".html"))
			return "text/html";
		else
			return "text/plain";
	}
	
	private byte[] getFileBytes(File file, int fileLength) {
		byte[] fileData = null;
		try {
			fileData = readFileData(file, fileLength);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return fileData;
	}
	
	private byte[] readFileData(File file, int fileLength) throws IOException {
		FileInputStream fileIn = null;
		byte[] fileData = new byte[fileLength];
		
		try {
			fileIn = new FileInputStream(file);
			fileIn.read(fileData);
		} finally {
			if (fileIn != null) 
				fileIn.close();
		}
		return fileData;
	}
	
}
